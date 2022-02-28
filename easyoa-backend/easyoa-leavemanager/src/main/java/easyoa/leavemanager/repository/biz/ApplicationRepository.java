package easyoa.leavemanager.repository.biz;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.repository.biz.custom.ApplicationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:08
 **/
public interface ApplicationRepository extends JpaRepository<Application,Long>, JpaSpecificationExecutor<Application>, ApplicationRepositoryCustom {

    List<Application> findByUserIdAndApplicateType(Long userId,String applicationType);

    Page<Application> findByUserIdAndApplicateTypeOrderByCreateTimeDesc(Long userId,String applicationType, Pageable pageable);

    List<Application> findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(Long  userId, String status, String applicationType,LocalDateTime start ,LocalDateTime end);

    List<Application> findByUserIdAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(Long  userId, String applicationType,LocalDateTime start ,LocalDateTime end);

    List<Application> findByUserIdAndStatusAndApplicateTypeOrderByCreateTime(Long  userId, String status, String applicationType);

    List<Application> findByUserIdAndStatusAndApplicateTypeAndStartTimeBeforeAndEndTimeAfter(Long userId,String status,String applicationType,LocalDateTime from,LocalDateTime to);

    List<Application> findByStatusInAndIdIn(List<String> statuses,List<Long> ids);
}
