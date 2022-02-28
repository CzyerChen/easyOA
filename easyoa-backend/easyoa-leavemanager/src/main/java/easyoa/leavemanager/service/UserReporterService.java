package easyoa.leavemanager.service;

import easyoa.core.domain.dto.ReportRelationshipExcel;
import easyoa.core.domain.po.user.User;

import java.util.List;

/**
 * Created by claire on 2019-07-26 - 13:34
 **/
public interface UserReporterService {
    void saveUserReporterInfo(List<ReportRelationshipExcel> list);

    User findUserAssignee(Long userId, List<String> users);
}
