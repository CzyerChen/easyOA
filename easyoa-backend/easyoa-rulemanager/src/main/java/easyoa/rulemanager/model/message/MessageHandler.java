package easyoa.rulemanager.model.message;

import easyoa.common.model.message.IHandler;

/**
 * Created by claire on 2019-07-10 - 11:08
 **/
public  class MessageHandler<T,E,M> implements IHandler<T ,E,M > {
    public E handle(T message, Long key) throws Exception{
        System.out.println("没有进入handler");

        return null;
    }

    @Override
    public E handle(M application) throws Exception {
        System.out.println("没有进入handler");
        return null;
    }
}
