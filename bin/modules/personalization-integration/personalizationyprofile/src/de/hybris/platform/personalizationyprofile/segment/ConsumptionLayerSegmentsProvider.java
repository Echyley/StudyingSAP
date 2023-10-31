/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.segment;

import de.hybris.platform.personalizationintegration.segment.SegmentsProvider;
import de.hybris.platform.personalizationservices.data.BaseSegmentData;
import de.hybris.platform.personalizationyprofile.yaas.Segment;
import de.hybris.platform.personalizationyprofile.yaas.client.CxSegmentServiceClient;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.exp.HttpException;


/**
 * Implementation of {@link SegmentsProvider} that reads segments information from yprofile consumption layer.
 */
public class ConsumptionLayerSegmentsProvider implements SegmentsProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsumptionLayerSegmentsProvider.class.getName());
	private static final String PROVIDER_ID = "CDS";
	private CxSegmentServiceClient cxSegmentServiceClient;
	private Converter<Object, BaseSegmentData> converter;

	@Override
	public Optional<List<BaseSegmentData>> getSegments()
	{
		if (converter != null)
		{
			final Optional<List<Segment>> segmentData = getSegmentData();
			if (segmentData.isPresent())
			{
				return Optional.of(converter.convertAll(segmentData.get()));
			}
		}
		return Optional.empty();
	}

	@Override
	public String getProviderId()
	{
		return PROVIDER_ID;
	}

	protected Optional<List<Segment>> getSegmentData()
	{
		try
		{
			return Optional.of(cxSegmentServiceClient.getSegments());
		}
		catch (final HttpException e)
		{
			LOG.warn("Get segments request failed");
			LOG.debug("Exception", e);
		}
		catch (final SystemException e)
		{
			LOG.warn("Failed to get segments. Error message : {} ", e.getMessage());
			LOG.debug("Exception", e);
		}
		return Optional.empty();
	}


	public CxSegmentServiceClient getCxSegmentServiceClient()
	{
		return cxSegmentServiceClient;
	}

	@Required
	public void setCxSegmentServiceClient(final CxSegmentServiceClient cxSegmentServiceClient)
	{
		this.cxSegmentServiceClient = cxSegmentServiceClient;
	}


	public Converter<Object, BaseSegmentData> getConverter()
	{
		return converter;
	}

	@Required
	public void setConverter(final Converter<Object, BaseSegmentData> converter)
	{
		this.converter = converter;
	}
}
