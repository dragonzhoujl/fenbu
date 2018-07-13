package com.qiyue.fenbu.exception;

public class CacheLockException extends RuntimeException{

	public CacheLockException(String message) {
		super(message);
	}
	
	public CacheLockException(String message,Throwable cause) {
		super(message,cause);
	}
}
