package com.demo.task.mulitThread.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public final class SingleResult<T> implements Serializable {
    private int code;
    private String msg;
    private T result;
    private boolean success;

    private SingleResult(T result) {
        this.result = result;
        this.success = true;
    }

    private SingleResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.success = false;
    }

    public static <T> SingleResult<T> success(T t) {
        return new SingleResult(t);
    }

    public static <T> SingleResult<T> success() {
        return new SingleResult((Object)null);
    }


    public static <T> SingleResult<T> error(Integer errorCode, String errorMsg) {
        return new SingleResult(errorCode, errorMsg);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SingleResult)) {
            return false;
        } else {
            SingleResult<?> other = (SingleResult)o;
            if (this.getCode() != other.getCode()) {
                return false;
            } else {
                label39: {
                    Object this$msg = this.getMsg();
                    Object other$msg = other.getMsg();
                    if (this$msg == null) {
                        if (other$msg == null) {
                            break label39;
                        }
                    } else if (this$msg.equals(other$msg)) {
                        break label39;
                    }

                    return false;
                }

                Object this$result = this.getResult();
                Object other$result = other.getResult();
                if (this$result == null) {
                    if (other$result != null) {
                        return false;
                    }
                } else if (!this$result.equals(other$result)) {
                    return false;
                }

                if (this.isSuccess() != other.isSuccess()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getCode();
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : $result.hashCode());
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "SingleResult(code=" + this.getCode() + ", msg=" + this.getMsg() + ", result=" + this.getResult() + ", success=" + this.isSuccess() + ")";
    }
}
