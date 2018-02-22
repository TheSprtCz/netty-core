package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DisconnectListenerObject extends AbstractListener {

    public DisconnectListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, String.class);
    }

    public void invoke(String reason) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, reason);
    }
}
