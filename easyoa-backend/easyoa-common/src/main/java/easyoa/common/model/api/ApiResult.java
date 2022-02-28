package easyoa.common.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-08 - 16:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private String reason;
    private ResultData<T> result;
    private Integer error_code;


}
