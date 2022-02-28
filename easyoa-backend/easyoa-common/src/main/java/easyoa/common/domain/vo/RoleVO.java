package easyoa.common.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by claire on 2019-06-28 - 15:55
 **/
@Data
public class RoleVO {
    private int roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private boolean deleted;
    /**
     * 描述
     */
    private String description;

    private String menuId;
}
