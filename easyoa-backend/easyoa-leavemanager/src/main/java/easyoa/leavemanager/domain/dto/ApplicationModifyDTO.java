package easyoa.leavemanager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Created by claire on 2019-10-04 - 20:26
 **/
@Data
@Builder
@AllArgsConstructor
public class ApplicationModifyDTO {
    private Long userId;
    private String userName;
    private String assigneeName;
    private Long applicationId;
    private LocalDate month;
    private String leaveTypeOld;
    private String leaveType;
    private Double daysOld;
    private Double days;
    private String status;

}
