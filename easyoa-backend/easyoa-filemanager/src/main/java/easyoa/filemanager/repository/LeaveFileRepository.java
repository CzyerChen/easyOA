package easyoa.filemanager.repository;

import easyoa.filemanager.domain.LeaveFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-07-11 - 11:56
 **/
public interface LeaveFileRepository extends JpaRepository<LeaveFile,Long> {

    LeaveFile findByFileCurrentName(String fileCurrentName);

    LeaveFile findByFileId(Long fileId);
}
