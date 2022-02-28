package easyoa.filemanager.domain;

import easyoa.common.domain.dto.AbstractPOJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by claire on 2019-06-20 - 19:38
 **/
@Entity
@Table(name = "fb_resources")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resources extends AbstractPOJO {
    @Id
    private long id;
    private String path;
    private long userId;
    private String remark;
}
