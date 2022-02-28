package easyoa.common.domain.vo;

import lombok.Data;

/**
 * Created by claire on 2019-07-12 - 17:16
 **/
@Data
public class GlobalFlowForm {
    private String id;
    private String name;
    private String content;
    private Integer parentId;
    private String assigneeIds;
    private String createTime;
    private Boolean deleted;
    private Integer companyId;
}
