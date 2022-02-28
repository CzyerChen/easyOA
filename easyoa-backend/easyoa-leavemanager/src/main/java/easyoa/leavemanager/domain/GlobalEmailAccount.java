package easyoa.leavemanager.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 15:20
 **/
@Entity
@Table(name = "fb_global_email")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalEmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password;
    private Boolean deleted;
    private Boolean active;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
