package easyoa.leavemanager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-06-27 - 17:06
 **/
@Entity
@Table(name = "fb_user_dept")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDepartment {
   /* @EmbeddedId
    private UserDeptPK id;*/
   @Id
   private String userName;
   private int deptId;
}
