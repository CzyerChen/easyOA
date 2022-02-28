package easyoa.leavemanager.exception;

import javax.naming.AuthenticationException;

/**
 * Created by claire on 2019-06-21 - 17:14
 **/
public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super();
    }
}
