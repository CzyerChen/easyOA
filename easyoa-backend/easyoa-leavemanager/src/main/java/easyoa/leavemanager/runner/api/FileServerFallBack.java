package easyoa.leavemanager.runner.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by claire on 2019-07-15 - 18:22
 **/
@Slf4j
public class FileServerFallBack implements FileServer {
    @Override
    public Long uploadFile(String name, Long userId) {
        log.error("远程调用失败");
        return null;
    }

    @Override
    public String downloadFile(String fileId) {
        log.error("远程调用失败");
        return null;
    }

    @Override
    public void handleFileUpload(MultipartFile file, String name) {
        log.error("远程调用失败");
    }
/*
    @Override
    public void uploadFile2Local(MultipartFile multipartFile, String userId) {
      log.error("远程调用失败");
    }*/


}
