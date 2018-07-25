package com.qiyue.fenbu.seckill;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.qiyue.fenbu.invocation.CacheLockInterceptor;

public class SecKill implements SeckillInterface{
	static Map<Long,Long> inventory=new HashMap<Long,Long>();
	static{
		inventory.put(10000001L, 10000L);
		inventory.put(10000002L, 10000L);
	}

	public Long reduceInventory(Long commodityId) {
		inventory.put(commodityId, inventory.get(commodityId)-1);
		return inventory.get(commodityId);
	}


	@Test
	public void testSecKill() {
		int threadCount=1000;
		int splitPoint=500;
		final CountDownLatch endCount=new CountDownLatch(threadCount);
		final CountDownLatch beginCount=new CountDownLatch(1);
		final SecKill sk=new SecKill();
		Thread[] threads=new Thread[threadCount];
		//起500个线程，秒杀第一个商品
		for(int i=0;i<splitPoint;i++) {
			threads[i]=new Thread(new Runnable() {

				public void run() {
					 try {
	                        //等待在一个信号量上，挂起
	                        beginCount.await();
	                        //用动态代理的方式调用secKill方法
	                        SeckillInterface proxy = (SeckillInterface) Proxy.newProxyInstance(SeckillInterface.class.getClassLoader(), 
	                            new Class[]{SeckillInterface.class}, new CacheLockInterceptor(sk));
	                        proxy.seckill("test", 10000001L);
	                        //testClass.testFunc("test", 10000001L);
	                        endCount.countDown();
	                    } catch (InterruptedException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
					
				}
				
			});
			threads[i].start();
		}
		  //再起500个线程，秒杀第二件商品
        for(int i= splitPoint;i < threadCount;i++){
            threads[i] = new Thread(new  Runnable() {
                public void run() {
                    try {
                        //等待在一个信号量上，挂起
                        beginCount.await();
                        //用动态代理的方式调用secKill方法
                        SeckillInterface proxy = (SeckillInterface) Proxy.newProxyInstance(SeckillInterface.class.getClassLoader(), 
                            new Class[]{SeckillInterface.class}, new CacheLockInterceptor(sk));
                        proxy.seckill("test", 10000002L);
                        //testClass.testFunc("test", 10000001L);
                        endCount.countDown();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();

        }
        long startTime = System.currentTimeMillis();
        //主线程释放开始信号量，并等待结束信号量，这样做保证1000个线程做到完全同时执行，保证测试的正确性
        beginCount.countDown();

        try {
            //主线程等待结束信号量
            endCount.await();
            //观察秒杀结果是否正确
            System.out.println(SecKill.inventory.get(10000001L));
            System.out.println(SecKill.inventory.get(10000002L));
            System.out.println("error count" + CacheLockInterceptor.ERROR_COUNT);
            System.out.println("total cost " + (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

	public void seckill(String userID, Long commidityID) {
		reduceInventory(commidityID);
	}
	
}
