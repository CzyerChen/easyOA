package easyoa.leavemanager.model;

/**
 * Created by claire on 2019-07-17 - 17:39
 **/
public interface IFlowHandler<T> {
    void handleTranfer(T t);
    void handleOver(T t);
    void handleTerminate(T t);
    void handleRedo(T t);
    void handleFinish(T t);
    void handleCancel(T t);
}
