package easyoa.rulemanager.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyoa.common.model.message.ApplyMessage;
import easyoa.common.model.message.FeedBack;
import easyoa.common.model.message.IHandler;
import easyoa.common.model.message.UserMessage;
import easyoa.rulemanager.domain.Application;
import easyoa.rulemanager.service.ApplicationService;
import easyoa.rulemanager.model.message.AllowenceMessageHandler;
import easyoa.rulemanager.model.message.ApplyMessageHandler;
import easyoa.rulemanager.model.message.MessagePorcessorFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * Created by claire on 2019-07-10 - 13:38
 **/
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessagePorcessorFactory messagePorcessorFactory;
    @Autowired
    private ApplicationService applicationService;


    /**
     * ApplyMessageHandler 负责条件审核 ，AllowenceMessageHandler 负责文件审核
     * @param message
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @ResponseBody
    public String checkMessageForRules(@NotBlank @RequestBody String message) throws Exception {
        IHandler processor = null;
        ObjectMapper mapper = new ObjectMapper();
        FeedBack feedBack = null;
        if (message.contains("AllowenceMessage")) {
            UserMessage<ApplyMessage> value = mapper.readValue(message, new TypeReference<UserMessage<ApplyMessage>>() {
            });
            processor = messagePorcessorFactory.getProcessor(AllowenceMessageHandler.class);
            if (processor != null) {
                feedBack = (FeedBack) processor.handle(value.getMessage(), value.getUserId());
                return mapper.writeValueAsString(feedBack);
            }
        } else if (message.contains("ApplyMessage")) {
            UserMessage<ApplyMessage> value = mapper.readValue(message, new TypeReference<UserMessage<ApplyMessage>>() {
            });
            processor = messagePorcessorFactory.getProcessor(ApplyMessageHandler.class);
            if (processor != null) {
                feedBack = (FeedBack) processor.handle(value.getMessage(), value.getUserId());
                return mapper.writeValueAsString(feedBack);
            }
        }
        return null;
    }

    @GetMapping
    public String checkForApply(@NotBlank @RequestParam(name = "type") String type, @RequestParam(name = "applicaitonId") Long applicaitonId) throws Exception {
        if(StringUtils.isBlank(type)){
            return null;
        }

        Application application = applicationService.findById(applicaitonId);
        if(application!= null) {
            IHandler processor = null;
            if("apply".equals(type)) {
                processor = messagePorcessorFactory.getProcessor(ApplyMessageHandler.class);
            }else if("allowence".equals(type)){
                processor = messagePorcessorFactory.getProcessor(AllowenceMessageHandler.class);
            }
            if(null != processor) {
                FeedBack feedBack = (FeedBack)processor.handle(application);
                if(feedBack!=null){
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.writeValueAsString(feedBack);
                }
            }
        }
        return null;
    }


}
