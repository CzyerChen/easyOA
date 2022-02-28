package easyoa.common.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by claire on 2019-07-12 - 13:48
 **/
@Data
public class GlobalFlowVO implements Serializable {
    private String flowId;
    private String parantId;
    private String name;
    private String content;
    private List<Integer> assigneeIds;
    private String assigneeNames;
    private String createDate;
    private String updateDate;
    private Boolean status;
    private Integer companyId;

}
