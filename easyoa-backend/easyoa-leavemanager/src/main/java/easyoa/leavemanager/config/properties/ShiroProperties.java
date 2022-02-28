package easyoa.leavemanager.config.properties;

import lombok.Data;

/**
 * Created by claire on 2019-06-21 - 11:42
 **/
public class ShiroProperties {
    /**
     * token默认有效时间 1天
     */
    private Long jwtTimeOut = 86400L;
    private String anonUrl;

    public Long getJwtTimeOut() {
        return jwtTimeOut;
    }

    public void setJwtTimeOut(Long jwtTimeOut) {
        this.jwtTimeOut = jwtTimeOut;
    }

    public String getAnonUrl() {
        return anonUrl;
    }

    public void setAnonUrl(String anonUrl) {
        this.anonUrl = anonUrl;
    }
}
