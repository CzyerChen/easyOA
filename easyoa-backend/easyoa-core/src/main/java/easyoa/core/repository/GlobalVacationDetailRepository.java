/**
 * Author:   claire
 * Date:    2020-01-21 - 11:25
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package easyoa.core.repository;

import easyoa.core.domain.po.GlobalVacationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 功能简述 <br/> 
 * 〈〉
 *
 * @author claire
 * @date 2020-01-21 - 11:25
 * @since 1.3.0
 */
public interface GlobalVacationDetailRepository extends JpaRepository<GlobalVacationDetail,Integer> {

    List<GlobalVacationDetail> findByYear(Integer year);
}
