package easyoa.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户数据类
 * Created by claire on 2019-06-24 - 10:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO extends AbstractPOJO {
    /**
     *用户id
     */
    private Long userId;
    /**
     *用户名
     */
    private String userName;
    /**
     *用户昵称
     */
    private String nickName;
    /**
     *用户性别
     */
    private Character sex;
    /**
     *用户部门
     */
    private Integer deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 职位
     */
    private String position;
    /**
     *用户工号
     */
    private String userCode;
    /**
     *用户头像
     */
    private String avatar;
    /**
     *用户账号状态
     */
    private Integer status;
    /**
     * 用户角色
     */
    private String roleName;
    private Integer roleId;

    private String lastLoginTime;

    private List<RoleDTO> roles;

    private String phone;
    private String email;

    private String createTime;

    private Integer companyId;
    private String companyName;
}
