package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectListenerObject extends AbstractListener {

    public ConnectListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, 0);
    }

    public void invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object);
    }
}
