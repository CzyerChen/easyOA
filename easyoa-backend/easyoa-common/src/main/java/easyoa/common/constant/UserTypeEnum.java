package easyoa.common.constant;

/**
 * Created by claire on 2019-06-28 - 11:57
 **/
public enum UserTypeEnum {
    REGULAR(1, "正式员工"),
    OUTER(2, "借调员工");

    private int id;
    private String name;

    UserTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId(){
        return  this.id;
    }
}

