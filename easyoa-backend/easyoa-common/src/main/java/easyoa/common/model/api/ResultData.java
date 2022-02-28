package easyoa.common.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-08 - 17:27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultData<T> {
    private T data;

}
