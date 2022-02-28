package easyoa.rule;

import easyoa.rulemanager.model.message.AllowenceMessageHandler;
import easyoa.rulemanager.model.message.MessagePorcessorFactory;
import easyoa.common.model.message.AllowenceMessage;
import easyoa.common.model.message.IHandler;
import easyoa.common.model.message.UserMessage;
import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by claire on 2019-07-10 - 11:31
 **/
public class MessageTest extends AbstractApplicationTest {

    @Test
    public void test1(){
        MessagePorcessorFactory mpf = new MessagePorcessorFactory();
        IHandler processor = mpf.getProcessor(AllowenceMessageHandler.class);
        UserMessage<AllowenceMessage> umessage = new UserMessage<>();
        umessage.setUserId(1L);
        umessage.setMessage(new AllowenceMessage("claire","1",1.0));
        try {
            processor.handle(umessage.getMessage(),umessage.getUserId());
        }catch (Exception e){

        }
    }

    @Test
    public void test2(){
        LocalDate localDate = LocalDate.of(2019, 7, 11);
        LocalDate localDate1 = localDate.minusDays(1);
        if(localDate1.isBefore(LocalDate.now())){
            localDate.getYear();
        }else {
            localDate.getYear();
        }
    }
}
