package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientDisconnectListenerObject extends AbstractListener {

    public ClientDisconnectListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, String.class, String.class);
    }

    public void invoke(String user, String reason) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, user, reason);
    }
}
