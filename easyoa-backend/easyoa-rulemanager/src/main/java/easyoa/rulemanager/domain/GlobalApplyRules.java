package easyoa.rulemanager.domain;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by claire on 2019-06-20 - 18:31
 **/
@Entity
@Table(name = "fb_global_apply_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalApplyRules extends AbstractPOJO {
    @Id
    private int id;
    private String range;
    private int days;
    private String description;
    private Date createTime;
    private Date updateTime;
}
