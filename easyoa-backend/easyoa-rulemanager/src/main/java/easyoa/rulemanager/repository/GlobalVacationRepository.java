package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.GlobalVacation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-09 - 10:09
 **/
public interface GlobalVacationRepository extends JpaRepository<GlobalVacation,Integer> {
    List<GlobalVacation> findByNameAndStartDate(String name, LocalDate startDate);

    List<GlobalVacation> findByNameAndFinish(String name, boolean finish);
}
