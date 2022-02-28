package easyoa.rulemanager.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by claire on 2019-08-06 - 16:25
 **/
@Entity
@Table(name = "fb_user_vacation_cal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVacationCal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long userId;
    private String userCode;
    private String userName;
    private LocalDate hireDate;
    private Double annual;
    private Double sick;
    private Double sickNormal;
    private Double marriage;
    private Double materPaternity;
    private Double maternity4;
    private Double funeral;
    private Double casual;
    private Double annualShould;
    private Double annualCal;
    private Double annualRest;
    private LocalDate calculateMonth;
    private LocalDateTime calculateTime;

}
