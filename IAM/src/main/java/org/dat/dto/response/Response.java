package org.dat.dto.response;


import org.dat.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class Response<T> implements Serializable {
    protected T data;
    private boolean success = true;
    private int code = 200;
    private String message;
    private long timestamp = Instant.now().toEpochMilli();
    private String status;
    @JsonIgnore
    private RuntimeException exception;

    public Response() {
//        this.status = ErrorCodeClient.SUCCESS.name();
    }

    public static <T> Response<T> of(T res) {
        Response<T> response = new Response<T>();
        response.data = res;
        response.success();
        return response;
    }

    public static <T> Response<T> ok() {
        Response<T> response = new Response<T>();
        response.success();
        return response;
    }

//    public static <T> Response<T> fail(RuntimeException exception) {
//        Response<T> response = new Response<T>();
//        response.setSuccess(false);
//        response.setStatus(ErrorCodeClient.FAIL.name());
//        response.setException(exception);
//        return response;
//    }
//
//    public static <T> Response<T> fail(String message, RuntimeException exception) {
//        Response<T> response = new Response<T>();
//        response.setSuccess(false);
//        response.setStatus(ErrorCodeClient.FAIL.name());
//        response.setException(exception);
//        response.setMessage(message);
//        return response;
//    }
//
//    public static <T> Response<T> fail() {
//        Response<T> response = new Response<T>();
//        response.setSuccess(false);
//        response.setStatus(ErrorCodeClient.FAIL.name());
//        return response;
//    }

    public Response<T> success() {
        this.success = true;
        this.code = 200;
        this.status = ErrorCode.SUCCESS.name();
        return this;
    }

    public Response<T> data(T res) {
        this.data = res;
        return this;
    }

    public Response<T> success(String message) {
        this.success = true;
        this.message = message;
        this.code = 200;
        this.status = ErrorCode.SUCCESS.name();
        return this;
    }
//
//    public Response<T> fail(String message, ResponseError responseError) {
//        this.success = false;
//        this.code = responseError.getCode();
//        this.status = ErrorCode.FAIL.name();
//        if (StringUtils.hasText(message)) {
//            this.message = message;
//        } else {
//            this.message = responseError.getMessage();
//        }
//        return this;
//    }
//
//    public Response<T> fail(Exception ex, ResponseError responseError) {
//        this.success = false;
//        this.code = responseError.getCode();
//        this.status = ErrorCode.FAIL.name();
//        this.message = ex.getMessage();
//        return this;
//    }

    public T getData() {
        if (this.exception != null) {
            throw this.exception;
        } else {
            return this.data;
        }
    }

    public boolean isSuccess() {
        if (this.exception != null) {
            throw this.exception;
        } else {
            return this.success;
        }
    }

    @JsonIgnore
    public void setException(final RuntimeException exception) {
        this.exception = exception;
    }

}
