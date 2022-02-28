package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-09 - 16:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationVO {
    private Integer vacationId;
    private String name;
    private String startDate;
    private String endDate;
    private String festival;
    private Integer days;
    private String description;
    private String advice;
    private String detail;
}
