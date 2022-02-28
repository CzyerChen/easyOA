package easyoa.rule;

import easyoa.rulemanager.domain.UserDetail;
import easyoa.rulemanager.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by claire on 2019-07-25 - 17:47
 **/
public class UserTest extends AbstractApplicationTest {
    @Autowired
    private UserService userService;

    @Test
    public void test(){
        UserDetail detail = userService.getUserDetailById(33L);
        Assert.assertNotNull(detail);
    }
}
