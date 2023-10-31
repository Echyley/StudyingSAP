/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.mock.model.ProductConfigurationMockModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationMockPersistenceServiceTest
{
	private static final String EXISTING_CONFIG_ID = "123";
	private static final String QUERY = "GET { ProductConfigurationMock } WHERE {configId} = ?configId";

	@Mock
	private ModelService modelService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@InjectMocks
	private DefaultConfigurationMockPersistenceService classUnderTest;

	private ConfigModel configModel;
	private ConfigModel extConfigModel;
	private ProductConfigurationMockModel existingPersistenceModel;


	@Before
	public void setup()
	{
		configModel = new ConfigModelImpl();
		configModel.setId(EXISTING_CONFIG_ID);
		configModel.setVersion("3");
		extConfigModel = new ConfigModelImpl();
		extConfigModel.setId(EXISTING_CONFIG_ID);
		extConfigModel.setVersion("1");

		given(modelService.create(ProductConfigurationMockModel.class))
				.willAnswer(invocation -> new ProductConfigurationMockModel());
		existingPersistenceModel = new ProductConfigurationMockModel();
		existingPersistenceModel.setCurrentConfigState(configModel);
		existingPersistenceModel.setConfigId(configModel.getId());
		final List<Object> resultList = Collections.singletonList(existingPersistenceModel);
		given(flexibleSearchService.search(eq(QUERY), argThat(map -> map.get("configId").equals(EXISTING_CONFIG_ID))))
				.willReturn(new SearchResultImpl<Object>(resultList, 1, 0, 0));
		given(flexibleSearchService.search(eq(QUERY), argThat(map -> !map.get("configId").equals(EXISTING_CONFIG_ID))))
				.willReturn(new SearchResultImpl<Object>(Collections.emptyList(), 0, 0, 0));


		clearInvocations(modelService);
	}

	@Test
	public void testWriteConfigModelEmpty()
	{
		existingPersistenceModel.setCurrentConfigState(null);
		classUnderTest.writeConfigModel(configModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, false,
						configModel, null)));
	}

	@Test
	public void testWriteConfigModelExisting()
	{
		classUnderTest.writeConfigModel(configModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, false,
						configModel, null)));
	}

	@Test
	public void testWriteConfigModelNotPersistedYet()
	{
		configModel.setId("456");
		classUnderTest.writeConfigModel(configModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, true,
						configModel, null)));
	}

	@Test
	public void testWriteExtConfigModelEmpty()
	{
		classUnderTest.writeExtConfigModel(extConfigModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, false,
						configModel, extConfigModel)));
	}

	@Test
	public void testWriteExtConfigModelExisting()
	{
		existingPersistenceModel.setExternalConfigState(new ConfigModelImpl());
		classUnderTest.writeExtConfigModel(extConfigModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, false,
						configModel, extConfigModel)));
	}

	@Test
	public void testWriteExtConfigModelNotPersistedYet()
	{
		extConfigModel.setId("456");
		classUnderTest.writeExtConfigModel(extConfigModel);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, true,
						null, extConfigModel)));
	}


	@Test
	public void testReadConfigModel()
	{
		final ConfigModel readModel = classUnderTest.readConfigModel(configModel.getId());
		assertNotSame(configModel, readModel); // should return a clone, not same instance
		assertEquals(configModel, readModel);
		assertEquals(configModel.getId(), readModel.getId());
	}

	@Test
	public void testReadConfigModelNotExisting()
	{
		assertNull(classUnderTest.readConfigModel("does not exists"));
	}

	@Test
	public void testReadConfigModelOnlyExternalStateExists()
	{
		existingPersistenceModel.setExternalConfigState(extConfigModel);
		existingPersistenceModel.setCurrentConfigState(null);
		assertNull(classUnderTest.readConfigModel(configModel.getId()));
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testReadConfigModelException()
	{
		final List<Object> list = new ArrayList<>();
		list.add(configModel);
		list.add(configModel);
		given(flexibleSearchService.search(eq(QUERY), any())).willReturn(new SearchResultImpl<Object>(list, 2, 0, 0));
		classUnderTest.readConfigModel("not unique");
	}

	@Test
	public void testReadExtConfigModel()
	{
		existingPersistenceModel.setExternalConfigState(extConfigModel);
		final ConfigModel readModel = classUnderTest.readExtConfigModel(extConfigModel.getId());
		assertNotSame(extConfigModel, readModel); // should return a clone, not same instance
		assertEquals(extConfigModel, readModel);
		assertEquals(extConfigModel.getId(), readModel.getId());
	}

	@Test
	public void testReadExtConfigModelNotExisting()
	{
		assertNull(classUnderTest.readExtConfigModel("does not exists"));
	}

	@Test
	public void testDeleteConfigModelNotExisting()
	{
		classUnderTest.deleteConfigModel("does not exist");
		verifyNoInteractions(modelService);
	}

	@Test
	public void testDeleteConfigModelExisting()
	{
		classUnderTest.deleteConfigModel(EXISTING_CONFIG_ID);
		verify(modelService).remove(existingPersistenceModel);
	}

	@Test
	public void testDeleteConfigModelExistingWithExtConfig()
	{
		existingPersistenceModel.setExternalConfigState(extConfigModel);
		classUnderTest.deleteConfigModel(EXISTING_CONFIG_ID);
		verify(modelService)
				.save(argThat(persistenceModel -> checkPersistenceModel((ProductConfigurationMockModel) persistenceModel, false, null,
						extConfigModel)));
	}


	private boolean checkPersistenceModel(final ProductConfigurationMockModel persistenceModelMock,
			final boolean newPersitenceModel, final ConfigModel currentConfigModel, final ConfigModel externalConfigModel)
	{
		final String configId = currentConfigModel == null? externalConfigModel.getId() : currentConfigModel.getId();
		boolean valid = persistenceModelMock.getConfigId().equals(configId)
				&& persistenceModelMock.getCurrentConfigState() == currentConfigModel &&  persistenceModelMock.getExternalConfigState() == externalConfigModel;
		if (newPersitenceModel)
		{
			valid = valid && persistenceModelMock != existingPersistenceModel;
		}
		else
		{
			valid = valid && persistenceModelMock == existingPersistenceModel;
		}
		return valid;
	}






}
