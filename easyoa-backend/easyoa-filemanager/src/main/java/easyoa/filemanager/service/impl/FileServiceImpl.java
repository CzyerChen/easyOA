package easyoa.filemanager.service.impl;

import easyoa.filemanager.client.SftpClient;
import easyoa.filemanager.config.SftpConfig;
import easyoa.filemanager.domain.LeaveFile;
import easyoa.filemanager.repository.LeaveFileRepository;
import easyoa.filemanager.service.FileService;
import easyoa.common.exception.BussinessException;
import easyoa.common.utils.DateUtil;
import easyoa.filemanager.exception.InfinItException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Created by claire on 2019-07-11 - 11:59
 **/
@Slf4j
@Service(value = "fileService")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class
)
public class FileServiceImpl implements FileService {
    @Autowired
    private LeaveFileRepository leaveFileRepository;
    @Autowired
    private SftpConfig sftpConfig;
    @Value("${file.upload.localPath}")
    private String localPath;
    @Value("${file.upload.targetPath}")
    private String targetPath;
    @Value("${file.download.tmpPath}")
    private String tmpPath;


    @Override
    public LeaveFile uploadFile2Server(Long userId, MultipartFile file) {
        LeaveFile leaveFile = new LeaveFile();
        try {
            uploadFile2LocalFile(userId, file,leaveFile);
        }catch (Exception e){
            log.error("写入到本地文件发生异常",e);
            throw new BussinessException("文件写入本地发生异常");
        }

      /*  SftpClient sftpClient = SftpClient.getINSTANCE();
        sftpClient.connect(sftpConfig.getServer(),sftpConfig.getPort(),sftpConfig.getLogin(),sftpConfig.getPassword());
        try{
            String targetFilePath = generateFileName(userId);
            sftpClient.uploadFile(leaveFile.getFileLocalPath()+leaveFile.getFileCurrentName(),leaveFile.getFileCurrentName(),targetPath,targetFilePath);
            leaveFile.setFileRemotePath(targetPath+targetFilePath);
            leaveFile.setFileContent(leaveFile.getFileOriginName());
        }catch (Exception e){
            log.error("sftp 上传文件出现异常",e);
        }finally {
            sftpClient.disconnect();
        }*/

        return  leaveFile;

    }

    @Override
    public Long uploadFile2RemoteServer(Long userId, String name) {
        LeaveFile leaveFile = leaveFileRepository.findByFileCurrentName(name);
        if(leaveFile == null){
            return null;
        }
        SftpClient sftpClient = SftpClient.getINSTANCE();
        sftpClient.connect(sftpConfig.getServer(),sftpConfig.getPort(),sftpConfig.getLogin(),sftpConfig.getPassword());
        try{
            String targetFilePath = generateFileName(userId);

            sftpClient.uploadFile(leaveFile.getFileLocalPath()+leaveFile.getFileCurrentName(),leaveFile.getFileCurrentName(),targetPath,generateFileName(userId));
            leaveFile.setFileRemotePath(targetPath+targetFilePath);
            leaveFile.setFileContent(leaveFile.getFileOriginName());
        }catch (Exception e){
            log.error("sftp 上传文件出现异常",e);
        }finally {
            sftpClient.disconnect();
        }

        LeaveFile leaveFile1 = saveLeaveFileDetail(userId, leaveFile);
        if(leaveFile1!= null){
            return leaveFile1.getFileId();
        }
        return null;
    }

    @Override
    public LeaveFile saveLeaveFileDetail(Long userId, LeaveFile leaveFile) {
        leaveFile.setUserId(userId);
        return leaveFileRepository.save(leaveFile);
    }

    @Override
    public LeaveFile findbyFileId(Long fileId) {
        return leaveFileRepository.findByFileId(fileId);
    }

    @Override
    public String downloadFile(LeaveFile leaveFile) throws InfinItException {
        SftpClient sftpClient = SftpClient.getINSTANCE();
        sftpClient.connect(sftpConfig.getServer(),sftpConfig.getPort(),sftpConfig.getLogin(),sftpConfig.getPassword());
        try {
            sftpClient.retrieveFile(leaveFile.getFileRemotePath() + leaveFile.getFileCurrentName(), tmpPath + leaveFile.getFileCurrentName());
        }catch (Exception e){
            log.error("sftp 文件下载失败",e);
        }finally {
            sftpClient.disconnect();
        }
        return tmpPath+leaveFile.getFileCurrentName();
    }

    private void uploadFile2LocalFile(Long userId,MultipartFile uploadFile,LeaveFile leaveFile) throws IOException {
       String filePath = localPath+generateFileName(userId);
       leaveFile.setFileLocalPath(filePath);
       leaveFile.setFileOriginName(uploadFile.getOriginalFilename());
        File fp = new File(filePath);
        if (!fp.exists()) {
            try {
                fp.mkdirs();// 目录不存在的情况下，创建目录。
            }catch (Exception e){
                log.error("创建目录发生异常");
                throw new BussinessException("创建目录发生异常");
            }
        }

        String newName = UUID.randomUUID().toString().replace("-","")+"."+uploadFile.getOriginalFilename().split("\\.")[1];
        leaveFile.setFileCurrentName(newName);
        String fileName = filePath+newName;
        File file = new File(fileName);
        uploadFile.transferTo(file);
    }

    private String generateFileName(Long userId){
        String name = DateUtil.formatDate(LocalDate.now(), DateUtil.DEFAULT_DATE_PATTERN);
        return "/personal/"+userId+"/"+name+"/";
    }



}
