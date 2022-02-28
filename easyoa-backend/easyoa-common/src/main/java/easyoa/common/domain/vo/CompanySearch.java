/**
 * Author:   claire
 * Date:    2022/2/19 - 9:57 上午
 * Description: 公司查询对象
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:57 上午          V1.0.0          公司查询对象
 */
package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能简述 
 * 〈公司查询对象〉
 *
 * @author claire
 * @date 2022/2/19 - 9:57 上午
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanySearch {
    private String companyName;
    private String createTimeFrom;
    private String createTimeTo;
}
