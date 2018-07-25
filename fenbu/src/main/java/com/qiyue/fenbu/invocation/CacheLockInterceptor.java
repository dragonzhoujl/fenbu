package com.qiyue.fenbu.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.qiyue.fenbu.annotation.CacheLock;
import com.qiyue.fenbu.annotation.LockedComplexObject;
import com.qiyue.fenbu.annotation.LockedObject;
import com.qiyue.fenbu.exception.CacheLockException;
import com.qiyue.fenbu.lock.RedisLock;

public class CacheLockInterceptor implements InvocationHandler {
	
	public static int ERROR_COUNT=0;
	private Object proxied;
	

	public CacheLockInterceptor(Object proxied) {
		super();
		this.proxied = proxied;
	}


	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		CacheLock cacheLock=method.getAnnotation(CacheLock.class);
		if(cacheLock==null) {
			System.out.println("no cacheLock annotation"); //没有CacheLock注解,pass
			return method.invoke(proxied, args);
		}
		Annotation[][] annotations= method.getParameterAnnotations();
		//根据获取到的参数注解和参数列表获得加锁的参数
        Object lockedObject = getLockedObject(annotations,args);
        String objectValue=lockedObject.toString();
        RedisLock lock=new RedisLock(cacheLock.lockedPrefix(),objectValue);
        boolean result=lock.lock(cacheLock.timeout(), cacheLock.expireTime());
        if(!result) {
        	ERROR_COUNT+=1;
        	throw new CacheLockException("get lock failed");
        }
        try {
        	return method.invoke(proxied, args);
        }finally {
        	lock.unlock();
        }
		
	}


	private Object getLockedObject(Annotation[][] annotations, Object[] args) {
		if(args==null||args.length==0) {
			throw new CacheLockException("方法参数为空，没有被锁定的对象");
		}
		if(null == annotations || annotations.length == 0){
            throw new CacheLockException("没有被注解的参数");
        }
		 //不支持多个参数加锁，只支持第一个注解为lockedObject或者lockedComplexObject的参数
		int index=-1;
		for(int i=0;i<annotations.length;i++) {
			for(int j=0;j<annotations[i].length;j++) {
				if(annotations[i][j] instanceof LockedComplexObject) {
					index=i;
					try {
						return args[i].getClass().getField(((LockedComplexObject)(annotations[i][j])).field());
					} catch (NoSuchFieldException e) {
						throw new CacheLockException("注解对象中没有该属性" + ((LockedComplexObject)annotations[i][j]).field());
					
					} catch (SecurityException e) {
						
						e.printStackTrace();
					}
				}
				 if(annotations[i][j] instanceof LockedObject){
	                    index = i;
	                    break;
	             }
				//找到第一个后直接break，不支持多参数加锁
		        if(index != -1){
		           break;
		        }
		       
			}
		}
		 if(index == -1){
	            throw new CacheLockException("请指定被锁定参数");
	        }

	        return args[index];
		
	}
	

}
