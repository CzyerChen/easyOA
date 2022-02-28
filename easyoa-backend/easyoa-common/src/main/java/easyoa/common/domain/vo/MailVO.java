package easyoa.common.domain.vo;

import lombok.Data;

/**
 * Created by claire on 2019-08-05 - 16:29
 **/
@Data
public class MailVO {
    private Integer id;
    private String email;
    private String password;
    private Boolean active;
    private String createTime;
    private String updateTime;
}
