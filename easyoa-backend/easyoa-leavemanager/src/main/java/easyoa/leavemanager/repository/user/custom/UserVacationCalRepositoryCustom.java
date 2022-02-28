package easyoa.leavemanager.repository.user.custom;

import easyoa.leavemanager.domain.user.UserVacationCal;
import easyoa.common.domain.vo.VacationCalSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by claire on 2019-08-07 - 09:48
 **/
public interface UserVacationCalRepositoryCustom {
     Page<UserVacationCal> findPageVacationCalInfo(VacationCalSearch search, Pageable pageable);

}
