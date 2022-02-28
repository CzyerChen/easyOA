package easyoa.core.repository;

import easyoa.core.domain.po.OperationWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-21 - 12:53
 **/
public interface OperationWatchLogRepository extends JpaRepository<OperationWatchLog, Long> {

}
