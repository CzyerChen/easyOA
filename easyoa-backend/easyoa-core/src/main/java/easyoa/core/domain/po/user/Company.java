/**
 * Author:   claire
 * Date:    2022/2/19 - 9:03 上午
 * Description: 公司
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:03 上午          V1.0.0          公司
 */
package easyoa.core.domain.po.user;

import easyoa.common.domain.dto.CompanyDTO;
import easyoa.common.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * 功能简述
 * 〈公司〉
 *
 * @author claire
 * @date 2022/2/19 - 9:03 上午
 * @since 1.0.0
 */
@Entity
@Table(name = "fb_company")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String companyName;
    private Date createTime;
    private Date updateTime;
    private Boolean deleted;

    public CompanyDTO toDto(){
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(this,dto);
        dto.setCreateTime(DateUtil.format(this.getCreateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
        dto.setUpdateTime(DateUtil.format(this.getUpdateTime().toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime()));
        return dto;
    }
}
