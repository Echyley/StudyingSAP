/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Required;

import rx.Observable;


public class AbstractClientAdapter<T>
{
	private ApiRegistryClientService apiRegistryClientService;
	private final Class<T> adapteeClass;

	protected AbstractClientAdapter(final Class<T> adapteeClass)
	{
		this.adapteeClass = adapteeClass;
	}

	public T getAdaptee()
	{
		try
		{
			return getApiRegistryClientService().lookupClient(adapteeClass);
		}
		catch (final CredentialException e)
		{
			throw new SystemException("Failed to create " + adapteeClass.getName(), e);
		}
	}

	protected <R> R blocking(final Function<T, Observable<R>> input)
	{
		return input.apply(getAdaptee()).toBlocking().first();
	}

	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
	}
}
