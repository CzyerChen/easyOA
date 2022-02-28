package easyoa.leavemanager.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-06-20 - 16:09
 **/
@Entity
@Table(name = "fb_position")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private String userCode;
}
