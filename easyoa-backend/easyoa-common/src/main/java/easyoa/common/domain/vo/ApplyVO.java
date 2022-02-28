package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Created by claire on 2019-07-10 - 17:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyVO {
    @NotBlank
    private String leaveDateFrom;
    @NotBlank
    private String leaveDateTo;
    @NotBlank
    private Integer leaveType;
    private String leaveName;
    @NotBlank
    private Long userId;
    private String leaveReson;
    @NotBlank
    private String applyName;
    private Integer applicateType;
}
