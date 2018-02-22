package org.sprt.netty.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Listener for PacketReceive events, can be used for both server and client.
 * In case of client listener, method should contain specific class like
 * void onReceive(String text) - in which case it will fire only when class is received.
 * or void onReceive(BasicPacket packet) - in which case whole packet will be passed.
 * In case of server there also needs to be ServerUser specified as first argument e.g.
 * void onReceive(ServerUser user, String text). Needs specified header to which it will listen
 * as value.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ReceiveListener {

    public String value();
}
