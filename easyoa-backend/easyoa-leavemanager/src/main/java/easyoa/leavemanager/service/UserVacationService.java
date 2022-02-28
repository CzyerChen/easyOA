package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.biz.Application;
import easyoa.leavemanager.domain.dto.ApplicationModifyDTO;
import easyoa.leavemanager.domain.user.UserVacation;
import easyoa.common.domain.vo.UserVacationVO;

/**
 * Created by claire on 2019-07-22 - 10:51
 **/
public interface UserVacationService {
    UserVacation findByUserId(Long userId, int year);

    void saveUserVacartion(UserVacation userVacation);

    Double realizeVacationCache(Application application);

    void removeVacationCache(Application application);

    void cancelVacationCache(Application application);

    UserVacationVO vacation2VO(UserVacation userVacation,int year);

    void rollBackUserVacation(Application application);

    void cancelUserVacation(ApplicationModifyDTO dto);

}
