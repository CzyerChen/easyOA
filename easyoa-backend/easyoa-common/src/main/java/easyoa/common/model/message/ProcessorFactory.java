package easyoa.common.model.message;

/**
 * Created by claire on 2019-07-10 - 11:16
 **/
public interface ProcessorFactory {
    IHandler getProcessor(Class<? extends IHandler> clazz);
}
