package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.dto.UserVacationCalDTO;
import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.common.domain.vo.VacationCalSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-08-07 - 09:05
 **/
public interface UserVacationCalService {
    List<UserVacationCal> findAll();

    Page<UserVacationCalDTO> findPageUserVacationCal(VacationCalSearch vacationCalSearch, Pageable pageable);

    UserVacationCal findByUserIdAndMonth(Long userId, LocalDate month);

    void cancelUserVacationCal(ApplicationModifyDTO dto);
}
