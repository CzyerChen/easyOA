package easyoa.common.domain.dto;

import lombok.Data;

/**
 * Created by claire on 2019-07-16 - 10:39
 **/
@Data
public class ApplicationFeedBack {
    private Long applicationId;
    private String content;
    private String leaveRange;
    private Double days;
    private String status;
    private String assignee;
}
