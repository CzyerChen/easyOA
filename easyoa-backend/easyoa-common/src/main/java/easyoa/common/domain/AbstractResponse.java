package easyoa.common.domain;

import java.io.Serializable;

/**
 * 抽象响应实体
 * Created by claire on 2019-06-21 - 16:37
 **/
public abstract class AbstractResponse implements Serializable {

    public Long code;
    public String responseMessage;

    AbstractResponse(Long code,String responseMessage){
        this.code = code;
        this.responseMessage = responseMessage;
    }

    AbstractResponse(Long code){
        this.code = code;
    }

}
