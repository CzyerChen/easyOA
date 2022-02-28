package easyoa.leavemanager.domain.biz;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by claire on 2019-06-21 - 18:32
 **/
@Entity
@Table(name = "fb_application_flow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationFlow extends AbstractPOJO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long applicationId;
    /**
     * 流转-1、结束-2、终止-3，重做-4（重新委派）
     */
    private Integer type;
    private String name;
    private Long userId;
    private Long assignee;
    private String assigneeName;
    private Integer level;
    /**
     * 通过、拒绝
     */
    private Boolean status;

    private Integer flowId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String content;

    private Boolean continueTrans;
}
