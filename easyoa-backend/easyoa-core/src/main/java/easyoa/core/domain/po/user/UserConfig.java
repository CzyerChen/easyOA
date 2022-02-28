package easyoa.core.domain.po.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户配置类
 * Created by claire on 2019-06-24 - 15:24
 **/
@Entity
@Table(name = "fb_user_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConfig implements Serializable {

    /**
     * 用户 ID
     */
    @Id
    private Long userId;

    /**
     * 系统主题 dark暗色风格，light明亮风格
     */
    private String theme;

    /**
     * 系统布局 side侧边栏，head顶部栏
     */
    private String layout;

    /**
     * 页面风格 1多标签页 0单页
     */
    private String multiPage;

    /**
     * 页面滚动是否固定侧边栏 1固定 0不固定
     */
    private String fixSiderbar;

    /**
     * 页面滚动是否固定顶栏 1固定 0不固定
     */
    private String fixHeader;

    /**
     * 主题颜色 RGB值
     */
    private String color;
}
