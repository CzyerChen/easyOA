package easyoa.common.exception;

/**
 * 应用异常类
 * Created by claire on 2019-06-24 - 13:34
 **/
public class BussinessException extends RuntimeException {
    private static final long serialVersionUID = -994962710559017255L;

    public BussinessException(String message){
        super(message);
    }
}
