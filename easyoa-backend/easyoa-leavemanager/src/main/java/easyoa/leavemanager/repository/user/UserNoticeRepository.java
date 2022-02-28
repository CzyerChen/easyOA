package easyoa.leavemanager.repository.user;

import easyoa.leavemanager.domain.user.UserNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:09
 **/
public interface UserNoticeRepository extends JpaRepository<UserNotice,Integer> {

    Page<UserNotice> findByUserIdAndCheckedAndTypeOrderByPriorityDesc(Long userId, Boolean checked, String type, Pageable pageable);

    Page<UserNotice> findByUserIdAndType(Long userId,String type, Pageable pageable);

    Page<UserNotice> findByType(String type,Pageable pageable);

    Page<UserNotice> findByTypeAndCheckedOrderByPriorityDesc(String type,Boolean checked,Pageable pageable);

    List<UserNotice> findByUserIdAndCreateDateBetweenOrderByCreateDateDesc(Long userId, LocalDate from ,LocalDate to);
}
