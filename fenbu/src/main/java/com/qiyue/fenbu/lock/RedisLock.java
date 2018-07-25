package com.qiyue.fenbu.lock;

import java.util.Random;

import com.qiyue.fenbu.redis.RedisClient;
import com.qiyue.fenbu.redis.RedisFactory;

public class RedisLock {
	//纳秒和毫秒之间的转换率
	public static final long MILLI_NANO_TIME=1000*1000L;
	
	public static final String LOCKED="TRUE";
	
	public static final Random 	RANDOM= new Random();
	
	private String key;
	
	private RedisClient redisClient;
	
	private boolean lock =true;
	
	 /**
     * 
     * @param purpose 锁前缀
     * @param key 锁定的ID等东西
     * @param client
     */
	public RedisLock(String purpose,String key) {
		this.key=purpose+"_"+key+"_lock";
		this.redisClient=RedisFactory.getDefaultClient();
	
	}
	public boolean lock(long timeout,int expire) {
		long nanoTime=System.nanoTime();
		timeout*=MILLI_NANO_TIME;
		try{
			//在timeout的时间范围内不断轮询锁
			while((System.nanoTime()-nanoTime)<timeout) {
				//锁不存在的话，设置锁并设置锁过期时间，即加锁
				if(this.redisClient.setnx(key, LOCKED)==1) {
					this.redisClient.expire(key, expire);//设置锁过期时间是为了在没有释放
					this.lock=true;
					return this.lock;
				}
				System.out.println("出现锁等待");
				//短暂休眠，避免可能的活锁
				Thread.sleep(3,RANDOM.nextInt(30));
			}
		}catch(Exception e) {
			throw new RuntimeException("locking error",e);
		}
		return false;
	}
	public  void unlock() {
		try {
			if(this.lock){
				redisClient.delKey(key);//直接删除
			}
		} catch (Throwable e) {
			
		}
	}



	

}
