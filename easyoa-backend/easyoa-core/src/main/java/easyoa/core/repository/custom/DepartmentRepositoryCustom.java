package easyoa.core.repository.custom;

import easyoa.common.domain.vo.DeptSearch;
import easyoa.core.domain.po.user.Department;

import java.util.List;

/**
 * Created by claire on 2019-07-04 - 10:16
 **/
public interface DepartmentRepositoryCustom {

    List<Department> findBySearchParam(DeptSearch deptSearch);
}
