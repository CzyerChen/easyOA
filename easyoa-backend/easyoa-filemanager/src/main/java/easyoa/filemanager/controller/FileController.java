package easyoa.filemanager.controller;

import easyoa.filemanager.domain.LeaveFile;
import easyoa.filemanager.exception.InfinItException;
import easyoa.filemanager.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by claire on 2019-07-11 - 11:15
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleFileUpload(@RequestPart(value = "file") MultipartFile file,@RequestParam(value = "userId")String userId) {
        System.out.println(userId);
        return file.getOriginalFilename();
    }

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(@RequestPart("file") MultipartFile applyFile, @RequestParam("userId") String userId) {
 /*       System.out.println(userId);
        System.out.println(applyFile.getOriginalFilename());*/
        LeaveFile leaveFile = fileService.uploadFile2Server(Long.valueOf(userId), applyFile);
        fileService.saveLeaveFileDetail(Long.valueOf(userId), leaveFile);
    }

    @GetMapping("/do")
    public Long doUpload(@RequestParam("name") String name,@RequestParam("userId")Long userId) {
        return fileService.uploadFile2RemoteServer(userId, name);
    }

    @GetMapping("/download")
    public String getRemoteFile(Long fileId) throws InfinItException {
        LeaveFile leaveFile = fileService.findbyFileId(fileId);
        if (leaveFile != null) {
            return fileService.downloadFile(leaveFile);
        }
        return null;
    }

}
