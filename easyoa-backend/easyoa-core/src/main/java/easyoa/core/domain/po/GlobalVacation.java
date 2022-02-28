package easyoa.core.domain.po;


import easyoa.core.model.AbstractFestival;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-06-20 - 18:37
 **/
@Entity
@Table(name = "fb_global_vacation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalVacation extends AbstractFestival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer days;
    private String description;
    private String advice;
    private Boolean finish;
    private String detail;

    @Override
    public Integer totalDays() {
        return null;
    }

    @Override
    public boolean needAjust() {
        return false;
    }
}
