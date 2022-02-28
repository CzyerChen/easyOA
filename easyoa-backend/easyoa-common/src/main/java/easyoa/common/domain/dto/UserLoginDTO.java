package easyoa.common.domain.dto;

import easyoa.common.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

/**
 * 用户登录数据类
 * Created by claire on 2019-06-24 - 14:04
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO extends AbstractPOJO {
    private static final long serialVersionUID = 2055229953429884344L;

    /**
     * 唯一编号
      */
    private String id = RandomStringUtils.randomAlphanumeric(20);
    /**
     *  用户名
     */
    private String username;
    /**
     * ip地址
     */
    private String ip;
    /**
     * token(加密后)
     */
    private String token;
    /**
     * 登录时间
     */
    private String loginTime = DateUtil.formatOfPattern(LocalDateTime.now(),DateUtil.BASE_PATTERN);
    /**
     * 登录地点
     */
    private String loginAddress;

}
