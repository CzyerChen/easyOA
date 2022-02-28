package easyoa.filemanager.service;

import easyoa.filemanager.domain.LeaveFile;
import easyoa.filemanager.exception.InfinItException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by claire on 2019-07-11 - 11:59
 **/
public interface FileService {

    LeaveFile uploadFile2Server(Long userId, MultipartFile file);

    Long uploadFile2RemoteServer(Long userId,String name);

    LeaveFile saveLeaveFileDetail(Long userId,LeaveFile leaveFile);

    LeaveFile findbyFileId(Long fileId);

    String downloadFile(LeaveFile leaveFile) throws InfinItException;
}
