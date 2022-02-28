package easyoa.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色数据类
 * Created by claire on 2019-06-24 - 11:07
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDTO extends AbstractPOJO {
    /**
     * 角色id
     */
    private int roleId;
    /**
     * 角色名称
     */
    private String roleName;

    private String createTime;

    private String description;

    private String updateTime;
}
