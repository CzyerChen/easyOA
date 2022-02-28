package easyoa.common.model.message;


/**
 * Created by claire on 2019-07-10 - 11:06
 **/
public interface IHandler<T ,E,M> {
   E handle(T  message,Long key) throws Exception;

   E handle(M application) throws Exception;
}
