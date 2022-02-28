package easyoa.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by claire on 2019-07-22 - 15:15
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVO implements Serializable {
    private Integer noticeId;
    private String title;
    private String createTime;
    private String priority;
    private Boolean checked;
    private String sender;
    private String sendTime;
    private String msgContent;



}
