/**
 * Author:   claire
 * Date:    2022/2/19 - 9:07 上午
 * Description: 公司业务类
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:07 上午          V1.0.0          公司业务类
 */
package easyoa.core.service;

import easyoa.common.domain.dto.CompanyDTO;
import easyoa.common.domain.vo.CompanySearch;
import easyoa.core.domain.po.user.Company;
import easyoa.core.domain.po.user.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 功能简述 
 * 〈公司业务类〉
 *
 * @author claire
 * @date 2022/2/19 - 9:07 上午
 * @since 1.0.0
 */
public interface CompanyService {
    Company save(Company company);

    void deleteById(Integer id);

    void deleteByIdIn(List<Integer> ids);

    Company update(Company company);

    Page<CompanyDTO> findAll(Pageable pageable, CompanySearch companySearch);

    List<CompanyDTO> findAll();

    void fillCompanyNames(List<Department> list);

    String findNameById(Integer companyId);

    List<CompanyDTO> findByIds(List<Integer> ids);

}
