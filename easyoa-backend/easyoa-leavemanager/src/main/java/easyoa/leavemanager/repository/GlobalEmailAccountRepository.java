package easyoa.leavemanager.repository;

import easyoa.leavemanager.domain.GlobalEmailAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by claire on 2019-08-05 - 14:42
 **/
public interface GlobalEmailAccountRepository  extends JpaRepository<GlobalEmailAccount,Integer> {

    GlobalEmailAccount findFirstByActive(Boolean active);

    GlobalEmailAccount findByEmailAndDeleted(String email,Boolean deleted);
}
