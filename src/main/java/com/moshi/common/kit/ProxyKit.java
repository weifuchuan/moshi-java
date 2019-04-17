package com.moshi.common.kit;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;

public class ProxyKit {
  public static <Target> Target intercept(Target target, Interceptor interceptor) {
    return (Target)
      Enhancer.create(
        target.getClass(),
        (MethodInterceptor)
          (o, method, args, methodProxy) -> {
            Object ret = method.invoke(target, args);
            interceptor.intercept(ret, method, args);
            return ret;
          });
  }

  public static <Target> Target intercept(
    Target target, Class<? extends Target> clz, Interceptor interceptor) {
    return (Target)
      Enhancer.create(
        clz,
        (MethodInterceptor)
          (o, method, args, methodProxy) -> {
            method =
              target.getClass().getMethod(method.getName(), method.getParameterTypes());
            Object ret = method.invoke(target, args);
            interceptor.intercept(ret, method, args);
            return ret;
          });
  }

  public interface Interceptor {
    void intercept(Object ret, Method method, Object[] args);
  }
}
