package easyoa.rulemanager.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by claire on 2019-07-08 - 16:54
 *  *
 *  * "reason":"Success",
 *  * 	"result":{
 *  * 		"data":{
 *  * 			"year":"2019",
 *  * 			"holidaylist":"[{\"name\":\"元旦\",\"startday\":\"2019-1-1\"},{\"name\":\"除夕\",\"startday\":\"2019-2-4\"},{\"name\":\"春节\",\"startday\":\"2019-2-5\"},{\"name\":\"清明节\",\"startday\":\"2019-4-5\"},{\"name\":\"劳动节\",\"startday\":\"2019-5-1\"},{\"name\":\"端午节\",\"startday\":\"2019-6-7\"},{\"name\":\"中秋节\",\"startday\":\"2019-9-13\"},{\"name\":\"国庆节\",\"startday\":\"2019-10-1\"}]",
 *  * 			"holiday_list":[
 *  *                                {
 *  * 					"name":"元旦",
 *  * 					"startday":"2019-1-1"
 *  *                },
 *  *            ........
 *  *             ]
 *  *                }* 	},
 *  * 	"error_code":0
 *  * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelForYearVacation {
    private String year;
    private String holidaylist;
    private List<BaseVacationModel> holiday_list;
}
