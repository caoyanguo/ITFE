package com.cfcc.itfe;

public class YakTransactionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public YakTransactionException(String msg) {
		super(msg);
	}
	public YakTransactionException(String msg,Throwable t) {
		super(msg,t);
	}
	public YakTransactionException(Throwable t) {
		super(t);
	}
}
