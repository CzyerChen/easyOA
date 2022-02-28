package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.GlobalEmailAccount;
import easyoa.leavemanager.service.EmailService;
import easyoa.common.domain.vo.MailVO;
import easyoa.common.utils.DateUtil;
import easyoa.leavemanager.repository.GlobalEmailAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-08-05 - 11:57
 **/
@Service(value = "emailService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

    @Autowired
    private GlobalEmailAccountRepository globalEmailAccountRepository;

    @Transactional
    @Override
    public void saveGlobalMail(GlobalEmailAccount globalEmailAccount) {
        globalEmailAccountRepository.save(globalEmailAccount);
    }

    @Transactional
    @Override
    public void updateGlobalMail(GlobalEmailAccount globalEmailAccount) {
        globalEmailAccount.setUpdateTime(LocalDateTime.now());
        globalEmailAccountRepository.save(globalEmailAccount);
    }

    @Transactional
    @Override
    public void deleteGlobalMail(List<Integer> ids) {
        List<GlobalEmailAccount> all = globalEmailAccountRepository.findAllById(ids);
        if (all != null && all.size() != 0) {
            globalEmailAccountRepository.deleteAll(all);
        }
    }

    @Override
    public GlobalEmailAccount findFirstValidAccount() {
        return globalEmailAccountRepository.findFirstByActive(true);
    }

    @Override
    public GlobalEmailAccount findById(Integer id) {
        return globalEmailAccountRepository.findById(id).orElse(null);
    }

    @Override
    public GlobalEmailAccount findAccountByEmail(String email) {
        return globalEmailAccountRepository.findByEmailAndDeleted(email, false);
    }

    @Override
    public List<GlobalEmailAccount> findAllMails() {
        return globalEmailAccountRepository.findAll();
    }

    @Override
    public Page<MailVO> findPageMails(Pageable pageable) {
        Page<GlobalEmailAccount> all = globalEmailAccountRepository.findAll(pageable);
        if (all != null && all.getContent() != null && all.getContent().size() != 0) {
            List<MailVO> list = all.getContent().stream().map(a -> {
                MailVO mailVO = new MailVO();
                mailVO.setId(a.getId());
                mailVO.setEmail(a.getEmail());
                mailVO.setActive(a.getActive());
                mailVO.setPassword(a.getPassword());
                mailVO.setCreateTime(DateUtil.format(a.getCreateTime()));
                mailVO.setUpdateTime(DateUtil.format(a.getUpdateTime()));
                return mailVO;
            }).collect(Collectors.toList());
            return new PageImpl<>(list,pageable,all.getTotalElements());
        }
        return null;
    }
}
