package easyoa.core.domain.po.user;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户类
 * Created by claire on 2019-06-20 - 14:25
 **/
@Cacheable(value = false)
/*@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONE)*/
@Entity
@Table(name = "fb_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractPOJO {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 密码
     */
    private String password;
    /**
     * 性别
     */
    private Character sex;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 部门id
     */
    private Integer deptId;
    /**
     * 用户工号
     */
    private String userCode;
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
    private Boolean deleted;
    /**
     * 账号状态
     */
    private Integer status;
    /**
     * 用户头像
     */
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "fb_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id")})
    private List<Role> roleList;

    /***
     * 回传的一个字段，没有实际意义
     */
    private transient String id;

    private Integer companyId;
}
