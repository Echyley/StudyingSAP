/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.exception;


/**
 * Base class for PPS related runtime exceptions
 * 
 */
public class PPSRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6901492870937956587L;

	/**
	 * Constructor
	 */
	public PPSRuntimeException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 *            Cause of the exception
	 */
	public PPSRuntimeException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param msg
	 *            Message text of the exception
	 */
	public PPSRuntimeException(final String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * 
	 * @param msg
	 *            Message text of the exception
	 * @param cause
	 *            Cause of the exception
	 */
	public PPSRuntimeException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

}