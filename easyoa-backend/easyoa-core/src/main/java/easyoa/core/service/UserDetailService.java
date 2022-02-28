package easyoa.core.service;

import easyoa.core.domain.po.user.UserDetail;

import java.util.List;

/**
 * Created by claire on 2019-06-27 - 17:54
 **/
public interface UserDetailService {
    void saveUserDetail(List<UserDetail> list);

    UserDetail findById(String id);

    UserDetail findByEmail(String email);

    UserDetail saveUserDetail(UserDetail userDetail);

    UserDetail findbyUserId(Long userId);

    void clearUserIds(String[] userIds);

    List<UserDetail> findAll();

    List<UserDetail> findByChineseNameIn(List<String> names);
}
