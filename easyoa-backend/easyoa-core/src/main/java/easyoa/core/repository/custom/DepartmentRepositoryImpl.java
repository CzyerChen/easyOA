package easyoa.core.repository.custom;

import easyoa.core.repository.DepartmentRepository;
import easyoa.common.domain.vo.DeptSearch;
import easyoa.common.utils.DateUtil;
import easyoa.core.domain.po.user.Department;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by claire on 2019-07-04 - 10:17
 **/
public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom{
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Department> findBySearchParam(DeptSearch deptSearch) {
        Specification<Department> specification = (Specification<Department>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if(deptSearch != null){
                if(Objects.nonNull(deptSearch.getCompanyId())){
                    list.add(criteriaBuilder.equal(root.get("companyId"), deptSearch.getCompanyId()));
                }
                if(StringUtils.isNotBlank(deptSearch.getDeptName())){
                    list.add(criteriaBuilder.like(root.get("deptName"), '%'+deptSearch.getDeptName()+'%'));
                }
                Date from = null;
                Date to = null;
                if (StringUtils.isNotBlank(deptSearch.getCreateTimeFrom())) {
                    from = DateUtil.parseDateWithHyphen(deptSearch.getCreateTimeFrom());
                } else {
                    from = DateUtil.parseDateWithHyphen("2010-01-01");
                }
                if (StringUtils.isNotBlank(deptSearch.getCreateTimeTo())) {
                    to = DateUtil.parseDateWithHyphen(deptSearch.getCreateTimeTo());
                } else {
                    to = new Date(System.currentTimeMillis()+3000);
                }
                list.add(criteriaBuilder.between(root.get("createTime"), from, to));
                list.add(criteriaBuilder.equal(root.get("deleted"),false));

            }
            if(list.size() != 0) {
                Predicate[] predicates = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(predicates));
            }else{
                return null;
            }
        };
        if(specification != null) {
            return departmentRepository.findAll(specification);
        }else{
            return departmentRepository.findAll();
        }
    }
}
