/**
 * Author:   claire
 * Date:    2022/2/19 - 9:08 上午
 * Description: 公司业务实现类
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2022/2/19 - 9:08 上午          V1.0.0          公司业务实现类
 */
package easyoa.leavemanager.service.impl;

import easyoa.common.domain.dto.CompanyDTO;
import easyoa.common.domain.vo.CompanySearch;
import easyoa.core.domain.po.user.Company;
import easyoa.core.domain.po.user.Department;
import easyoa.core.repository.CompanyRepository;
import easyoa.core.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能简述 
 * 〈公司业务实现类〉
 *
 * @author claire
 * @date 2022/2/19 - 9:08 上午
 * @since 1.0.0
 */
@Slf4j
@Service("companyService")
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Company save(Company company) {
        company.setCreateTime(new Date());
        company.setUpdateTime(company.getCreateTime());
        company.setDeleted(Boolean.FALSE);
        return companyRepository.save(company);
    }

    @Override
    public void deleteById(Integer id) {
        Optional<Company> company = companyRepository.findById(id);
        if(company.isPresent()){
            Company cpy = company.get();
            cpy.setDeleted(Boolean.TRUE);
            companyRepository.save(cpy);
        }
    }

    @Override
    public void deleteByIdIn(List<Integer> ids) {
        List<Company> companies = companyRepository.findAllById(ids);
        if(!CollectionUtils.isEmpty(companies)){
            companies.forEach(d->d.setDeleted(Boolean.TRUE));
            companyRepository.saveAll(companies);
        }
    }

    @Override
    public Company update(Company company) {
        Optional<Company> companyOptional = companyRepository.findById(company.getId());
        if(companyOptional.isPresent()) {
            Company cpy = companyOptional.get();
            cpy.setCompanyName(company.getCompanyName());
            cpy.setUpdateTime(new Date());
            return companyRepository.save(cpy);
        }
        return null;
    }

    @Override
    public Page<CompanyDTO> findAll(Pageable pageable, CompanySearch companySearch) {
        Specification<Company> specification = (Specification<Company>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            predicatesList.add(criteriaBuilder.equal(root.get("deleted").as(Boolean.class), Boolean.FALSE));

            if(!StringUtils.isEmpty(companySearch.getCompanyName())){
                predicatesList.add(criteriaBuilder.like(root.get("companyName").as(String.class), "%"+companySearch.getCompanyName()+"%"));
            }
            if(!StringUtils.isEmpty(companySearch.getCreateTimeFrom())){
                predicatesList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(String.class), companySearch.getCreateTimeFrom()+" 00:00:00"));
            }
            if(!StringUtils.isEmpty(companySearch.getCreateTimeTo())){
                predicatesList.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(String.class), companySearch.getCreateTimeTo()+" 23:59:59"));
            }
            Predicate[] predicates = new Predicate[predicatesList.size()];
            return criteriaBuilder.and(predicatesList.toArray(predicates));
        };
        Page<Company> companyPage = companyRepository.findAll(specification, pageable);
        Pageable pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "userId");
        List<CompanyDTO> list =new ArrayList<>();
        if (companyPage.getContent().size() != 0) {
             list = companyPage.getContent().stream().map(Company::toDto).collect(Collectors.toList());
        }
        return new PageImpl<>(list, pageRequest, companyPage.getTotalElements());
    }

    @Override
    public List<CompanyDTO> findAll() {
        List<Company> companies = companyRepository.findAll();
        if(!CollectionUtils.isEmpty(companies)){
            return companies.stream().map(Company::toDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void fillCompanyNames(List<Department> list) {
        List<Integer> companyIds = list.stream().map(Department::getCompanyId).distinct().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(companyIds)){
            List<Company> companies = companyRepository.findByDeletedAndIdIn(Boolean.FALSE, companyIds);
            if(!CollectionUtils.isEmpty(companies)){
                Map<Integer, String> nameMap = companies.stream().collect(Collectors.toMap(Company::getId, Company::getCompanyName));
                list.forEach(d ->{
                        d.setCompanyName(nameMap.getOrDefault(d.getCompanyId(),null));
                });
            }
        }
    }

    @Override
    public String findNameById(Integer companyId) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        return companyOptional.map(Company::getCompanyName).orElse(null);
    }

    @Override
    public List<CompanyDTO> findByIds(List<Integer> ids) {
        List<Company> companies = companyRepository.findAllById(ids);
        if(!CollectionUtils.isEmpty(companies)){
            return companies.stream().map(Company::toDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
