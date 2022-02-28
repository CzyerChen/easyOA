/**
 * Author:   claire
 * Date:    2020-01-21 - 11:18
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.core.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 功能简述 <br/> 
 * 〈用户休假细节〉
 *
 * @author claire
 * @date 2020-01-21 - 11:18
 * @since 1.3.0
 */
@Entity
@Table(name = "fb_global_vacation_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalVacationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String offday;
    private Integer year;
}
