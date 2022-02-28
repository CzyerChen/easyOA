package easyoa.core.service;

import easyoa.core.domain.po.user.UserConfig;

import java.util.List;

/**
 * Created by claire on 2019-06-26 - 17:15
 **/
public interface UserConfigService {

    List<UserConfig> getUserConfigs();

    UserConfig getUserConfigById(long userId);

    void initDefaultUserConfig(long userId);

    UserConfig saveUserConfig(UserConfig userConfig);

    void removeUserConfigs(List<UserConfig> configs);

    void refreshUserConfigs();

}
