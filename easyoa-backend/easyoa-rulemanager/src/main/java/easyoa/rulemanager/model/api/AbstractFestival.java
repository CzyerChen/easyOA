package easyoa.rulemanager.model.api;

import easyoa.common.model.api.IAjustable;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by claire on 2019-07-08 - 16:25
 **/
@Data
@MappedSuperclass
public abstract class AbstractFestival  implements IAjustable, Serializable {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate festival;

    public abstract Integer totalDays();

    public String getYearMonth(){
        return null;
    }

    public Integer getYear(){
        return  null;
    }

    public Integer getMonth(){
        return null;
    }

    public Integer getDay(){
        return null;
    }

    public String getDayLunarInfo(){
        return null;
    }

    public String toString(){
        return  null;
    }

}
