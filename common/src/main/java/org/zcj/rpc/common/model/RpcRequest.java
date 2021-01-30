package org.zcj.rpc.common.model;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 10 18
 * Description:
 */
public class RpcRequest {

    private String className;

    private String version;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    public RpcRequest(){}

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
