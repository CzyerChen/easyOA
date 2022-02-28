package easyoa.leavemanager.domain;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by claire on 2019-06-20 - 15:25
 **/
@Entity
@Table(name = "fb_global_flow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalFlow extends AbstractPOJO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer root;
    private Integer parentId;
    private Boolean deleted;
    private Integer total;
    private Integer level;
    private String content;
    private String name;
    private String assigneeIds;
    private LocalDate createTime;
    private LocalDate updateTime;
    private Integer companyId;

}
