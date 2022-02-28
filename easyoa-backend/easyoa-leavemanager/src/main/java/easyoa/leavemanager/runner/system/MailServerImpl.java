package easyoa.leavemanager.runner.system;

import easyoa.leavemanager.config.MailSender;
import easyoa.leavemanager.domain.dto.Pair;
import easyoa.common.exception.BussinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-08-05 - 14:36
 **/
@Service(value = "mailServer")
public class MailServerImpl implements MailServer{
    @Autowired
    private MailSender mailSender;

    @Async("taskExecutor")
    @Override
    public void sendSimpleMail(String sendTo, String titel, String content) throws UnsupportedEncodingException, MessagingException {
        if(mailSender.isSendFlag()) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            InternetAddress from = new InternetAddress();
            from.setAddress(mailSender.getUsername());
            from.setPersonal("System", "UTF-8");
            helper.setFrom(from);
            helper.setTo(sendTo);
            helper.setSubject(titel);
            helper.setText(content, true);
            mailSender.send(message);
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendAttachmentsMail(String sendTo, String titel, String content, List<Pair<String, File>> attachments) {
        if(mailSender.isSendFlag()) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom("from someone");
                helper.setTo(sendTo);
                helper.setSubject(titel);
                helper.setText(content);

                for (Pair<String, File> pair : attachments) {
                    helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
                }
            } catch (Exception e) {
                throw new BussinessException("邮件发送失败");
            }

            mailSender.send(mimeMessage);
        }
    }

    @Async("taskExecutor")
    public void sendInlineMail() {
        if(mailSender.isSendFlag()) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom("from someone");
                helper.setTo("@163.com");
                helper.setSubject("主题：嵌入静态资源");
                helper.setText("<html><body><img src=\"cid:weixin\" ></body></html>", true);

                FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
                helper.addInline("weixin", file);
            } catch (Exception e) {
                throw new BussinessException("发送邮件失败");
            }

            mailSender.send(mimeMessage);
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendTemplateMail(String sendTo, String titel, Map<String, Object> content, List<Pair<String, File>> attachments) {
       /* MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("from someone");
            helper.setTo(sendTo);
            helper.setSubject(titel);

            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "template.vm", "UTF-8", content);
            helper.setText(text, true);

            for (Pair<String, File> pair : attachments) {
                helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
            }
        } catch (Exception e) {
            throw new BussinessExeption("发送邮件失败");
        }

        mailSender.send(mimeMessage);*/
    }
}
