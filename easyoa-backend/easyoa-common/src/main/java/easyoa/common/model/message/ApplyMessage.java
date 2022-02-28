package easyoa.common.model.message;

import lombok.Data;

/**
 * Created by claire on 2019-07-10 - 11:11
 **/
@Data
public class ApplyMessage extends Message {
     private String leaveDate;

    public ApplyMessage(){
        super();
    }

    public ApplyMessage(String name,String type,Double days){
        super(name, type,days);
    }
    @Override
    public String detail() {
        return null;
    }
}
