package com.github.system.base.dto.r;


public enum ApiErrorCode implements IErrorCode {
    /**
     * 失败
     */
    FAILED(-1, "操作失败",false),
    /**
     * 成功
     */
    SUCCESS(200, "执行成功",true);

    private final long code;
    private final String msg;
    private final Boolean success;

    ApiErrorCode(final long code, final String msg,final Boolean success) {
        this.code = code;
        this.msg = msg;
        this.success =success;
    }

    public static ApiErrorCode fromCode(long code) {
        ApiErrorCode[] ecs = ApiErrorCode.values();
        for (ApiErrorCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }
    @Override
    public Boolean getSuccess() {
        return success;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s, success=%s} ", code, msg,success);
    }
}