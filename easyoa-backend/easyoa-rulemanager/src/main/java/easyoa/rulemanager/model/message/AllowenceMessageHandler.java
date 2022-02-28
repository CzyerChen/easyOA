package easyoa.rulemanager.model.message;

import easyoa.rulemanager.constant.ContextConstant;
import easyoa.rulemanager.constant.RuleConstant;
import easyoa.rulemanager.domain.Application;
import easyoa.rulemanager.domain.LeaveFile;
import easyoa.rulemanager.model.SpringContextHolder;
import easyoa.rulemanager.service.LeaveFileService;
import easyoa.common.model.message.AllowenceMessage;
import easyoa.common.model.message.FeedBack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by claire on 2019-07-10 - 11:25
 **/
@Slf4j
public class AllowenceMessageHandler extends MessageHandler<AllowenceMessage,FeedBack, Application> {

    @Override
    public FeedBack handle(AllowenceMessage message, Long key) throws Exception {
        log.info("AllowenceMessageHandler proceesor ");
        LeaveFileService leaveFileService = (LeaveFileService) SpringContextHolder.getBean(ContextConstant.FILE);
        LeaveFile leaveFile = leaveFileService.getLeaveFile(key, message.getFileId());

        FeedBack feedBack = new FeedBack(message.getName(),message.getType(),message.getDays());
        if(leaveFile!= null && StringUtils.isNotBlank(leaveFile.getFileRemotePath())){
            feedBack.setRequestMessage("资料审核");
            feedBack.setMeet(true);
            feedBack.setTimeMeet(true);
        }else{
            feedBack.setMeet(false);
            feedBack.setTimeMeet(false);
            feedBack.setInvalidMessage("未提供必要的资料审核");
        }

        return feedBack;
    }

    @Override
    public FeedBack handle(Application application) throws Exception {
        log.info("AllowenceMessageHandler proceesor for application");
        LeaveFileService leaveFileService = (LeaveFileService)SpringContextHolder.getBean(ContextConstant.FILE);
        LeaveFile leaveFile = leaveFileService.getLeaveFile(application.getUserId(), application.getResources());

        FeedBack feedBack = new FeedBack(application.getLeaveType(),application.getApplicateType(),application.getDays());
        boolean chehckFlag = true;
        boolean fileNeedFlag= true;
        switch(application.getLeaveType()){
            case RuleConstant.ANNUAL_LEAVE:
                fileNeedFlag = false;
                break;
            case RuleConstant.CASUAL_LEAVE:
                fileNeedFlag = false;
                break;
            case RuleConstant.FUNERAL_LEAVE:
                fileNeedFlag = false;
                break;
            case RuleConstant.SICK_LEAVE:
                if(application.getDays()<=1) {
                   fileNeedFlag = false;
                }else{
                    if(leaveFile == null || StringUtils.isBlank(leaveFile.getFileRemotePath())){
                        chehckFlag =false;
                    }
                }
                break;
            case RuleConstant.PATERNITY_LEAVE:
                if(leaveFile == null || StringUtils.isBlank(leaveFile.getFileRemotePath())){
                    chehckFlag =false;
                }
                break;
            case RuleConstant.MARRIAGE_LEAVE:
                if(leaveFile == null || StringUtils.isBlank(leaveFile.getFileRemotePath())){
                   chehckFlag=false;
                }
                break;
            case RuleConstant.MATERNITY_LEAVE:
                if(leaveFile == null || StringUtils.isBlank(leaveFile.getFileRemotePath())){
                    chehckFlag=false;
                }
                break;
        }

        if(fileNeedFlag){
            if(chehckFlag){
                feedBack.setRequestMessage("资料审核成功，已通过");
                feedBack.setMeet(true);
                feedBack.setTimeMeet(true);
            }else{
                feedBack.setInvalidMessage("未提供资料文件审核，未通过");
                feedBack.setMeet(false);
                feedBack.setTimeMeet(false);
            }
        }else {
            feedBack.setRequestMessage("无需资料审核，已通过");
            feedBack.setMeet(true);
            feedBack.setTimeMeet(true);
        }


        return feedBack;
    }

}
