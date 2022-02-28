package easyoa.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by claire on 2019-06-28 - 14:29
 **/
@Data
public class PageRequestEntry implements Serializable {
    private static final long serialVersionUID = -4869594085374385813L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer limit  = 10;
    private Integer offset = 0;
    private String  direction;
    private String orderBy;
    private String sortField;
    private String sortOrder;
}
