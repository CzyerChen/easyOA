package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.GlobalFlow;
import easyoa.leavemanager.domain.GlobalFlowDTO;
import easyoa.leavemanager.service.CacheService;
import easyoa.leavemanager.service.GlobalFlowService;
import easyoa.leavemanager.utils.TreeUtil;
import easyoa.common.domain.vo.GlobalFlowVO;
import easyoa.common.utils.DateUtil;
import easyoa.core.model.TreeNode;
import easyoa.core.service.UserService;
import easyoa.leavemanager.repository.biz.GlobalFlowRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-12 - 13:54
 **/
@Slf4j
@Service(value = "globalFlowService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class GlobalFlowServiceImpl implements GlobalFlowService {
    @Autowired
    private GlobalFlowRepository globalFlowRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CacheService cacheService;


    @Override
    public Map<String, Object> getGlobalFlowInfo(Integer companyId) {
        Map<String, Object> map = new HashMap<>();

        List<GlobalFlow> parentFlows = findByParentIdAndCompanyId(0,companyId);
        List<String> fullIds = new ArrayList<>();
        try {
            if (parentFlows != null) {
                List<TreeNode<GlobalFlowVO>> fulltrees = new ArrayList<>();
                parentFlows.forEach(p -> {
                    List<GlobalFlow> chidrenFlows = findByRootAndCompanyId(p.getId(),companyId);
                    if (chidrenFlows != null) {
                        chidrenFlows.add(p);
                        List<TreeNode<GlobalFlowVO>> trees = new ArrayList<>();
                        List<String> ids = new ArrayList<>();

                        flowToTree(trees, ids, chidrenFlows);
                        fullIds.addAll(ids);
                        fulltrees.addAll(trees);
                    }
                });

                TreeNode<GlobalFlowVO> treeNode = TreeUtil.buildTree(fulltrees);
                map.put("ids", fullIds);
                map.put("rows", treeNode);
                map.put("total", fullIds.size());
            }
        } catch (Exception e) {
            log.error("查询流程失败", e);
            map.put("rows", null);
            map.put("total", 0);
        }
        return map;
    }

    @Override
    public Integer generateRoot(Integer parentId) {
        Optional<GlobalFlow> parent = globalFlowRepository.findById(parentId);
        if (parent.isPresent()) {
            GlobalFlow globalFlow = parent.get();
            return -1 == globalFlow.getRoot() ? globalFlow.getId() : globalFlow.getRoot();
        }
        return null;
    }

    @Override
    public void saveGlobalFlow(GlobalFlow globalFlow) {
        globalFlowRepository.save(globalFlow);

        refreshFlowCache(globalFlow.getCompanyId());
    }

    @Override
    public void updateChildFlow(Integer parentId, boolean deleted) {
        ArrayList<Integer> ids = new ArrayList<>(Arrays.asList(parentId));
        List<GlobalFlow> list = findAllFlowByParentId(ids);
        List<GlobalFlow> flows = list.stream().filter(f -> f.getDeleted() != deleted).collect(Collectors.toList());
        if (flows.size() != 0) {
            flows.forEach(f -> f.setDeleted(deleted));
            globalFlowRepository.saveAll(flows);
        }
    }


    public List<GlobalFlow> findAllFlowByParentId(List<Integer> parentIds) {
        List<GlobalFlow> parents = globalFlowRepository.findByParentIdIn(parentIds);
        if (parents == null || parents.size() == 0) {
            return new ArrayList<>();
        }

        List<Integer> list = parents.stream().map(GlobalFlow::getId).distinct().collect(Collectors.toList());
        List<GlobalFlow> flowList = findAllFlowByParentId(list);
        flowList.addAll(parents);
        return flowList;
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        List<GlobalFlow> parent = globalFlowRepository.findAllById(ids);
        List<GlobalFlow> all = new ArrayList<>();
        if (parent != null & parent.size() != 0) {
            all.addAll(parent);
            List<GlobalFlow> children = globalFlowRepository.findByParentIdIn(ids);
            if (children != null) {
                all.addAll(children);
            }
        }
        if (all != null) {
            globalFlowRepository.deleteAll(all);
        }
    }

    @Override
    public List<GlobalFlowDTO> findEffectiveFlow(Integer companyId) {
        GlobalFlow flow = globalFlowRepository.findFirstByRootAndDeletedAndCompanyId(-1, false,companyId);
        if(flow != null) {
            ArrayList<Integer> ids = new ArrayList<>(Collections.singletonList(flow.getId()));
            List<GlobalFlow> flows = findAllFlowByParentId(ids);
            flows.add(flow);
            return flow2Vo(flows);
        }
        return  null;
    }

    @Override
    public GlobalFlow findByParentId(Integer parentId) {
        List<GlobalFlow> list = globalFlowRepository.findByParentId(parentId);
        if(list!= null){
            return list.get(0);
        }
        return  null;
    }

    @Override
        public GlobalFlow findById(Integer flowId) {
        return globalFlowRepository.findById(flowId).orElse(null);
    }

    @Override
    public GlobalFlow findFirstByAssigneeNotLike(Integer root,Long userId,Integer parentFlowId) {
        List<GlobalFlow> list = globalFlowRepository.findByRootAndDeletedAndAssigneeIdsNotLike(root,false,"%"+userId+"%");
        if(list !=null && list.size() != 0){
            List<GlobalFlow> flows = list.stream().filter(f -> f.getId() > parentFlowId).sorted(Comparator.comparingInt(GlobalFlow::getId)).collect(Collectors.toList());
            return flows.get(0);
        }

        return null;
    }

    @Override
    public GlobalFlow findLastFlowByRoot(Integer root) {
        return globalFlowRepository.findFirstByRootAndDeletedOrderByIdDesc(root,false);
    }

    @Override
    public Integer getRestFlowCount(Integer flowId, Integer root) {
        List<GlobalFlow> list = globalFlowRepository.findByDeletedAndRootAndIdGreaterThan(false, root, flowId);
        if(list != null && list.size() != 0){
            return list.size();
        }
        return null;
    }

    @Override
    public void refreshFlowCache(Integer companyId) {
        List<GlobalFlowDTO> effectiveFlow = findEffectiveFlow(companyId);
        if (effectiveFlow != null && effectiveFlow.size() != 0) {
            RList<GlobalFlowDTO> flowList = cacheService.getFlowList(companyId);
            if (!flowList.isEmpty()) {
                flowList.clear();
                flowList.addAll(effectiveFlow);
            } else {
                flowList.addAll(effectiveFlow);
            }
        }
    }

    @Override
    public void cacheAllFlow() {
        List<Integer> activeCompanys = userService.getActiveCompanys();
        for(Integer companyId:activeCompanys) {
            List<GlobalFlowDTO> effectiveFlow = findEffectiveFlow(companyId);
            if (effectiveFlow != null && effectiveFlow.size() != 0) {
                RList<GlobalFlowDTO> flowList = cacheService.getFlowList(companyId);
                if (!flowList.isEmpty()) {
                    flowList.clear();
                    flowList.addAll(effectiveFlow);
                } else {
                    flowList.addAll(effectiveFlow);
                }
            }
        }
    }

    private List<GlobalFlowDTO> flow2Vo(List<GlobalFlow> list) {
        if (list != null) {
           return list.stream().map(f -> {
                GlobalFlowDTO dto = new GlobalFlowDTO();
                dto.setId(f.getId());
                if (StringUtils.isNotBlank(f.getAssigneeIds())) {
                    dto.setAssigneeIds(f.getAssigneeIds());
                }
                if (StringUtils.isNotBlank(f.getContent())) {
                    dto.setContent(f.getContent());
                }
                if (StringUtils.isNotBlank(f.getName())) {
                    dto.setName(f.getName());
                }
                if (null != f.getParentId()) {
                    dto.setParentId(f.getParentId());
                }
                if (null != f.getRoot()) {
                    dto.setRoot(f.getRoot());
                }
                if (null != f.getDeleted()) {
                    dto.setDeleted(f.getDeleted());
                }
                if (null != f.getLevel()) {
                    dto.setLevel(f.getLevel());
                }
                if (null != f.getTotal()) {
                    dto.setTotal(f.getTotal());
                }
                if(Objects.nonNull(f.getCompanyId())){
                    dto.setCompanyId(f.getCompanyId());
                }
                return dto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private void flowToTree(List<TreeNode<GlobalFlowVO>> treeNodes, List<String> ids, List<GlobalFlow> list) {
        list.forEach(l -> {
            ids.add(l.getId().toString());
            TreeNode<GlobalFlowVO> node = new TreeNode<>();
            GlobalFlowVO vo = new GlobalFlowVO();
            BeanUtils.copyProperties(l,vo);
            vo.setAssigneeIds(StringUtils.isNotBlank(l.getAssigneeIds())?Arrays.stream(l.getAssigneeIds().split(",")).map(Integer::valueOf).collect(Collectors.toList()):null);
            vo.setParantId(l.getParentId().toString());

            node.setId(l.getId().toString());
            node.setKey(node.getId());
            node.setParentId(l.getParentId().toString());
            node.setText(l.getName());
            node.setTitle(l.getContent());
            node.setCreateTime(DateUtil.formatDate(l.getCreateTime(), DateUtil.DATE_PATTERN_WITH_HYPHEN));
            node.setUpdateTime(DateUtil.formatDate(l.getUpdateTime(), DateUtil.DATE_PATTERN_WITH_HYPHEN));
            node.setStatus(l.getDeleted());
            node.setGroupName(l.getCompanyId().toString());
            node.setEntity(vo);

            if (StringUtils.isNotBlank(l.getAssigneeIds())) {
                List<String> users = Arrays.asList(l.getAssigneeIds().split(","));
                StringBuilder sb = new StringBuilder("");
                users.stream().map(Long::valueOf).forEach(u -> {
                    String name = userService.getUserNameById(u);
                    if (StringUtils.isNotBlank(name)) {
                        sb.append(name + " ");
                    }
                });
                node.setAssigneeNames(sb.toString());
            }
            treeNodes.add(node);
        });
    }

    private List<GlobalFlow>  findByParentIdAndCompanyId(Integer parentId,Integer companyId){
        return globalFlowRepository.findAll((Specification<GlobalFlow>) (root, cq, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.equal(root.get("parentId").as(Integer.class), parentId));
            if (Objects.nonNull(companyId)) {
                predicateList.add(cb.equal(root.get("companyId").as(Integer.class), companyId));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            pre = predicateList.toArray(pre);
            return cq.where(pre).getRestriction();
        });
    }

    private List<GlobalFlow> findByRootAndCompanyId(Integer rootPid,Integer companyId){
        return globalFlowRepository.findAll((Specification<GlobalFlow>) (root, cq, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.equal(root.get("root").as(Integer.class), rootPid));
            if (Objects.nonNull(companyId)) {
                predicateList.add(cb.equal(root.get("companyId").as(Integer.class), companyId));
            }
            Predicate[] pre = new Predicate[predicateList.size()];
            pre = predicateList.toArray(pre);
            return cq.where(pre).getRestriction();
        });
    }

}
