package easyoa.leavemanager;

import easyoa.leavemanager.runner.system.MailServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

/**
 * Created by claire on 2019-08-05 - 14:07
 **/
public class MailTest extends AbstractApplicationTest {

    @Autowired
    private MailServer mailServer;

}
