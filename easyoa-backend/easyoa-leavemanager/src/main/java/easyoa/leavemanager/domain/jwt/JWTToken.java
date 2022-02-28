package easyoa.leavemanager.domain.jwt;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by claire on 2019-06-21 - 14:47
 **/
@Data
public class JWTToken implements AuthenticationToken {
    //private static final long serialVersionUID = 1282057025599826155L;

    private String token;

    private String exipreAt;

    public JWTToken(String token) {
        this.token = token;
    }

    public JWTToken(String token, String exipreAt) {
        this.token = token;
        this.exipreAt = exipreAt;
    }


    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
