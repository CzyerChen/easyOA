package easyoa.leavemanager.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by claire on 2019-07-16 - 11:56
 **/
@Data
public class GlobalFlowDTO implements Serializable {

    private Integer id;
    private Integer root;
    private Integer parentId;
    private Boolean deleted;
    private Integer total;
    private Integer level;
    private String content;
    private String name;
    private String assigneeIds;
    private Integer companyId;
}
