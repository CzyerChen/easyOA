package easyoa.leavemanager.config.database;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Created by claire on 2019-06-21 - 14:24
 **/
@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DruidConfig {

    /**
     * 数据库地址
     */
    private String url;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 初始化连接数量
     */
    private int initialSize;

    /**
     * 最小闲置连接
     */
    private int minIdle;

    /**
     * 最大存活连接
     */
    private int maxActive;

    /**
     * 配置获取连接等待超时的时间
     */
    private long maxWait;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis;

    /**
     * 配置一个连接在池中最大生存的时间，单位是毫秒
     */
    private long maxEvictableIdleTimeMillis;

    /**
     *
     */
    private boolean testWhileIdle;

    /**
     *
     */
    private boolean testOnBorrow;

    /**
     *
     */
    private boolean testOnReturn;

    /**
     *
     */
    private boolean poolPreparedStatements;

    /**
     *
     */
    private int maxOpenPreparedStatements;

    /**
     *
     */
    private boolean asyncInit;


    @Bean
    public DruidDataSource druidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
        druidDataSource.setAsyncInit(asyncInit);
        return druidDataSource;
    }
}
