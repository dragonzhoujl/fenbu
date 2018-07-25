package com.qiyue.fenbu.test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TestCountDownLatch {

	public static void main(String[] args) {
		CountDownLatch begin =new CountDownLatch(1);
		CountDownLatch end =new CountDownLatch(5);
		for(int i=1;i<=5;i++) {
			new Thread(new AWorker(i,begin,end)).start();
		}
		try {  
			Thread.sleep((long) (Math.random() * 5000));  
        } catch (InterruptedException e1) {  
        	e1.printStackTrace();  
        } 
		System.out.println("judge say : run !");  
        begin.countDown();  
        long startTime = System.currentTimeMillis(); 
        try {
			end.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			 long endTime = System.currentTimeMillis(); 
			 System.out.println("judge say : all arrived !");  
             System.out.println("spend time: " + (endTime - startTime)); 
		}
	}
	

}
class AWorker implements Runnable{
	final int id ;
	final CountDownLatch begin;
	final CountDownLatch end;
	public AWorker(int i,CountDownLatch b,CountDownLatch e) {
		begin=b;
		end=e;
		id=i;
	}
	public void run() {
		System.out.println("id==="+id+", is ready");
		try {
			begin.await();
			Thread.sleep((long) (Math.random()*10000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			System.out.println("id==="+id+", is arrive");
			end.countDown();
		}
		
	}
	
}
