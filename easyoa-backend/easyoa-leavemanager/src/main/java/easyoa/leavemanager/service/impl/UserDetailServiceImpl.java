package easyoa.leavemanager.service.impl;

import easyoa.core.domain.po.user.UserDetail;
import easyoa.core.repository.UserDetailRepository;
import easyoa.core.service.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-06-27 - 17:55
 **/
@Slf4j
@Service("userDetailService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserDetailServiceImpl implements UserDetailService {
    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public void saveUserDetail(List<UserDetail> list) {
       userDetailRepository.saveAll(list);
    }

    @Override
    public UserDetail findById(String id) {
        Optional<UserDetail> userDetail = userDetailRepository.findById(id);
        return userDetail.orElse(null);
    }

    @Override
    public UserDetail findByEmail(String email) {
        return userDetailRepository.findFirstByEmail(email);
    }

    @Override
    public UserDetail saveUserDetail(UserDetail userDetail) {
       return  userDetailRepository.save(userDetail);
    }

    @Override
    public UserDetail findbyUserId(Long userId) {
        return userDetailRepository.findByUserId(userId);
    }

    @Override
    public void clearUserIds(String[] userIds) {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(userIds));
        if(list!=null) {
            List<Long> list1 = list.stream().map(Long::valueOf).distinct().collect(Collectors.toList());
            if(list1!= null){
                List<UserDetail> userDetails = userDetailRepository.findByUserIdIn(list1);
                userDetails.forEach(u ->u.setUserId(0L));
                userDetailRepository.saveAll(userDetails);
            }
        }
    }

    @Override
    public List<UserDetail> findAll() {
        return userDetailRepository.findAll();
    }

    @Override
    public List<UserDetail> findByChineseNameIn(List<String> names) {
        return userDetailRepository.findByChineseNameInAndDeleted(names,Boolean.FALSE);
    }

}
