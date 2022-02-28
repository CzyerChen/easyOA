package easyoa.common.constant;

/**
 * Created by claire on 2019-07-16 - 09:42
 **/
public enum FlowTypeEnum {
    TRANSFER(0,"流转","RUNNING"),
        OVER(1,"结束","OVER"),
    TERMINATE(2,"终止","OVER"),
        REDO(3,"重做","RUNNING");

    private Integer id;
    private String value;
    private String status;

    FlowTypeEnum(Integer id,String value,String status){
        this.id = id;
        this.value = value;
        this.status = status;
    }

    public static String getStatusInfo(Integer id){
        /**
         * zero based
         */
        FlowTypeEnum value = FlowTypeEnum.values()[id];
        return value.getStatus();
    }

    private String getStatus(){
        return this.status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

   public static void main(String[] args){
       String statusInfo = FlowTypeEnum.getStatusInfo(2);
       System.out.println(statusInfo);
   }
}
