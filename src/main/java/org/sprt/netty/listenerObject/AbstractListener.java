package org.sprt.netty.listenerObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class AbstractListener {

    protected final Method method;
    protected final Object object;

    public AbstractListener(Method method, Object object) {
        this.method = method;
        this.object = object;
    }

    protected boolean check(Class<?> returnClass, int count, Class<?>... expectedClasses) {
        if (!method.getReturnType().equals(returnClass))
            return false;

        Parameter[] parameters = method.getParameters();
        if (parameters.length != count)
            return false;

        for (int i = 0; i < expectedClasses.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> expectedClass = expectedClasses[i];
            if (!parameter.getType().equals(expectedClass))
                return false;
        }
        return true;
    }

    protected boolean check(Class<?> returnClass, Class<?>... expectedClasses) {
        if (!method.getReturnType().equals(returnClass))
            return false;

        Parameter[] parameters = method.getParameters();
        if (parameters.length != expectedClasses.length)
            return false;

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> expectedClass = expectedClasses[i];
            if (!parameter.getType().equals(expectedClass))
                return false;
        }
        return true;
    }

    protected boolean check(Class<?> returnClass, int count) {
        if (!method.getReturnType().equals(returnClass))
            return false;

        Parameter[] parameters = method.getParameters();
        if (parameters.length != count)
            return false;

        return true;
    }

    public abstract boolean isValid();
}
