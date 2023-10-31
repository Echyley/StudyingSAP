/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.group.impl;


import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.scimfacades.ScimGroup;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScimGroupFacadeTest {
	
	private static final String EXTERNAL_ID = "external-id";

	@InjectMocks
	private final DefaultScimGroupFacade scimGroupFacade = new DefaultScimGroupFacade();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private ScimUserGroupModel scimUserGroupModel;

	@Mock
	private Converter<ScimUserGroupModel, ScimGroup> scimGroupConverter;

	@Mock
	private ModelService modelService;

	@Mock
	private Converter<ScimGroup, ScimUserGroupModel> scimGroupReverseConverter;

	@Mock
	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Test
	public void testCreateGroup()
	{
		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		scimGroup.setDisplayName("testGroup");

		Mockito.lenient().when(modelService.create(ScimUserGroupModel.class)).thenReturn(scimUserGroupModel);
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		Mockito.lenient().when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		final ScimGroup returnedScimGroup = scimGroupFacade.createGroup(scimGroup);

		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		Mockito.verify(modelService).save(scimUserGroupModel);
		Assert.assertEquals(scimGroup, returnedScimGroup);
	}


	@Test
	public void testUpdateGroupWhenGroupExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		Mockito.lenient().when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		final ScimGroup returnedScimGroup = scimGroupFacade.updateGroup(EXTERNAL_ID, scimGroup);

		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		Mockito.verify(modelService).save(scimUserGroupModel);
		Assert.assertEquals(scimGroup, returnedScimGroup);
	}

	@Test(expected = ScimException.class)
	public void testUpdateGroupWhenGroupDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.updateGroup(EXTERNAL_ID, new ScimGroup());
	}

	@Test
	public void testGetGroupWhenGroupExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroup(EXTERNAL_ID);

		Mockito.verify(scimGroupConverter).convert(scimUserGroupModel);
	}

	@Test(expected = ScimException.class)
	public void testGetGroupWhenGroupDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.getGroup(EXTERNAL_ID);
	}

	@Test
	public void testGetGroupForScimGroupIdWhenModelExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroup(EXTERNAL_ID);
		Assert.assertEquals(scimUserGroupModel, scimGroupFacade.getGroupForScimGroupId(EXTERNAL_ID));
	}

	@Test(expected = ScimException.class)
	public void testGetGroupForScimGroupIdWhenModelDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenThrow(new ModelNotFoundException("exception"));
		scimGroupFacade.getGroup(EXTERNAL_ID);
		Assert.assertNull(scimGroupFacade.getGroupForScimGroupId(EXTERNAL_ID));
	}

	@Test
	public void testDeleteGroupWhenGroupExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		scimGroupFacade.deleteGroup(EXTERNAL_ID);
		Mockito.verify(modelService, Mockito.times(1)).remove(scimUserGroupModel);
	}

	@Test(expected = ScimException.class)
	public void testDeleteGroupWhenGroupDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimGroupFacade.deleteGroup(EXTERNAL_ID);
	}

	@Test
	public void testGetGroups()
	{
		
		final ScimGroup scimGroup = new ScimGroup();
		scimGroup.setId(EXTERNAL_ID);
		scimGroup.setDisplayName("testGroup");
		
		Mockito.lenient().when(modelService.create(ScimUserGroupModel.class)).thenReturn(scimUserGroupModel);
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(scimUserGroupModel));
		Mockito.lenient().when(scimGroupConverter.convert(scimUserGroupModel)).thenReturn(scimGroup);

		scimGroupFacade.createGroup(scimGroup);
		
		Mockito.lenient().when(scimUserGroupGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(scimUserGroupModel));

		scimGroupFacade.getGroups();

		Mockito.verify(scimGroupConverter).convert(scimUserGroupModel);
		Mockito.verify(scimGroupReverseConverter).convert(scimGroup, scimUserGroupModel);
		
	}

}