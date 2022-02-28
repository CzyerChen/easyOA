package easyoa.leavemanager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by claire on 2019-07-17 - 09:53
 **/
@Entity
@Table(name = "fb_user_vacation")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    private Double total;
    private Double totalAll;

    private Double casualLeave;
    private Double sickLeave;
    private Double funeralLeave;
    private Double marriageLeave;
    private Double maternityLeave;
    private Double paternityLeave;
    private Double annualLeave;
    private Double sickLeaveNormal;

    private Integer weeklyApply;
    private Integer monthlyApply;

    private Double clBackup;
    private Double slBackup;
    private Double flBackup;
    private Double mlBackup;
    private Double mnlBackup;
    private Double pnlBackup;
    private Double alBackup;
    private Double slnBackup;

    private Integer year;
    private Double annualShould;
    private Double restAnnualLeave;


}
