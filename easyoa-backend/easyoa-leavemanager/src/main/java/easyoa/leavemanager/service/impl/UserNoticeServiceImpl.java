package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.user.UserNotice;
import easyoa.leavemanager.service.UserNoticeService;
import easyoa.common.constant.MessageTypeEnum;
import easyoa.common.domain.vo.NoticeVO;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.repository.user.UserNoticeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-16 - 10:03
 **/
@Service(value = "userNoticeService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class UserNoticeServiceImpl implements UserNoticeService {
    @Autowired
    private UserNoticeRepository userNoticeRepository;

    @Override
    public void saveUserNotice(UserNotice userNotice) {
        userNoticeRepository.save(userNotice);
    }

    @Override
    public void saveSystemNotice(NoticeVO noticeVO) {
        UserNotice userNotice = new UserNotice();
        userNotice.setChecked(false);
        userNotice.setSender("System");
        userNotice.setSendTime(LocalDateTime.now());
        if(StringUtils.isNotBlank(noticeVO.getTitle())) {
            userNotice.setTitle(noticeVO.getTitle());
        }
        userNotice.setPriority(0);
        if(StringUtils.isNotBlank(noticeVO.getMsgContent())) {
            userNotice.setMessage(noticeVO.getMsgContent());
        }
        userNotice.setType(MessageTypeEnum.SYSTEM.toString());
        userNotice.setCreateDate(LocalDate.now());
        saveUserNotice(userNotice);
    }

    @Override
    public void saveUserNotice(List<UserNotice> list) {
         userNoticeRepository.saveAll(list);
    }

    @Override
    public void saveMessage(String applicationMsg, String flowMsg,Long userId,String assignee,String flowName) {
        UserNotice userNotice = new UserNotice();
        userNotice.setUserId(userId);
        userNotice.setPriority(0);
        userNotice.setSender(assignee);
        userNotice.setSendTime(LocalDateTime.now());
        userNotice.setChecked(false);
        userNotice.setTitle(flowName+ "结果通知");
        userNotice.setType(MessageTypeEnum.PERSONAL.toString());
        userNotice.setMessage("申请单消息: "+(null==applicationMsg? "无; ":applicationMsg) +"  流程消息: "+(null==flowMsg?"无":flowMsg));
        userNotice.setCreateDate(LocalDate.now());
        saveUserNotice(userNotice);
    }

    @Override
    public void saveMessageforAssigenee(String userName, Long assignee) {
        UserNotice userNotice = new UserNotice();
        userNotice.setChecked(false);
        userNotice.setSender(userName);
        userNotice.setSendTime(LocalDateTime.now());
        userNotice.setTitle("审批任务通知");
        userNotice.setPriority(0);
        userNotice.setUserId(assignee);
        userNotice.setMessage("审批人员您好，您有来自"+userName+"的申请信息，请及时处理");
        userNotice.setType(MessageTypeEnum.PERSONAL.toString());
        userNotice.setCreateDate(LocalDate.now());
        saveUserNotice(userNotice);
    }

    @Override
    public Page<UserNotice> findValidPersonalUserNotice(Long userId, Pageable pageable) {
        return userNoticeRepository.findByUserIdAndCheckedAndTypeOrderByPriorityDesc(userId,false, MessageTypeEnum.PERSONAL.toString(),pageable);
    }

    @Override
    public Page<UserNotice> findAllPersonalUserNotice(Long userId, Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "checked"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "priority"));
        PageRequest of = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders));

        return userNoticeRepository.findByUserIdAndType(userId,MessageTypeEnum.PERSONAL.toString(),of);
    }

    @Override
    public Page<UserNotice> findValidSystemNotice(Pageable pageable) {
        return userNoticeRepository.findByTypeAndCheckedOrderByPriorityDesc(MessageTypeEnum.SYSTEM.toString(),false,pageable);
    }

    @Override
    public Page<UserNotice> findAllSystemNotice(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "checked"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "priority"));
        PageRequest of = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), new Sort(orders));
        return userNoticeRepository.findByType(MessageTypeEnum.SYSTEM.toString(),of);
    }

    @Override
    public List<NoticeVO> notice2Vo(List<UserNotice> list) {
        if(list != null && list.size() != 0){
            return list.stream().map(n -> {
                NoticeVO vo = new NoticeVO();
                vo.setNoticeId(n.getId());
                if (StringUtils.isNotBlank(n.getTitle())) {
                    vo.setTitle(n.getTitle());
                }
                if (StringUtils.isNotBlank(n.getMessage())) {
                    vo.setMsgContent(n.getMessage());
                }
                if (null !=n.getPriority()) {
                    vo.setPriority(n.getPriority() ==2?"H":(n.getPriority()==1?"M":"L"));
                }
                if (StringUtils.isNotBlank(n.getSender())) {
                    vo.setSender(n.getSender());
                }
                if (null != n.getCreateDate()) {
                    vo.setCreateTime(DateUtil.formatDate(n.getCreateDate(), DateUtil.DATE_PATTERN_WITH_HYPHEN));
                }
                if (null != n.getSendTime()) {
                    vo.setSendTime(DateUtil.format(n.getSendTime()));
                }
                if(null != n.getChecked()){
                    vo.setChecked(n.getChecked());
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public UserNotice findNoticeById(Integer id) {
        return userNoticeRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserNotice> findNoticesByIds(List<Integer> ids) {
        return userNoticeRepository.findAllById(ids);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        List<UserNotice> all = userNoticeRepository.findAllById(ids);
        if(all != null && all.size() !=0){
            userNoticeRepository.deleteAll(all);
        }
    }

    @Override
    public List<UserNotice> findUserNoticeByUserIdAndTimeBetween(Long userId, LocalDate from, LocalDate to) {
        return userNoticeRepository.findByUserIdAndCreateDateBetweenOrderByCreateDateDesc(userId, from, to);
    }
}
