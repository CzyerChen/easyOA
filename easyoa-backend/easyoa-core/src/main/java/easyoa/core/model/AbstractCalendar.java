package easyoa.core.model;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by claire on 2019-07-08 - 16:19
 **/
@Data
@MappedSuperclass
public abstract class AbstractCalendar implements Serializable {
    protected String weekday;
    protected String date;
    protected String description;
    protected Integer status;
    private transient String lunarYear;
    private transient String lunar;
    private String lunarInfo;

    public abstract String getYearMonth();

    public abstract Integer getYear();

    public abstract Integer getMonth();

    public abstract Integer getDay();

    public abstract String getDayLunarInfo();

    public abstract String toString();
}
