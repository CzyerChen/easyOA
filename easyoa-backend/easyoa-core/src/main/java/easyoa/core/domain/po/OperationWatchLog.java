package easyoa.core.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 19:35
 **/
@Entity
@Table(name = "fb_operation_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationWatchLog {
    /**
     *日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     *用户名
     */
    private String username;
    /**
     * 操作
     */
    private String operation;
    /**
     * 执行时长
     */
    private long time;
    /**
     * 方法
     */
    private String method;
    /**
     * 参数
     */
    private String params;
    /**
     * IP
     */
    private String ip;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 地址
     */
    private String location;

 }