package easyoa.common.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by claire on 2019-07-10 - 11:10
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Message implements Serializable {
    @JsonProperty("name")
    protected String name;
    @JsonProperty("type")
    protected String type;
    //申请天数
    @JsonProperty("days")
    protected Double days;



    public abstract String detail();
}
