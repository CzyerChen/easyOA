package easyoa.core.exception;

import java.io.Serializable;

/**
 * 请求限制异常类
 * Created by claire on 2019-06-21 - 16:44
 **/
public class LimitAccessException extends RuntimeException{

    public LimitAccessException(String message) {
        super(message);
    }

}
