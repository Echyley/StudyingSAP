/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.task.runner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSResponse;


/**
 * Test class for GigyaToHybrisUserUpdateTaskRunner
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaToHybrisUserUpdateTaskRunnerTest
{

	@InjectMocks
	private final GigyaToHybrisUserUpdateTaskRunner taskRunner = new GigyaToHybrisUserUpdateTaskRunner();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private Converter<GSResponse, CustomerModel> gigyaUserReverseConverter;

	@Mock
	private TaskModel taskModel;

	@Mock
	private TaskService taskService;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaFieldMappingModel firstGigyaFieldMapping;

	@Mock
	private GigyaFieldMappingModel secondGigyaFieldMapping;

	@Mock
	private GigyaFieldMappingModel thirdGigyaFieldMapping;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GigyaConfigModel firstGigyaConfig;

	@Mock
	private GigyaConfigModel secondGigyaConfig;

	@Test
	public void testWhenContextItemIsNull() 
	{
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaFieldMappingGenericDao);
	}

	@Test
	public void testWhenContextItemExistButNoMappings() 
	{
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithIncorrectSyncDirection() 
	{
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(firstGigyaFieldMapping));
		Mockito.lenient().when(firstGigyaFieldMapping.getSyncDirection()).thenReturn(null);

		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionG2H() throws GigyaApiException 
	{
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("first-api");
		Mockito.lenient().when(firstGigyaConfig.getGigyaApiKey()).thenReturn("first-api");
		
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(firstGigyaFieldMapping));
		Mockito.lenient().when(firstGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaAttributeName()).thenReturn("sample.attribute");
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);
		
		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter, Mockito.times(1)).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService, Mockito.times(1)).save(gigyaUser);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionBOTHAndMultipleMappings()
			throws GigyaApiException 
	{
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		final List<GigyaFieldMappingModel> fieldMappings = new ArrayList<>();
		fieldMappings.add(firstGigyaFieldMapping);
		fieldMappings.add(secondGigyaFieldMapping);

		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(fieldMappings);
		Mockito.lenient().when(firstGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaAttributeName()).thenReturn("sample.attribute");
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(secondGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaAttributeName()).thenReturn("anotherAttr");
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter, Mockito.times(1)).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService, Mockito.times(1)).save(gigyaUser);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithcorrectSyncDirectionBOTHAndMultipleMappingsWIthIncorrectDirection()
			throws GigyaApiException 
	{
		Mockito.lenient().when(firstGigyaConfig.getGigyaApiKey()).thenReturn("first-api");
		Mockito.lenient().when(secondGigyaConfig.getGigyaApiKey()).thenReturn("second-api");
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("first-api");
		
		final List<GigyaFieldMappingModel> fieldMappings = new ArrayList<>();
		fieldMappings.add(firstGigyaFieldMapping);
		fieldMappings.add(secondGigyaFieldMapping);

		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(fieldMappings);
		Mockito.lenient().when(firstGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaAttributeName()).thenReturn("sample.attribute");
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(secondGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaAttributeName()).thenReturn("anotherAttr");
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter, Mockito.times(1)).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService, Mockito.times(1)).save(gigyaUser);
	}

	@Test
	public void testWhenContextItemExistAndMappingsExistWithMultipleConfigs() throws GigyaApiException 
	{
		Mockito.lenient().when(firstGigyaConfig.getGigyaApiKey()).thenReturn("first-api");
		Mockito.lenient().when(secondGigyaConfig.getGigyaApiKey()).thenReturn("second-api");
		Mockito.lenient().when(taskModel.getContextItem()).thenReturn(gigyaUser);
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("first-api");
		
		final List<GigyaFieldMappingModel> fieldMappings = new ArrayList<>();
		fieldMappings.add(firstGigyaFieldMapping);
		fieldMappings.add(secondGigyaFieldMapping);
		fieldMappings.add(thirdGigyaFieldMapping);

		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(fieldMappings);
		Mockito.lenient().when(firstGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.BOTH);
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaAttributeName()).thenReturn("sample.attribute");
		Mockito.lenient().when(firstGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(thirdGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.lenient().when(thirdGigyaFieldMapping.getGigyaAttributeName()).thenReturn("thirdAttr");
		Mockito.lenient().when(thirdGigyaFieldMapping.getGigyaConfig()).thenReturn(firstGigyaConfig);

		Mockito.lenient().when(secondGigyaFieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaAttributeName()).thenReturn("anotherAttr");
		Mockito.lenient().when(secondGigyaFieldMapping.getGigyaConfig()).thenReturn(secondGigyaConfig);

		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.anyString(), Mockito.anyMap(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(gsResponse);

		taskRunner.run(taskService, taskModel);

		Mockito.verify(gigyaUserReverseConverter, Mockito.times(1)).convert(gsResponse, gigyaUser);
		Mockito.verify(modelService, Mockito.times(1)).save(gigyaUser);
	}

}
