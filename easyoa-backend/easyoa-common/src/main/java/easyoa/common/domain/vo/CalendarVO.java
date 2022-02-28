/**
 * Author:   claire
 * Date:    2022/2/11 - 1:43 下午
 * Description: 日历数据对象
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/11 - 1:43 下午          V1.0.0          日历数据对象
 */
package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能简述 
 * 〈日历数据对象〉
 *
 * @author claire
 * @date 2022/2/11 - 1:43 下午
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarVO {
    private String type;
    private String content ="";
    private String day;
    private Integer hit;
}
