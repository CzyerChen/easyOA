package easyoa.leavemanager;

import easyoa.common.utils.CommonUtil;
import easyoa.leavemanager.domain.jwt.JWTToken;
import easyoa.leavemanager.utils.JWTUtil;
import org.junit.Test;

/**
 * Created by claire on 2019-06-26 - 18:58
 **/
public class CommonTest extends AbstractApplicationTest {
    @Test
    public void testEncrype(){
        String token = CommonUtil.encryptToken(JWTUtil.sign("admin", "123456"));
        JWTToken jwtToken = new JWTToken(CommonUtil.decryptToken(token));
        System.out.println(jwtToken.toString());
    }
}
