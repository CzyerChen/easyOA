package easyoa.core.repository.custom;

import easyoa.core.repository.RoleRepository;
import easyoa.common.domain.vo.RoleSearch;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by claire on 2019-06-28 - 14:38
 **/
@Slf4j
public class RoleRepositoryImpl implements RoleRepositoryCustom {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Role> findRolePageBySearParam(RoleSearch roleSearch) {

        Specification<Role> specification = (Specification<Role>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (roleSearch != null) {
                if (StringUtils.isNotBlank(roleSearch.getRoleName())) {
                    predicateList.add(criteriaBuilder.like(root.get("roleName"), '%'+roleSearch.getRoleName()+'%'));
                }

                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(roleSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(roleSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(roleSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(roleSearch.getCreateTimeTo());
                } else {
                    to = new Date(System.currentTimeMillis()+3000);
                }
                predicateList.add(criteriaBuilder.between(root.get("createTime"), from, to));
                predicateList.add(criteriaBuilder.equal(root.get("deleted"), false));
            }
            if(predicateList.size() !=0) {
                Predicate[] predicates = new Predicate[predicateList.size()];
                return criteriaBuilder.and(predicateList.toArray(predicates));
            }else{
                return null;
            }
        };
        if(specification != null) {
            return roleRepository.findAll(specification);
        }else{
            return roleRepository.findAll();
        }
    }

    @Override
    public Page<Role> findRolePageBySearParam(RoleSearch roleSearch, Pageable pageable) {
        Specification<Role> specification = (Specification<Role>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (roleSearch != null) {
                if (StringUtils.isNotBlank(roleSearch.getRoleName())) {
                    predicateList.add(criteriaBuilder.like(root.get("roleName"), '%'+roleSearch.getRoleName()+'%'));
                }

                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(roleSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(roleSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(roleSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(roleSearch.getCreateTimeTo());
                } else {
                    to = new Date(System.currentTimeMillis()+3000);
                }
                predicateList.add(criteriaBuilder.between(root.get("createTime"), from, to));
                predicateList.add(criteriaBuilder.equal(root.get("deleted"), false));
            }
            if(predicateList.size() != 0) {
                Predicate[] predicates = new Predicate[predicateList.size()];
                return criteriaBuilder.and(predicateList.toArray(predicates));
            }else{
                return null;
            }
        };
        if(specification != null) {
            return roleRepository.findAll(specification, pageable);
        }else{
            return roleRepository.findAll(pageable);
        }
    }

    @Override
    public void saveRoleMenu(List<Integer> ids, Integer roleId) {
       for(Integer m : ids){
           String sql = "insert into fb_role_menu(role_id,menu_id) values("+roleId+","+m+")";
           try {
               jdbcTemplate.execute(sql);
           }catch (Exception e){
               log.error("插入role_menu遇到异常,sql{}，异常{}",sql,e);
               throw  new BussinessException("插入role_menu遇到异常");
           }
       }
    }

    @Override
    public void deleteRoleMenu(Integer roleId) {
        String sql = "delete from fb_role_menu where role_id = "+roleId;
        try {
            jdbcTemplate.execute(sql);
        }catch (Exception e){
            log.error("删除role_menu遇到异常",e);
            throw new BussinessException("删除role_menu遇到异常");
        }
    }
}
