/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.yaas.Segment;

import java.util.List;

import rx.Observable;


/**
 * Adapter around the Charon client to retrieve segments in a blocking way without blocking every Charon client.
 */
public class CxSegmentServiceClientAdapter extends AbstractClientAdapter<CxSegmentServiceClient> implements CxSegmentServiceClient
{

	protected CxSegmentServiceClientAdapter()
	{
		super(CxSegmentServiceClient.class);
	}

	@Override
	public List<Segment> getSegments()
	{
		return blocking(CxSegmentServiceClient::getSegmentsAsync);
	}

	@Override
	public Observable<List<Segment>> getSegmentsAsync()
	{
		return getAdaptee().getSegmentsAsync();
	}
}
