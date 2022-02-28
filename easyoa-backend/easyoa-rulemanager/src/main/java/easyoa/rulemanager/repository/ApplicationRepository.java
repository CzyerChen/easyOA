package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by claire on 2019-07-17 - 09:36
 **/
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    List<Application> findByUserIdAndCreateTimeBetween(Long userId, LocalDateTime begin, LocalDateTime end);

    List<Application> findByUserIdAndStatusAndApplicateTypeAndCreateTimeBetweenOrderByCreateTime(Long  userId, String status, String applicationType,LocalDateTime start ,LocalDateTime end);

    List<Application> findByUserIdAndStatusAndApplicateTypeAndStartTimeBetween(Long  userId, String status, String applicationType,LocalDateTime start ,LocalDateTime end);


}
