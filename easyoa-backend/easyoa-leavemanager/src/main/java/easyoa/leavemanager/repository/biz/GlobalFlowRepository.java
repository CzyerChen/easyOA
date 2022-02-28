package easyoa.leavemanager.repository.biz;

import easyoa.leavemanager.domain.GlobalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by claire on 2019-07-12 - 13:53
 **/
public interface GlobalFlowRepository extends JpaRepository<GlobalFlow,Integer>, JpaSpecificationExecutor<GlobalFlow> {

    List<GlobalFlow> findByRoot(Integer root);

    List<GlobalFlow> findByRootAndCompanyId(Integer root,Integer companyId);

    List<GlobalFlow> findByParentId(Integer parentId);

    List<GlobalFlow> findByParentIdAndCompanyId(Integer parentId,Integer companyId);

    List<GlobalFlow> findByParentIdIn(List<Integer> parentIds);

    List<GlobalFlow> findByDeleted(boolean deleted);

    GlobalFlow findFirstByRootAndDeletedAndCompanyId(Integer root,boolean deleted,Integer companyId);

    GlobalFlow findFirstByRootAndDeletedOrderByIdDesc(Integer root,boolean deleted);

    List<GlobalFlow> findByRootAndDeletedAndAssigneeIdsNotLike(Integer root,Boolean deleted,String assigneeIds);

    List<GlobalFlow>  findByDeletedAndRootAndIdGreaterThan(Boolean deleted,Integer root,Integer id);


}
