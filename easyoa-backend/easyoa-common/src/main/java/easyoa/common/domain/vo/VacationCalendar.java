package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Created by claire on 2019-07-09 - 18:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacationCalendar implements Serializable {
    //private String date;
    private String type;
    private String content;
}
