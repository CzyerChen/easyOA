package easyoa.rulemanager.model.message;

import easyoa.common.model.message.IHandler;
import easyoa.common.model.message.ProcessorFactory;
import org.springframework.stereotype.Component;

/**
 * Created by claire on 2019-07-10 - 11:28
 **/
@Component
public class MessagePorcessorFactory implements ProcessorFactory {
    @Override
    public IHandler getProcessor(Class<? extends IHandler> clazz) {
        IHandler handler = null;

        try {
            handler = (IHandler) Class.forName(clazz.getName()).newInstance();
            return handler;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
}
