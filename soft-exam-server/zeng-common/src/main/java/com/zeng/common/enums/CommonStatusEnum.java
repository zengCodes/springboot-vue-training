package com.zeng.common.enums;

/**
 * @Author zenghuiqing
 * @Description
 * @Date create in 2021/12/2 1:20
 **/
public enum CommonStatusEnum {
    /**
     * "禁用或上架或全部"
     */
    DISABLE(0),
    /**
     * "启用或下架或公开"
     */
    AVAILABLE(1),
    /**
     * 隐私
     */
    RECOVER(2);

    private int statusCode;

    CommonStatusEnum(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
