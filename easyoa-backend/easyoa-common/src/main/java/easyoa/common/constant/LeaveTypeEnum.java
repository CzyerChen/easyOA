package easyoa.common.constant;

/**
 * Created by claire on 2019-10-05 - 14:38
 **/
public enum LeaveTypeEnum {
    /**

     */
    CASUAL_LEAVE("事假",
            "setCasualLeave",
            "setClBackup",
            "getCasualLeave",
            "getClBackup",
            "setCasual",
            "getCasual"),
    /**

     */
    SICK_LEAVE("带薪病假",
            "setSickLeave",
            "setSlBackup",
            "getSickLeave",
            "getSlBackup",
            "setSick",
            "getSick"),
    /**

     */
    SICK_LEAVE_NORMAL("普通病假",
            "setSickLeaveNormal",
            "setSlnBackup",
            "getSickLeaveNormal",
    "getSlnBackup",
            "setSickNormal",
            "getSickNormal"),
    /**

     */
    MARRIAGE_LEAVE("婚假",
            "setMarriageLeave",
            "setMlBackup",
            "getMarriageLeave",
            "getMlBackup",
            "setMarriage",
            "getMarriage"),
    /**

     */
    FUNERAL_LEAVE("丧假",
            "setFuneralLeave",
            "setFlBackup",
            "getFuneralLeave",
            "getFlBackup",
            "setFuneral",
            "getFuneral"),
    /**

     */
    MATERNITY_LEAVE("产假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
            "getMnlBackup",
            "setMaternity",
            "getMaternity"),
    /**

     */
    MATERNITY_LEAVE_1("产假-难产假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
            "getMnlBackup",
            "",
            ""),
    /**

     */
    MATERNITY_LEAVE_2("产假-多胞胎假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
            "getMnlBackup",
            "",
            ""),
    /**

     */
    MATERNITY_LEAVE_3("产假-哺乳假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
    "getMnlBackup",
            "",
            ""),
    /**

     */
    MATERNITY_LEAVE_4("产假-产检假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
            "getMnlBackup",
            "setMaterPaternity",
            "getMaterPaternity"),
    /**

     */
    MATERNITY_LEAVE_5("产假-流产假",
            "setMaternityLeave",
            "setMnlBackup",
            "getMaternityLeave",
            "getMnlBackup",
            "",
            ""),
    /**

     */
    PATERNITY_LEAVE("陪产假",
            "setPaternityLeave",
            "setPnlBackup",
            "getPaternityLeave",
            "getPnlBackup",
            "setMaterPaternity",
            "getMaterPaternity"),
    /**

     */
    ANNUAL_LEAVE("年假",
            "setAnnualLeave",
            "setAlBackup",
            "getAnnualLeave",
            "getAlBackup",
            "setAnnual",
            "getAnnual");


    private String name;
    private String setMethod;
    private String setSubMethod;
    private String getMethod;
    private String getSubMethod;
    private String setCalMethod;
    private String getCalMethod;

    LeaveTypeEnum(String name,
                  String setMethod,
                  String setSubMethod,
                  String getMethod,
                  String getSubMethod,
                  String setCalMethod,
                  String getCalMethod) {
        this.name = name;
        this.setMethod = setMethod;
        this.setSubMethod = setSubMethod;
        this.getMethod = getMethod;
        this.getSubMethod = getSubMethod;
        this.setCalMethod = setCalMethod;
        this.getCalMethod = getCalMethod;
    }

    public String getName() {
        return name;
    }

    public static String getMethodByNameForSet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.setMethod;
            }
        }
        return null;
    }

    public static String getSubMethodByNameForSet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.setSubMethod;
            }
        }
        return null;
    }

    public static String getMethodByNameForGet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.getMethod;
            }
        }
        return null;
    }

    public static String getSubMethodByNameForGet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.getSubMethod;
            }
        }
        return null;
    }

    public static String getMethodByNameCalForSet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.setCalMethod;
            }
        }
        return null;
    }

    public static String getMethodByNameCalForGet(String name) {
        for (LeaveTypeEnum leaveType : LeaveTypeEnum.values()) {
            if (leaveType.name.equals(name)) {
                return leaveType.getCalMethod;
            }
        }
        return null;
    }
}