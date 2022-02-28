package easyoa.common.model.message;

import lombok.Data;

/**
 * Created by claire on 2019-07-10 - 11:12
 **/
@Data
public class AllowenceMessage extends Message {
    private Long fileId;

    public AllowenceMessage(){
        super();
    }

    public AllowenceMessage(String name,String type,Double days){
        super(name, type,days);
    }
    @Override
    public String detail() {
        return null;
    }
}
