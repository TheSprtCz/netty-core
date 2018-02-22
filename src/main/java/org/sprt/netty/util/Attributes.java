package org.sprt.netty.util;

import io.netty.util.AttributeKey;

public class Attributes {

    public static final AttributeKey<String> NAME = AttributeKey.newInstance("nick");
    public static final AttributeKey<String> REASON = AttributeKey.newInstance("reason");
}
