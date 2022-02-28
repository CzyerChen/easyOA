/**
 * Author:   claire
 * Date:    2020-01-21 - 11:22
 * Description: 休假细节接口类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.leavemanager.service;

import easyoa.core.domain.po.GlobalVacationDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 功能简述 <br/> 
 * 〈休假细节接口类〉
 *
 * @author claire
 * @date 2020-01-21 - 11:22
 * @since 1.3.0
 */
public interface VacationDetailService {
    void saveVacationDetails( Set<LocalDate> vacationDate);

    List<GlobalVacationDetail> findAllVacations(Integer year);
}
