package easyoa.rulemanager.service;

import easyoa.rulemanager.domain.LeaveFile;

/**
 * Created by claire on 2019-07-10 - 17:12
 **/
public interface LeaveFileService {
    LeaveFile getLeaveFile(Long userId, Long fileId);
}
