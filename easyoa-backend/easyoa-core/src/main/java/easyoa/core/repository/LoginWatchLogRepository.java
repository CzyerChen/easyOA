package easyoa.core.repository;

import easyoa.core.domain.po.LoginWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-06-21 - 13:26
 **/
public interface LoginWatchLogRepository extends JpaRepository<LoginWatchLog,Long> {
}
