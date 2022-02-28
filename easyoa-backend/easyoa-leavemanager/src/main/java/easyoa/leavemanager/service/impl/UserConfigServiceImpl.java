package easyoa.leavemanager.service.impl;

import easyoa.common.constant.UserConstant;
import easyoa.core.domain.po.user.UserConfig;
import easyoa.core.repository.UserConfigRepository;
import easyoa.core.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by claire on 2019-06-26 - 17:16
 **/
@Slf4j
@Service("userConfigService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserConfigServiceImpl implements UserConfigService {
    @Autowired
    private UserConfigRepository userConfigRepository;

    @Override
    public List<UserConfig> getUserConfigs() {
        return userConfigRepository.findAll();
    }

    @Override
    public UserConfig getUserConfigById(long userId){
        return  userConfigRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void initDefaultUserConfig(long userId) {
        UserConfig userConfig = new UserConfig();
        userConfig.setUserId(userId);
        userConfig.setColor(UserConstant.DEFAULT_COLOR);
        userConfig.setFixHeader(UserConstant.DEFAULT_FIX_HEADER);
        userConfig.setFixSiderbar(UserConstant.DEFAULT_FIX_SIDERBAR);
        userConfig.setLayout(UserConstant.DEFAULT_LAYOUT);
        userConfig.setTheme(UserConstant.DEFAULT_THEME);
        userConfig.setMultiPage(UserConstant.DEFAULT_MULTIPAGE);
       userConfigRepository.save(userConfig);
    }

    @Override
    public UserConfig saveUserConfig(UserConfig userConfig) {
        return userConfigRepository.save(userConfig);
    }

    @Override
    public void removeUserConfigs(List<UserConfig> configs) {
         userConfigRepository.deleteAll(configs);
    }

    @Override
    public void refreshUserConfigs() {

    }
}
