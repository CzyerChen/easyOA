package easyoa.common.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by claire on 2019-07-22 - 09:37
 **/
@Data
public class WeekData {
    // 一周的开始时间
    private LocalDate start;
    // 一周的结束时间
    private LocalDate end;

    public WeekData(List<LocalDate> localDates) {
        this.start = localDates.get(0);
        this.end = localDates.get(localDates.size()-1);
    }

    @Override
    public String toString() {
        return "开始时间：" + this.start + "，结束时间：" + this.end;
    }

}
