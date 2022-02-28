package easyoa.common.model.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by claire on 2019-07-10 - 11:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage<T extends Message>  implements Serializable {
    private Long userId;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    @JsonSubTypes({ @JsonSubTypes.Type(value = ApplyMessage.class, name = "applyMessage"),
            @JsonSubTypes.Type(value = AllowenceMessage.class, name = "allowenceMessage"),
            @JsonSubTypes.Type(value = AllowenceMessage.class, name = "feedBack") })
    private T message;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

       public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
