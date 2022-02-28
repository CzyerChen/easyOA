package easyoa.rulemanager.domain;


import easyoa.rulemanager.model.api.AbstractCalendar;

import javax.persistence.*;

/**
 * Created by claire on 2019-07-08 - 16:30
 **/
@Entity
@Table(name = "fb_calendar")
public class DailyDetails extends AbstractCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String avoid;
    private String animalsYear;
    private String suit;

    private transient Integer year;
    private transient Integer month;
    private transient Integer day;


    public DailyDetails(){
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDayLunarInfo(){
        return getLunarYear()+" "+getLunar();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getYearMonth() {
        return year + "-"+month;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    @Override
    public Integer getMonth() {
        return month;
    }

    @Override
    public Integer getDay() {
        return day;
    }

    public String getAvoid() {
        return avoid;
    }

    public void setAvoid(String avoid) {
        this.avoid = avoid;
    }

    public String getAnimalsYear() {
        return animalsYear;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }
}
