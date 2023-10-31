/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.yaas.ProfileReference;

import java.util.List;

import rx.Observable;


/**
 * Adapter around the Charon client to retrieve profile references in a blocking way without blocking every Charon
 * client.
 */
public class CxIdentityServiceClientAdapter extends AbstractClientAdapter<CxIdentityServiceClient>
		implements CxIdentityServiceClient
{
	public CxIdentityServiceClientAdapter()
	{
		super(CxIdentityServiceClient.class);
	}

	@Override
	public List<ProfileReference> getProfileReferences(final String identityKey, final String identityType,
			final String identityOrigin)
	{
		return blocking(a -> a.getProfileReferencesAsync(identityKey, identityType, identityOrigin));
	}

	@Override
	public List<ProfileReference> getProfileReferences(final String identityKey, final String identityType,
			final String identityOrigin, final int limit, final String sortBy, final String sortDirection)
	{
		return blocking(a -> a.getProfileReferencesAsync(identityKey, identityType, identityOrigin, limit, sortBy, sortDirection));
	}

	@Override
	public Observable<List<ProfileReference>> getProfileReferencesAsync(final String identityKey, final String identityType,
			final String identityOrigin)
	{
		return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin);
	}

	@Override
	public Observable<List<ProfileReference>> getProfileReferencesAsync(final String identityKey, final String identityType,
			final String identityOrigin, final int limit, final String sortBy, final String sortDirection)
	{
		return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin, limit, sortBy, sortDirection);
	}
}
