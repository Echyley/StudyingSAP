/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.services.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.enums.SAPDestinationKey;
import de.hybris.platform.sap.sapmodel.model.SAPConsumedDestinationEntryModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * JUnit test suite for {@link DefaultSapMappedDestinationServiceTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapMappedDestinationServiceTest
{
	private DefaultSapMappedDestinationService sapMappedDestinationService;
	
	@Mock
	private BaseStoreModel baseStoreModel;
	
	@Mock
	protected ModelService modelService;
	
	private SAPConfigurationModel sapConfigurationModel;
	
	private final ConsumedDestinationModel consumedDestinationModel = mock(ConsumedDestinationModel.class);
		
	@Before
	public void setUp() {
		SAPConsumedDestinationEntryModel consumedDestinationEntryModel= new SAPConsumedDestinationEntryModel();
		sapMappedDestinationService = new DefaultSapMappedDestinationService();
		sapConfigurationModel = new SAPConfigurationModel();
		sapMappedDestinationService.setModelService(modelService);
		consumedDestinationEntryModel.setConsumedDestination(consumedDestinationModel);
		consumedDestinationEntryModel.setDestinationKey(SAPDestinationKey.BILLINGDESTINATION);
		sapConfigurationModel.setSapConsumedDestinationEntries(Set.of(consumedDestinationEntryModel));
		baseStoreModel.setSAPConfiguration(sapConfigurationModel);
		
	}
	
	@Test
	public void testGetDestinationByKeyForBaseStoreNoConsumedDestination() {
		baseStoreModel.setSAPConfiguration(null);
		sapConfigurationModel.setSapConsumedDestinationEntries(Collections.emptySet());
		lenient().when(modelService.create(SAPConfigurationModel.class)).thenReturn(sapConfigurationModel);
		lenient().when(modelService.getAttributeValue(sapConfigurationModel,SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES)).thenReturn(sapConfigurationModel.getSapConsumedDestinationEntries());
		Optional<AbstractDestinationModel> destination = sapMappedDestinationService.getDestinationByKeyForBaseStore(baseStoreModel, SAPDestinationKey.BILLINGDESTINATION);
		assertEquals(Optional.empty(), destination);
	}
	
	@Test
	public void testGetDestinationByKeyForBaseStoreWithConsumedDestination() {
		
		lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		lenient().when(modelService.getAttributeValue(sapConfigurationModel,SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES)).thenReturn(sapConfigurationModel.getSapConsumedDestinationEntries());
		Optional<AbstractDestinationModel> destination = sapMappedDestinationService.getDestinationByKeyForBaseStore(baseStoreModel, SAPDestinationKey.BILLINGDESTINATION);
		assertNotNull(destination);
	}
	
	@Test
	public void testGetDestinationByKeyForBaseStoreWithNullDestination() {
		
		lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		lenient().when(modelService.getAttributeValue(sapConfigurationModel,SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES)).thenReturn(sapConfigurationModel.getSapConsumedDestinationEntries());
		Optional<AbstractDestinationModel> destination = sapMappedDestinationService.getDestinationByKeyForBaseStore(baseStoreModel, null);
		assertEquals(Optional.empty(), destination);
	}
	
	
	@Test
	public void testGetDestinationByKeyForBaseStoreWithoutSystemUpdate() {
		
		lenient().when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		lenient().when(modelService.getAttributeValue(sapConfigurationModel,SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES)).thenThrow(new AttributeNotSupportedException("cannot find attribute",SAPConfigurationModel.SAPCONSUMEDDESTINATIONENTRIES));
		Optional<AbstractDestinationModel> destination = sapMappedDestinationService.getDestinationByKeyForBaseStore(baseStoreModel, null);
		assertEquals(Optional.empty(), destination);
	}
	
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetDestinationByKeyForBaseStoreWhenBaseStoreIsNull()
	{
		sapMappedDestinationService.getDestinationByKeyForBaseStore(null, mock(SAPDestinationKey.class));
	}
	

}
