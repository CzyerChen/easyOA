package easyoa.core.domain.po.user;


import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 部门类
 * Created by claire on 2019-06-20 - 14:28
 **/
@Entity
@Table(name = "fb_department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends AbstractPOJO {
    /**
     * 部门id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 部门名字
     */
    private String deptName;
    /**
     * 部门类型
     */
    private Integer deptType;
    /**
     * 部门等级
     */
    private Integer level;
    /**
     * 部门创建时间
     */
    private Date createTime;
    /**
     * 部门配置更新时间
     */
    private Date updateTime;
    /**
     * 父部门id
     */
    private Integer parentId;
    /**
     * 部门路径
     */
    private String path;
    /**
     * 是否为根部门
     */
    private Boolean root;
    /**
     * 是否已删除
     */
    private Boolean deleted;
    /**
     * 所属中心
     */
    private String center;
    /**
     * 所属公司
     */
    private Integer companyId;

    transient private String companyName;

}
