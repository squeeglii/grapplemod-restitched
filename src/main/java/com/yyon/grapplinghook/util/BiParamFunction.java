package com.yyon.grapplinghook.util;

@FunctionalInterface
public interface BiParamFunction<A, B, Z> {

    Z apply(A a, B b) ;

}
