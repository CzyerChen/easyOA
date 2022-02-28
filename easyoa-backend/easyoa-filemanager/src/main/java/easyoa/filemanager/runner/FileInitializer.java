package easyoa.filemanager.runner;

import easyoa.filemanager.client.SftpClient;
import easyoa.filemanager.config.SftpConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by claire on 2019-07-30 - 15:44
 **/
@Slf4j
@Component
public class FileInitializer implements CommandLineRunner {
    @Autowired
    private SftpConfig sftpConfig;

    @Override
    public void run(String... args) throws Exception {
       SftpClient client = SftpClient.getINSTANCE();
       try {
           client.connect(sftpConfig.getServer(), sftpConfig.getPort(), sftpConfig.getLogin(), sftpConfig.getPassword());
           log.info("FTP服务连接成功");
       }catch (Exception e){
          log.error("连接SFTP发生异常，请检查连接,{}",e);
       }

    }
}
