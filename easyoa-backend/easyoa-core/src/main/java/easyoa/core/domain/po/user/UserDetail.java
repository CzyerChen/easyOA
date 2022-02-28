package easyoa.core.domain.po.user;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 14:27
 **/
@Entity
@Table(name = "fb_user_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail extends AbstractPOJO {
    /**
     * 用户工号
     */
    @Id
    private String  id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 中文名
     */
    private String chineseName;
    /**
     * 英文名
     */
    private String englishName;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * true- 男   false- 女
     */
    private Boolean sex;
    /**
     * true- 已婚  false - 未婚
     */
    private Boolean marriage;
    /**
     * 工作年限
     */
    private Double workYear;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 所处中心
     */
    private String center;
    /**
     * 出生日期
     */
    private Date birthDate;
    /**
     * 雇佣日期
     */
    private Date hireDate;
    /**学校
     *
     */
    private String school;
    /**
     * 职位
     */
    private String positionName;
    /**
     * 是否删除
     */
    private Boolean deleted;
    /**
     * 现居住地
     */
    private String address;
    /**
     *司龄
     */
    private Double workInCompony;
    /**
     * 员工类型
     */
    private Integer type;
    /**
     * 参加工作时间
     */
    private Date workDate;

}
