package easyoa.leavemanager.service;

import easyoa.leavemanager.domain.GlobalEmailAccount;
import easyoa.common.domain.vo.MailVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by claire on 2019-08-05 - 11:56
 **/
public interface EmailService {


    void saveGlobalMail(GlobalEmailAccount globalEmailAccount);

    void updateGlobalMail(GlobalEmailAccount globalEmailAccount);

    void deleteGlobalMail(List<Integer> ids);

    List<GlobalEmailAccount>  findAllMails();

    Page<MailVO> findPageMails(Pageable pageable);

    GlobalEmailAccount findFirstValidAccount();

    GlobalEmailAccount findById(Integer id);

    GlobalEmailAccount findAccountByEmail(String email);
}
