/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.segment;

import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.CONSUMPTION_LAYER_INTEGRATION_CONVERTER_NAME;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationintegration.segment.UserSegmentsProvider;
import de.hybris.platform.personalizationintegration.service.CxIntegrationMappingService;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerProfileMapper;
import de.hybris.platform.personalizationyprofile.strategy.CxProfileIdentifierStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.personalizationyprofile.yaas.client.CxProfileServiceClient;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;


/**
 * Implementation of {link UserSegmentsProvider} which read user segment information from yprofile consumption layer
 */
public class ConsumptionLayerUserSegmentsProvider implements UserSegmentsProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsumptionLayerUserSegmentsProvider.class.getName());
	private static final String PROVIDER_ID = "CDS_CUST";
	protected static final String FIELD_SEPARATOR = ",";

	private volatile String profileFields;
	private final Object profileFieldsLock = new Object();

	private List<CxConsumptionLayerProfileMapper> mappers;
	private CxProfileServiceClient cxProfileServiceClient;
	private CxIntegrationMappingService cxIntegrationMappingService;
	private CxProfileIdentifierStrategy cxProfileIdentifierStrategy;


	@Override
	public List<SegmentMappingData> getUserSegments(final UserModel user)
	{
		ServicesUtil.validateParameterNotNull(user, "user must not be null");

		if (CollectionUtils.isNotEmpty(mappers))
		{
			final Optional<Profile> profileData = getProfileData(user);
			if (profileData.isPresent())
			{
				final List<SegmentMappingData> result = profileData
						.flatMap(p -> cxIntegrationMappingService.mapExternalData(p, CONSUMPTION_LAYER_INTEGRATION_CONVERTER_NAME))
						.map(MappingData::getSegments)//
						.orElseGet(Collections::emptyList);
				normalizeAffinity(result);
				return result;
			}
		}
		return null;
	}

	@Override
	public String getProviderId()
	{
		return PROVIDER_ID;
	}

	protected void normalizeAffinity(final List<SegmentMappingData> mappingData)
	{
		//nothing to do by default
	}

	protected Optional<Profile> getProfileData(final UserModel user)
	{
		final String currentFields = getProfileFields();
		if (StringUtils.isEmpty(currentFields))
		{
			LOG.warn("Mappers don't define any required fields so no data will be read from Yaas Profile");
			return Optional.empty();
		}

		final String profileId = cxProfileIdentifierStrategy.getProfileIdentifier(user);
		if (StringUtils.isNotEmpty(profileId))
		{
			try
			{
				return Optional.of(cxProfileServiceClient.getProfile(profileId, currentFields));
			}
			catch (final NotFoundException e)
			{
				LOG.debug("Profile not found : {}", profileId, e);
				return Optional.of(new Profile());
			}
			catch (final HttpException e)
			{
				LOG.warn("Get profile request failed for user {}", user.getUid());
				LOG.debug("Exception", e);
			}
			catch (final SystemException e)
			{
				LOG.warn(
						"Failed to get yaas profile for user {}. Check if yaas configuration is properly defined for cxProfileServiceClient. Error message : {} ",
						user.getUid(), e.getMessage());
				LOG.debug("Exception", e);
			}
		}

		return Optional.empty();
	}

	protected List<CxConsumptionLayerProfileMapper> getMappers()
	{
		return mappers;
	}

	@Autowired(required = false)
	public void setMappers(final List<CxConsumptionLayerProfileMapper> mappers)
	{
		this.mappers = mappers;
	}

	protected String getProfileFields()
	{
		final String tmp = profileFields;
		if (tmp == null)
		{
			synchronized (profileFieldsLock)
			{
				if (profileFields == null)
				{
					profileFields = createProfileFields();
				}
				return profileFields;
			}
		}
		return tmp;
	}

	protected String createProfileFields()
	{
		String fields = "";
		if (mappers != null && !mappers.isEmpty())
		{
			fields = mappers.stream()
			                .map(CxConsumptionLayerProfileMapper::getRequiredFields)
			                .filter(Objects::nonNull)
			                .flatMap(Set::stream)
			                .filter(StringUtils::isNotBlank)
			                .map(String::trim)
			                .distinct()
			                .collect(Collectors.joining(FIELD_SEPARATOR));
		}
		return fields;
	}

	public void resetFields()
	{
		synchronized (profileFieldsLock)
		{
			this.profileFields = null;
		}
	}

	protected CxProfileServiceClient getCxProfileServiceClient()
	{
		return cxProfileServiceClient;
	}

	@Required
	public void setCxProfileServiceClient(final CxProfileServiceClient cxProfileServiceClient)
	{
		this.cxProfileServiceClient = cxProfileServiceClient;
	}

	protected CxProfileIdentifierStrategy getCxProfileIdentifierStrategy()
	{
		return cxProfileIdentifierStrategy;
	}

	@Required
	public void setCxProfileIdentifierStrategy(final CxProfileIdentifierStrategy cxProfileIdentifierStrategy)
	{
		this.cxProfileIdentifierStrategy = cxProfileIdentifierStrategy;
	}

	protected CxIntegrationMappingService getCxIntegrationMappingService()
	{
		return cxIntegrationMappingService;
	}

	@Required
	public void setCxIntegrationMappingService(final CxIntegrationMappingService cxIntegrationMappingService)
	{
		this.cxIntegrationMappingService = cxIntegrationMappingService;
	}
}
