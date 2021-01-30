package org.zcj.rpc.common.model;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 10 20
 * Description:
 */
public class RpcResponse {

    private Throwable exception;

    private Object result;

    public RpcResponse() {
    }


    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
