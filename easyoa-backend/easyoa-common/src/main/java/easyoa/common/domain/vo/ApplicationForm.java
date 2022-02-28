package easyoa.common.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Created by claire on 2019-07-15 - 09:47
 **/
@Data
public class ApplicationForm {
    /**
     * 申请人
     */
    private Long userId;
    /**
     * 休假类型
     */
    @NotBlank(message = "请假类别不能为空")
    private String leaveType;
    /**
     * 休假开始时间
     */
    @NotBlank(message = "请假开始时间不能为空")
    private String leaveTimeFrom;
    /**
     * 休假结束时间
     */
    private String leaveTimeTo;
    /**
     * 休假原因
     */
    private String leaveReason;
    /**
     * 本地文件位置，待上传
     */
    private String uploadFile;
}
