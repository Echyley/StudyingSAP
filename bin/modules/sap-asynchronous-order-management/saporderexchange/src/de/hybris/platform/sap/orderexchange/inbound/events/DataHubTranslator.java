/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.inbound.events;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;


/**
 * Abstract base class for Datahub Translators
 * 
 * @param <T>
 *           Type of helper class
 */
public abstract class DataHubTranslator<T> extends AbstractSpecialValueTranslator
{
	private String helperBeanName;
	private T inboundHelper;

	protected T getInboundHelper()
	{
		return inboundHelper;
	}

	protected DataHubTranslator(String beanName)
	{
		super();
		this.helperBeanName = beanName;
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		if (getInboundHelper() == null)
		{
			setInboundHelper((T) Registry.getApplicationContext().getBean(helperBeanName));
		}
	}

	@Override
	public void validate(final String paramString) throws HeaderValidationException
	{
		// Nothing to do
	}

	@Override
	public String performExport(final Item paramItem) throws ImpExException
	{
		return null;
	}

	@Override
	public boolean isEmpty(final String paramString)
	{
		return false;
	}

	protected void setInboundHelper(T service)
	{
		this.inboundHelper = service;
	}
}
