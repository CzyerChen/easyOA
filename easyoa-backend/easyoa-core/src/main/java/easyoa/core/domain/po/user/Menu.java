package easyoa.core.domain.po.user;


import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 菜单类
 * Created by claire on 2019-06-20 - 13:58
 **/
@Entity
@Table(name = "fb_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu extends AbstractPOJO {
    public static final String TYPE_MENU = "0";

    public static final String TYPE_BUTTON = "1";


    /**
     * 菜单id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;

    /**
     * 父菜单id
     */
    private Integer parentId;
    /**
     * 菜单名称
     */
    @NotBlank(message = "{required}")
    private String menuName;
    /**
     * 菜单路径
     */
    private String path;
    /**
     * 菜单组件
     */
    private String component;
    /**
     * 权限
     */
    private String permissions;
    /**
     * 按钮
     */
    private String icon;
    /**
     * 类型
     */
    @NotBlank(message = "{required}")
    private String type;
    /**
     * 排序
     */
    private Double orderNum;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 角色列表
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "fb_role_menu", joinColumns = { @JoinColumn(name = "menu_id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id") })
    private List<Role> roleList;

}
