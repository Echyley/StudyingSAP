/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.yaas.Profile;

import rx.Observable;


/**
 * Adapter around the Charon client to retrieve profile in a blocking way without blocking every Charon client.
 */
public class CxProfileServiceClientAdapter extends AbstractClientAdapter<CxProfileServiceClient> implements CxProfileServiceClient
{
	public CxProfileServiceClientAdapter()
	{
		super(CxProfileServiceClient.class);
	}

	@Override
	public Profile getProfile(final String id)
	{
		return blocking(a -> a.getProfileAsync(id));
	}


	@Override
	public Profile getProfile(final String id, final String fields)
	{
		return blocking(a -> a.getProfileAsync(id, fields));
	}

	@Override
	public Observable<Profile> getProfileAsync(final String id)
	{
		return getAdaptee().getProfileAsync(id);
	}

	@Override
	public Observable<Profile> getProfileAsync(final String id, final String fields)
	{
		return getAdaptee().getProfileAsync(id, fields);
	}

}
