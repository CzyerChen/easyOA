package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.biz.LeaveFile;
import easyoa.leavemanager.service.LeaveFileService;
import easyoa.leavemanager.repository.biz.LeaveFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by claire on 2019-07-15 - 18:30
 **/
@Service(value = "leaveFileService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class LeaveFileServiceImpl implements LeaveFileService {

    @Autowired
    private LeaveFileRepository leaveFileRepository;

    @Override
    @Transactional
    public void saveFile(LeaveFile leaveFile) {
       leaveFileRepository.save(leaveFile);
    }

    @Override
    public LeaveFile findByFileId(Long fileId) {
        return leaveFileRepository.findByFileId(fileId);
    }

    @Override
    public String findFileOriginNameById(Long fileId) {
        LeaveFile leaveFile = leaveFileRepository.findByFileId(fileId);
        if(leaveFile !=null){
            return leaveFile.getFileOriginName();
        }
        return null;
    }
}
