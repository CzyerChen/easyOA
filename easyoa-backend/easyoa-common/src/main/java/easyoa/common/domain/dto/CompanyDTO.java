/**
 * Author:   claire
 * Date:    2022/2/19 - 9:54 上午
 * Description:
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:54 上午          V1.0.0
 */
package easyoa.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能简述 
 * 〈〉
 *
 * @author claire
 * @date 2022/2/19 - 9:54 上午
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private Integer id;
    private String companyName;
    private String createTime;
    private String updateTime;
}
