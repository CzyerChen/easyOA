package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.user.UserNotice;
import easyoa.common.domain.vo.NoticeVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-16 - 10:02
 **/
public interface UserNoticeService {

    void saveUserNotice(UserNotice userNotice);

    void saveSystemNotice(NoticeVO noticeVO);

    void saveUserNotice(List<UserNotice> list);

    void saveMessage(String applicationMsg,String flowMsg,Long userId,String assignee,String flowName);

    void saveMessageforAssigenee(String userName,Long assignee);

    Page<UserNotice> findValidPersonalUserNotice(Long userId, Pageable pageable);

    Page<UserNotice> findAllPersonalUserNotice(Long userId, Pageable pageable);

    Page<UserNotice>  findValidSystemNotice(Pageable pageable);

    Page<UserNotice>  findAllSystemNotice(Pageable pageable);

    List<NoticeVO> notice2Vo(List<UserNotice> list);

    UserNotice findNoticeById(Integer id);

    List<UserNotice> findNoticesByIds(List<Integer> ids);

    void deleteByIds(List<Integer> ids);

    List<UserNotice> findUserNoticeByUserIdAndTimeBetween(Long userId, LocalDate from, LocalDate to);
}
