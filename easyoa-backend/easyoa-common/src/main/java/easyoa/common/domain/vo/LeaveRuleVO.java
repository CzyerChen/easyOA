package easyoa.common.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by claire on 2019-07-11 - 17:39
 **/
@Data
public class LeaveRuleVO  implements Serializable {
    private int ruleId;
    private String ruleName;
    private Integer ruleType;
    private Integer subType;
    private Integer maxPermitDay;
    private Integer leaveDaysFrom;
    private Integer leaveDaysTo;
    private Integer forwardDays;
    private Integer age;
    private Integer workYearsFrom;
    private Integer workYearsTo;
    private String notice;
    private Boolean needUpload;
    private String createTime;
    private String updateTime;
    private String fileRequired;
    private Integer companyId;
}
