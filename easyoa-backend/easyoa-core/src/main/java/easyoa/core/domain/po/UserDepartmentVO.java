package easyoa.core.domain.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Created by claire on 2019-06-26 - 22:08
 **/
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDepartmentVO {
    private Long userId;
    private String username;
    private Integer deptId;
    private String deptName;

}
