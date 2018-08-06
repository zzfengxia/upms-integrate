package com.zz.upms.base.common.protocol;

/**
 * Created by Francis.zz on 2017/4/27.
 */
public class Response<T> {
    private String message;
    /**
     * 0：成功
     */
    private String status;
    private T data;

    private Response(String message, String status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<T>("", "0", data);
    }

    public static <T> Response<T> success() {
        return new Response<T>("", "0", null);
    }

    public static <T> Response<T> error(String message) {
        return new Response<T>(message, "-1", null);
    }

    public static <T> Response<T> error(String errorCode, String message) {
        return new Response<T>(message, errorCode, null);
    }
}
