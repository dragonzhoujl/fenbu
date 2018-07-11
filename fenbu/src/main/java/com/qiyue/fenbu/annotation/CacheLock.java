package com.qiyue.fenbu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
	String lockedPrefix() default "";//redis 锁key的前缀
	long timeout() default 2000;//轮询锁的时间
	int expireTime() default 1000;//key在redis里面存在的时间
	
	
	

}
