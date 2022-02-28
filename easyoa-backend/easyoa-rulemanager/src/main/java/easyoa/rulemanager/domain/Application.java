package easyoa.rulemanager.domain;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by claire on 2019-06-20 - 18:26
 **/
@Entity
@Table(name = "fb_application")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application extends AbstractPOJO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer deptId;
    private String deptName;
    private Long userId;
    private String userName;
    private String position;
    private String leaveType;
    private String applicateType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double days;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime finishTime;
    private String leaveReason;
    private String status;
    private String remark;
    private Long resources;
    private String stage;

}
