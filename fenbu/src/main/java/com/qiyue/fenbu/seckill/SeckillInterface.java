package com.qiyue.fenbu.seckill;

import com.qiyue.fenbu.annotation.CacheLock;
import com.qiyue.fenbu.annotation.LockedObject;

public interface SeckillInterface {
	//cacheLock注解可能产生并发的方法
	//最简单的秒杀方法，参数是用户ID和商品ID。可能有多个线程争抢一个商品，所以商品ID上加LockedObject注解
	@CacheLock(lockedPrefix="TEST_PREFIX")
	public void seckill(String userID,@LockedObject Long commidityID);
}
