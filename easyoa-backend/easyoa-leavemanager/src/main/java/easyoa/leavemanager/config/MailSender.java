package easyoa.leavemanager.config;

import easyoa.common.utils.CommonUtil;
import easyoa.leavemanager.domain.GlobalEmailAccount;
import easyoa.leavemanager.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by claire on 2019-08-05 - 14:28
 **/
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailSender extends JavaMailSenderImpl implements JavaMailSender {

    //保存多个用户名和密码的队列
    private ArrayList<String> usernameList;
    private ArrayList<String> passwordList;
    private boolean sendFlag = false;
    //轮询标识
    private int currentMailId = 0;

    private final MailProperties properties;

    @Autowired
    private EmailService emailService;

    public MailSender(MailProperties properties) {
        this.properties = properties;

        // 初始化账号
        if (usernameList == null)
            usernameList = new ArrayList<String>();
        String[] userNames = this.properties.getUsername().split(",");
        if (userNames != null) {
            usernameList.addAll(Arrays.asList(userNames));
        }

        // 初始化密码
        if (passwordList == null)
            passwordList = new ArrayList<String>();
        String[] passwords = this.properties.getPassword().split(",");
        if (passwords != null) {
            passwordList.addAll(Arrays.asList(passwords));
        }
        if(this.properties.getProperties()!=null){
            sendFlag = Boolean.valueOf(this.properties.getProperties().get("flag"));
        }
    }

    @Override
    protected void doSend(MimeMessage[] mimeMessage, Object[] object) throws MailException {
        GlobalEmailAccount account = emailService.findFirstValidAccount();
        if(account != null && StringUtils.isNotBlank(account.getEmail())&& StringUtils.isNotBlank(account.getPassword())){
            super.setUsername(account.getEmail());
            super.setPassword(CommonUtil.decryptToken(account.getPassword()));
        }else{
            super.setUsername(usernameList.get(currentMailId));
            super.setPassword(passwordList.get(currentMailId));
        }

        // 设置编码和各种参数
        super.setHost(this.properties.getHost());
        super.setDefaultEncoding(this.properties.getDefaultEncoding().name());
        super.setJavaMailProperties(asProperties(this.properties.getProperties()));
        super.doSend(mimeMessage, object);

        // 轮询
       // currentMailId = (currentMailId + 1) % usernameList.size();
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    @Override
    public String getUsername() {
        return usernameList.get(currentMailId);
    }

    public boolean isSendFlag() {
        return sendFlag;
    }
}
