package easyoa.leavemanager.repository.biz;

import easyoa.leavemanager.domain.biz.ApplicationFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by claire on 2019-06-21 - 17:08
 **/
public interface ApplicationFlowRepository extends JpaRepository<ApplicationFlow, Integer> {
    ApplicationFlow findFirstByApplicationId(Long applicationId);

    List<ApplicationFlow> findByApplicationIdIn(List<Long> applicaitonIds);

    List<ApplicationFlow> findByTypeAndApplicationIdIn(Integer type,Long applicaitonId);

    List<ApplicationFlow> findByApplicationId(Long applicationId);

    List<ApplicationFlow> findByAssignee(Long assignee);

    List<ApplicationFlow> findByAssigneeAndStatusIsNullAndFlowIdIsNotNull(Long assignee);

    List<ApplicationFlow> findByAssigneeAndStatusIsNotNullAndFlowIdIsNotNull(Long assignee);

    ApplicationFlow findFirstByApplicationIdAndFlowIdIsNotNullOrderByIdDesc(Long applicationId);

    Page<ApplicationFlow> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
}
