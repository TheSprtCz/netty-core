package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientReceiveListenerObject extends AbstractListener {

    private Class<?> argClass;
    public ClientReceiveListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, 1);
    }

    public void invoke(Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, argClass.cast(obj));
    }

    public void process() {
        argClass = method.getParameterTypes()[0];
    }

    public boolean isCorrectClass(Object obj) {
        return argClass.isAssignableFrom(obj.getClass());
    }

    public Class<?> getArgClass() {
        return argClass;
    }
}
