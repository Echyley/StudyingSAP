/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.clickstream.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Aspect to provide error log, whenever 'sapymktclickstream' is called at runtime:
 */
@Aspect
public class ClickStreamAspect
{

	private static final Logger LOG = LoggerFactory.getLogger(ClickStreamAspect.class);

	private static final String ERRORMESSAGE = "sapymktclickstream is deprecated. It is not recommended to be used in productive live environments";

	/**
	 * Behavior to print log on console whenever com.hybris.ymkt.clickstream package is called
	 */
	@Before("execution(* com.hybris.ymkt.clickstream..*.*(..))")
	public void clickStreamAspect()
	{
		LOG.error(ERRORMESSAGE);
	}
}