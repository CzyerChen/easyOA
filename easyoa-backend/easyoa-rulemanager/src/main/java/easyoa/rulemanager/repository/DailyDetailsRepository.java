package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.DailyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-08 - 17:50
 **/
public interface DailyDetailsRepository extends JpaRepository<DailyDetails,Integer> {
}
