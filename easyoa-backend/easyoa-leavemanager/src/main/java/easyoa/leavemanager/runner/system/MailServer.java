package easyoa.leavemanager.runner.system;

import easyoa.leavemanager.domain.dto.Pair;

import javax.mail.MessagingException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by claire on 2019-08-05 - 14:35
 **/
public interface MailServer {
    /**
     * 发送简单邮件
     * @param sendTo 收件人地址
     * @param titel  邮件标题
     * @param content 邮件内容
     */
    void sendSimpleMail(String sendTo, String titel, String content) throws UnsupportedEncodingException, MessagingException;

    /**
     * 发送简单邮件
     * @param sendTo 收件人地址
     * @param titel  邮件标题
     * @param content 邮件内容
     * @param attachments<文件名，附件> 附件列表
     */
    void sendAttachmentsMail(String sendTo, String titel, String content, List<Pair<String, File>> attachments);

    /**
     * 发送模板邮件
     * @param sendTo 收件人地址
     * @param titel  邮件标题
     * @param content<key, 内容> 邮件内容
     * @param attachments<文件名，附件> 附件列表
     */
    void sendTemplateMail(String sendTo, String titel, Map<String, Object> content, List<Pair<String, File>> attachments);

}
