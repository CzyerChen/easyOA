package easyoa.rulemanager.service.impl;

import easyoa.rulemanager.domain.User;
import easyoa.rulemanager.domain.UserDetail;
import easyoa.rulemanager.repository.UserDetailRepository;
import easyoa.rulemanager.repository.UserRepository;
import easyoa.rulemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by claire on 2019-07-10 - 14:00
 **/
@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDetailRepository userDetailRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetail getUserDetailById(Long userId) {
        return userDetailRepository.findByUserId(userId);
    }

    @Override
    public UserDetail getUserDetailById(String id) {
        return userDetailRepository.findById(id).orElse(null);
    }

    @Override
    public Character getUserSex(Long userId) {
        User user = userRepository.findByUserId(userId);
        if(user!= null){
            return user.getSex();
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findByDeletedAndStatus(false,1);
    }


    @Override
    public User getUser(Long userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        return userRepository.findByDeletedAndStatusAndUserIdIn(false,1,ids);
    }
}
