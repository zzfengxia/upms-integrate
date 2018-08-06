package com.zz.upms.base.common.exception;

/**
 * Created by Francis.zz on 2017/7/10.
 */
public enum ErrorCode implements IErrorCode {
    BIZ_ERROR("-1", "业务异常"),
    /**
     * 并发冲突异常
     */
    STALE_OBJECT_STATE_EXCEPTION("10", "版本冲突,数据已被其他事务修改"),

    /**
     * 服务器http请求异常
     */
    HTTP_REQUEST_ERROR("4003", "http request error, %s"),

    SOCKET_REQUEST_ERROR("21", "socket request error, %s"),

    CARDCONFIRM_NO_ACCESSCODE("101", "制卡回盘接口未分配AccessCode"),

    NOT_FOUND_ORDER("102", "not found order info by orderno:%s, chanel:%s"),

    INVALID_DGI_DATA("103", "invalid %s DGI data: %s, cardNo: %s"),

    NOT_TERMINAL_BY_CITY("104", "invalid city code: %s"),
    
    HuaWeiDeleteCardException("105", "HuaWeiDeleteCardException: %s"),
    
    Yikatong_ReturnCardException("106", "Yikatong_ReturnCardException: %s"),
    
    STATUS_HAS_BEEN_UPDATED("107", "STATUS_HAS_BEEN_UPDATED: %s"),  
    
    HUAWEI_REFRUND_EXCEPTION("108","Hauwei_refund_exception : %s"),
    
    YIKATONG_GET_BALANCE_INTERFACE_EXCEPTION("109","Yikatong_get_balance_exception : %s"),

    /**************华为Exception START*********************/
    /**
     * 华为Wallet服务器状态码定义start
     * 0	成功
     * 1	参数错误	进一步的信息可以查看返回说明。
     * 2	签名错误
     * 3	内部错误	各种无具体原因的处理异常等
     * 4	无权访问	比如白名单控制时，部分地址无权访问服务器或接口;或者商户无权访问某些资源，比如非华为商户不能进行充值调整等。
     * 5	操作失败	不区分具体原因的操作失败，进一步的信息可以通过描述获取。
     */
    OP_HUAWEI_PARAM_ERROR("1", "参数错误,%s为空"),
    OP_HUAWEI_SIGN_ERRO("2", "verify sign error"),
    OP_HUAWEI_INNER_ERROR("3", "system internal error, %s"),
    OP_HUAWEI_NO_RIGHT_ACCESS("4", "not allow access, %s"),
    OP_HUAWEI_HTTP_FAILED("5", "failure, %s"),

    HW_INSUFFICIENT_CARD_RESOURCES("1001", "insufficient card resources"),
    HW_ILLEGAL_CITY("1002", "seId[%s] [HW_ILLEGAL_CITY] cardCode is not found, ExternalCode[%s]"),
    HW_SERVER_CLOSED("1009", "seId[%s] [CreateOrderHandler] card is closed, cardCode[%s] cardStatus[%s]"),

    HW_APDU_EXCUTE_EXCEPTION("6002", "APDU excuted failed"),
    HW_SECURITY_ERROR("6004", "seId[%s] Failed to process NWExtAuth with SD[%s]"),
    HW_DISSUPPORT_CITY("9001", "seId[%s] not support city ExternalCode[%s]"),
    HW_DISSUPPORT_MOBILE_MODE("9002", "seId[%s] not support mobile model[%s]"),
    // 激活请求失败
    HW_ACTIVATE_FAIL_EXCEPTION("3102", "card activated fail"),
    /**************华为Exception END*********************/

    /**
     * 201-300 通用异常代码段
     */
    MISSING_ARGS_ERROR("201", "missing params:%s"),

    SIGN_VERIFY_ERROR("202", "verify sign error"),

    NO_REGISTRY_PROCEDURE("-1", "[%s] had not registry [%s] process");
    ;

    private String message;
    /**
     * 异常描述，支持String.format格式
     */
    private String errorCode;

    ErrorCode(String errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    public ErrorCode setMessage(String msg) {
        this.message = msg;
        return this;
    }
}
