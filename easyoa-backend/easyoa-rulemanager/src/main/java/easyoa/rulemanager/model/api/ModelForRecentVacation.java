package easyoa.rulemanager.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by claire on 2019-07-08 - 16:54
 * {
 * 	"reason":"Success",
 * 	"result":{
 * 		"data":{
 * 			"year":"2019",
 * 			"year-month":"2019-9",
 * 			"holiday":"[{\"desc\":\"9月13日放假，与周末连休。\",\"festival\":\"2019-9-13\",\"list\":[{\"date\":\"2019-9-13\",\"status\":\"1\"},{\"date\":\"2019-9-14\",\"status\":\"1\"},{\"date\":\"2019-9-15\",\"status\":\"1\"}],\"list#num#\":3,\"name\":\"中秋节\",\"rest\":\"拼假建议：2019年9月9日（周一）~2019年9月12日（周四）请假4天，可拼9天中秋节小长假\"},{\"desc\":\"10月1日至10月7日放假，9月29日（星期日）、10月12日（星期六）上班。\",\"festival\":\"2019-10-1\",\"list\":[{\"date\":\"2019-10-1\",\"status\":\"1\"},{\"date\":\"2019-9-29\",\"status\":\"2\"},{\"date\":\"2019-10-2\",\"status\":\"1\"},{\"date\":\"2019-10-3\",\"status\":\"1\"},{\"date\":\"2019-10-4\",\"status\":\"1\"},{\"date\":\"2019-10-7\",\"status\":\"1\"},{\"date\":\"2019-10-12\",\"status\":\"2\"},{\"date\":\"2019-10-5\",\"status\":\"1\"},{\"date\":\"2019-10-6\",\"status\":\"1\"}],\"list#num#\":9,\"name\":\"国庆节\",\"rest\":\"拼假建议：10月8日（周二）~10月12日（周六）请5天假，可拼13天国庆节小长假。\"}]",
 * 			"holiday_array":[
 *                                {
 * 					"desc":"9月13日放假，与周末连休。",
 * 					"festival":"2019-9-13",
 * 					"list":[
 *                        {
 * 							"date":"2019-9-13",
 * 							"status":"1"
 *                        },
 *                        {
 * 							"date":"2019-9-14",
 * 							"status":"1"
 *                        },
 *                        {
 * 							"date":"2019-9-15",
 * 							"status":"1"
 *                        }
 * 					],
 * 					"list#num#":3,
 * 					"name":"中秋节",
 * 					"rest":"拼假建议：2019年9月9日（周一）~2019年9月12日（周四）请假4天，可拼9天中秋节小长假",
 * 					"list_num":3
 *                },
 *                ....
 *  			]
 *                }* 	},
 * 	"error_code":0
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelForRecentVacation {
    private String year;
    private String holiday;
    private List<FestivalDataModel> holiday_array;


}
