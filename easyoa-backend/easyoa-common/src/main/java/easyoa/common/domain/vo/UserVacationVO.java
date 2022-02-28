package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-24 - 16:24
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVacationVO {

    private Double casualLeave;
    private Double sickLeave;
    private Double funeralLeave;
    private Double marriageLeave;
    private Double maternityLeave;
    private Double paternityLeave;
    private Double annualLeave;
    private Double sickLeaveNormal;
    private String annualLeaveTotal;
    private Double annualShould;
    private Double restAnnualLeave;
}
