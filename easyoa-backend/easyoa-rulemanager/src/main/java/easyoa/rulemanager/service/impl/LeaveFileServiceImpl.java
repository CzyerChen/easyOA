package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.LeaveFile;
import easyoa.rulemanager.service.LeaveFileService;
import easyoa.rulemanager.repository.LeaveFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by claire on 2019-07-10 - 17:12
 **/
@Service(value = "leaveFileService")
public class LeaveFileServiceImpl implements LeaveFileService {
    @Autowired
    private LeaveFileRepository leaveFileRepository;

    @Override
    public LeaveFile getLeaveFile(Long userId, Long fileId) {
        return leaveFileRepository.findByUserIdAndFileId(userId,fileId);
    }
}
