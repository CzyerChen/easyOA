package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by claire on 2019-07-21 - 15:52
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationOverall {
    private String name;
    private String content;
}
