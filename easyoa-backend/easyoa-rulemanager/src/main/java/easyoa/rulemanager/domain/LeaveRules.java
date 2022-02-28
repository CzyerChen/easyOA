package easyoa.rulemanager.domain;


import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by claire on 2019-06-20 - 18:42
 **/
@Entity
@Table(name = "fb_leave_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRules extends AbstractPOJO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDate createTime;
    private LocalDate updateTime;
    private String fileRequired;
    private Integer companyId;
}
