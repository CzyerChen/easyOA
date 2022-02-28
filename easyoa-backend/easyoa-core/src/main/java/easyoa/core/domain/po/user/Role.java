package easyoa.core.domain.po.user;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 角色类
 * Created by claire on 2019-06-20 - 14:26
 **/
@Entity
@Table(name = "fb_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends AbstractPOJO {
    /**
     * 角色id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "fb_user_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
            @JoinColumn(name = "user_id") })
    private List<User> userList;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "fb_role_menu", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
            @JoinColumn(name = "menu_id") })
    private List<Menu> menuList;
}
