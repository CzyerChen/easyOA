package easyoa.core.domain.po.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-07-01 - 10:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fb_images")
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}
