package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.biz.LeaveFile;

/**
 * Created by claire on 2019-07-15 - 18:29
 **/
public interface LeaveFileService {

    void saveFile(LeaveFile leaveFile);

    LeaveFile findByFileId(Long fileId);

    String findFileOriginNameById(Long fileId);
}
