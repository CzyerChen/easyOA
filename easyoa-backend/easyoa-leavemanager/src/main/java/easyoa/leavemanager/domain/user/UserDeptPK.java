package easyoa.leavemanager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by claire on 2019-06-27 - 17:09
 **/
@Embeddable
@NoArgsConstructor
@Data
public class UserDeptPK implements Serializable {
    private String userCode;
    private int deptId;

}
