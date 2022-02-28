package easyoa.common.domain.vo;

import lombok.Data;

/**
 * Created by claire on 2019-07-19 - 16:16
 **/
@Data
public class ApplicationSearch {
    private String leaveType;
    private String userCode;
    private Long userId;
    private String createTimeFrom;
    private String createTimeTo;
    private String applyTimeFrom;
    private String applyTimeTo;
    private String status;
    private Integer companyId;
}
