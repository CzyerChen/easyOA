/**
 * Author:   claire
 * Date:    2022/2/19 - 9:09 上午
 * Description: 公司DAO类
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:09 上午          V1.0.0          公司DAO类
 */
package easyoa.core.repository;

import easyoa.core.domain.po.user.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 功能简述 
 * 〈公司DAO类〉
 *
 * @author claire
 * @date 2022/2/19 - 9:09 上午
 * @since 1.0.0
 */
public interface CompanyRepository extends JpaRepository<Company,Integer>, JpaSpecificationExecutor<Company> {

    List<Company> findByDeleted(Boolean deleted);

    List<Company> findByDeletedAndIdIn(Boolean deleted,List<Integer> ids);
}
