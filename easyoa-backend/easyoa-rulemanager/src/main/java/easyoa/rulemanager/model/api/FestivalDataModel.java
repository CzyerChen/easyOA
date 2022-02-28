package easyoa.rulemanager.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by claire on 2019-07-08 - 16:19
 *

 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FestivalDataModel {
    private String festival;
    private String desc;
    private String name;
    private String rest;
    private Integer list_num;
    private List<BaseVacationModel> list;
}
