package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sprt.netty.client.ServerUser;

public class ServerReceiveListenerObject extends AbstractListener {

    private Class<?> argClass;
    public ServerReceiveListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, 2, ServerUser.class);
    }

    public void invoke(ServerUser user, Object obj) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, user, argClass.cast(obj));
    }

    public void process() {
        argClass = method.getParameterTypes()[1];
    }

    public boolean isCorrectClass(Object obj) {
        return argClass.isAssignableFrom(obj.getClass());
    }

    public Class<?> getArgClass() {
        return argClass;
    }
}
