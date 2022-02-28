package easyoa.rulemanager.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by claire on 2019-07-09 - 09:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseVacationModel {
    private String name;
    private String startday;
    private String endday;
    private String date;
    private String status;
    private LocalDate realDate;

}
