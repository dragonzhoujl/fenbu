package com.qiyue.fenbu.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.qiyue.fenbu.json.Util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

public class RedisClient {
	
	public JedisPool jedisPool;

	public RedisClient(JedisPool jedisPool) {
		super();
		this.jedisPool = jedisPool;
	}

	public RedisClient() {
		super();
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	public Object getByKey(String key) {
		Jedis client=jedisPool.getResource();		
		try{
			return client.get(key);
		}finally {
			client.close();
		}
		
	}
	public boolean isKeyExist(String key ) {
		Jedis client=jedisPool.getResource();	
		try {
			return	client.exists(key);
		}finally {
			client.close();			
		}		
	}
	
	public void set(String key,String value) {
		Jedis client=jedisPool.getResource();		
		try {
			client.set(key, value);			
		}finally {
			client.close();
		}
	}
	
	public Long setnx(String key,String value) {
		Jedis client=jedisPool.getResource();
		try {
			Long result=client.setnx(key, value);
			System.out.println("setnx key=" + key + " value=" + value + 
            		"result=" + result) ;	
			return result;
			}finally {
				client.close();
		}
	}
	
	public boolean setKeyWithExpireTime(String key,String value,int time) {
		if(time==0) {
			
		}
		Jedis client = jedisPool.getResource();
		String isSuccess="";
		try {
			isSuccess=client.setex(key, time, value);
			if ("OK".equals(isSuccess)) {
                return true;
            } else {
                return false;
            }
		}finally {
			client.close();
		}
	}
	
	public boolean lpush(String key,List<String> value) {
		Jedis client=jedisPool.getResource();
		try {
			Transaction tx=client.multi();
			for(String s:value){
				tx.lpush(key, s);
			}
			tx.exec();
			return true;
		}finally {
			client.close();
		}
	}
	
	
	public List<String> lrange(String key){
		Jedis client=jedisPool.getResource();
		List<String> returnList=null;
		try {
			returnList=client.lrange(key, 0, -1);
		}finally {
			client.close();
		}
		return returnList;
	}
	

	
	 public List<String> lrange(String key, int start, int length){
			Jedis client = jedisPool.getResource();
	        try {
	            return client.lrange(key, start, length);
	        } finally{
	            client.close();
	        }
	    }
	 public boolean setAnObject(String key,Object o) {
		 Jedis client=jedisPool.getResource();
		 try {
			 String afterSerialize=JSON.toJSONString(o);
			 o=client.set(key, afterSerialize);
			 return true;
		 }finally {
			 client.close();
		 }
	 }
	 
	 @SuppressWarnings("unchecked")
	public <T> T getSetT(String key ,T newValue) {
		 Jedis client=jedisPool.getResource();
		 T t;
		 try {
			 
			 String afterSerialize=Util.beanToJson(newValue);
			 String value=client.getSet(key, afterSerialize);
			 t=(T) Util.jsonToBean(value, newValue.getClass());
			 return t;
		 }finally {
			 client.close();
		 }
		
	 }
	 public <T> T getAnObject(String key,Class<T> zz ) {
		 Jedis client=jedisPool.getResource();
		 T t;
		 try {
			 String s=client.get(key);
			 if(s==null||s.length()==0) {
				 return null;
			 }
			 t=Util.jsonToBean(s, zz);
			 return t;
		 }finally {
			 client.close();
		 }
	 }
	 
	 public List<String> getKeys(String pattern){
		 Jedis client=jedisPool.getResource();
		 List<String> result=new ArrayList<String>();
		 try {
			 Set<String> set=client.keys(pattern);
			 result.addAll(set);
		 }finally {
			 
			 client.close();
		 }
		 return result;
	 }
	 
	 
	 
	 public boolean delKey(String key) {
		 Jedis client=jedisPool.getResource();
		 try {
			 System.out.println("del key="+key);
			 client.del(key);
			 return true;
		 }finally {
			 client.close();
		 }
	 }
	 
}
