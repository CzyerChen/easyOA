package easyoa.leavemanager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-07-26 - 10:03
 **/
@Entity
@Table(name = "fb_user_reporter")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userCode;
    private String userName;
    private String reporterCode;
    private String reporterName;

    public UserReporter(String userCode, String userName, String reporterCode, String reporterName) {
        this.userCode = userCode;
        this.userName = userName;
        this.reporterCode = reporterCode;
        this.reporterName = reporterName;
    }
}
