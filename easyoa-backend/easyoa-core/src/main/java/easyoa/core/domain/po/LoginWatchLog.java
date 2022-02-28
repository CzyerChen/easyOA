package easyoa.core.domain.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 13:56
 **/
@Entity
@Table(name = "fb_login_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginWatchLog {
    /**
     * 日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * IP
     */
    private String ip;
    /**
     * 地址
     */
    private String location;
    /**
     * 设备
     */
    private String device;
    /**
     * 创建时间
     */
    private Date createTime;
}
