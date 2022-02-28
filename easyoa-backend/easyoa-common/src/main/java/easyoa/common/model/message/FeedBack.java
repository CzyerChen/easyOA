package easyoa.common.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-10 - 13:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack extends Message {
    private Boolean meet;
    private String invalidMessage;
    private String requestMessage;
    //表示实现限制不满足，也能够让其提交申请
    private Boolean timeMeet;

    public FeedBack(String name ,String type,Double days){
        super(name, type, days);
    }

    @Override
    public String detail() {
        return null;
    }

    public void error(){
         this.meet=Boolean.FALSE;
         this.invalidMessage="流程异常，请联系管理员";
    }
}
