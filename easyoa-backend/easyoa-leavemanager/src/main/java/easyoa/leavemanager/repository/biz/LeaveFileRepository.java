package easyoa.leavemanager.repository.biz;

import easyoa.leavemanager.domain.biz.LeaveFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-15 - 18:31
 **/
public interface LeaveFileRepository extends JpaRepository<LeaveFile,Long> {

    LeaveFile findByFileId(Long fileId);
}
