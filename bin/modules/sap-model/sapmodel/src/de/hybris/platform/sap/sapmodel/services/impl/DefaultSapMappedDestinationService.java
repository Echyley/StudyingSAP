/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services.impl;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.enums.SAPDestinationKey;
import de.hybris.platform.sap.sapmodel.model.SAPConsumedDestinationEntryModel;
import de.hybris.platform.sap.sapmodel.services.SapMappedDestinationService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of SapMappedDestinationService interface to provide business logic for interface
 * 
 */
public class DefaultSapMappedDestinationService implements SapMappedDestinationService<AbstractDestinationModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapMappedDestinationService.class);
	
	private ModelService modelService;
	
	private final BiFunction<Set<SAPConsumedDestinationEntryModel>, SAPDestinationKey, Optional<SAPConsumedDestinationEntryModel>> selectEntry = (
			sapConsumedDestinationEntries, destinationKey) -> sapConsumedDestinationEntries.stream()
					.filter(destinationEntryData -> destinationEntryData.getDestinationKey().equals(destinationKey)).findFirst();
			
	@Override
	public Optional<AbstractDestinationModel> getDestinationByKeyForBaseStore(@NotNull final BaseStoreModel baseStoreModel, final SAPDestinationKey destinationKey)
	{
		Preconditions.checkArgument(baseStoreModel != null, "Parameter baseStoreModel cannot be null.");
		
		SAPConfigurationModel sapConfigurationModel = getCurrentSAPConfiguration(baseStoreModel);
		Object sapConsumedDestinationEntryObject = null;
		try 
		{
			sapConsumedDestinationEntryObject = getModelService().getAttributeValue(sapConfigurationModel, SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES);
		}
		catch (final AttributeNotSupportedException e) 
		{
					LOG.debug("Configurable consumed destination feature is not enabled, please update the system");
		}
		
		if(sapConsumedDestinationEntryObject !=null)
		{
			final Optional<SAPConsumedDestinationEntryModel> destinationEntry = selectEntry
					.apply((Set<SAPConsumedDestinationEntryModel>)sapConsumedDestinationEntryObject, destinationKey);
			
			if (destinationEntry.isPresent())
			{
				final Optional<AbstractDestinationModel> consumedDestination = destinationEntry.map(SAPConsumedDestinationEntryModel::getConsumedDestination);
				LOG.debug("Picked consumed destination [{}] for the destination key [{}]!", consumedDestination,
						destinationKey);
				return consumedDestination;
			}
			else
			{
				LOG.debug("No consumed destination is mapped for the destination key [{}] in base store [{}]!", destinationKey,
							baseStoreModel.getName());
			}
		}
		
		return Optional.empty();
		
	}

	private SAPConfigurationModel getCurrentSAPConfiguration(final BaseStoreModel baseStore)
	{

		SAPConfigurationModel sapConfiguration = baseStore.getSAPConfiguration();

		if (sapConfiguration != null)
		{
			return sapConfiguration;
		}
		else
		{
			LOG.debug("No SAP multiple back-ends configuration is maintained for the base store [{}]!",
					baseStore.getName());
			SAPConfigurationModel sapConfigurationModel = getModelService().create(SAPConfigurationModel.class);
			sapConfigurationModel.setSapConsumedDestinationEntries(Sets.newHashSet());
			return sapConfigurationModel;
		}

	}
	
	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



}
