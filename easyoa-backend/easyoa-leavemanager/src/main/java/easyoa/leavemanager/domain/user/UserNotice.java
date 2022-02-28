package easyoa.leavemanager.domain.user;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 17:08
 **/
@Entity
@Table(name = "fb_notice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long userId;
    private String title;
    private String message;
    private LocalDate createDate;
    private Boolean checked;
    private String sender;
    private LocalDateTime sendTime;
    private Integer priority;
    private String type;
}
