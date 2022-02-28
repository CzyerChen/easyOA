package easyoa.rulemanager.repository;

import easyoa.rulemanager.domain.LeaveFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-10 - 17:11
 **/
public interface LeaveFileRepository extends JpaRepository<LeaveFile,Long> {

    LeaveFile findByUserIdAndFileId(Long userId,Long fileId);
}
