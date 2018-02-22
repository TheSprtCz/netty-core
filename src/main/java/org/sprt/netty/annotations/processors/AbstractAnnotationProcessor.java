package org.sprt.netty.annotations.processors;

import java.lang.reflect.Method;

public abstract class AbstractAnnotationProcessor {

    public void processObject(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            process(method, obj);
        }
    }

    protected abstract void process(Method method, Object obj);
}
