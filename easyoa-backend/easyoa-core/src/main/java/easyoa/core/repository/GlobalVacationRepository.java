package easyoa.core.repository;

import easyoa.core.domain.po.GlobalVacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-09 - 10:09
 **/
public interface GlobalVacationRepository extends JpaRepository<GlobalVacation,Integer> {

    List<GlobalVacation> findByNameAndStartDateAndFinish(String name, LocalDate startDate, boolean finish);

    List<GlobalVacation> findByFinish(boolean finish);

    Page<GlobalVacation> findByFinish(boolean finish, Pageable pageable);

    Page<GlobalVacation> findByYear(int year, Pageable pageable);

    List<GlobalVacation> findByFinishAndStartDateBetween(boolean finish, LocalDate start , LocalDate end);

    GlobalVacation findByFinishAndStartDate(boolean finish, LocalDate date);
}
