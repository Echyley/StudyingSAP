/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.daos.CMSItemTypeAttributeFilterConfigDao;
import de.hybris.platform.cmsfacades.model.CMSItemTypeAttributeFilterConfigModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSItemAttributeFilterEnablerServiceTest
{
	@InjectMocks
	@Spy
	private DefaultCMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnabler;

	@Mock
	private CMSItemTypeAttributeFilterConfigDao cmsItemTypeAttributeFilterConfigDao;
	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel cmsItemComposedTypeModel;
	@Mock
	private ComposedTypeModel contentPageComposedTypeModel;
	@Mock
	private ComposedTypeModel abstractCMSComponentComposedTypeModel;
	@Mock
	private ComposedTypeModel simpleCMSComponentModelComposedTypeModel;
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private List<CMSItemTypeAttributeFilterConfigModel> configs;

	@Before
	public void setUp() throws Exception
	{
		configs = new ArrayList<>();
		appendToConfigs("CMSItem", "BASIC", "uid, catalogVersion, itemtype", configs);
		appendToConfigs("CMSItem", "DEFAULT", "BASIC, name", configs);
		appendToConfigs("CMSItem", "FULL", "DEFAULT", configs);
		appendToConfigs("CMSItem", "SELECT", "BASIC,name", configs);

		appendToConfigs("AbstractPage", "BASIC", "uid, name, typeCode, masterTemplate, title, robotTag", configs);
		appendToConfigs("AbstractPage", "DEFAULT", "BASIC, defaultPage, restrictions", configs);
		appendToConfigs("AbstractPage", "FULL", "DEFAULT, contentSlots", configs);

		appendToConfigs("ContentPage", "BASIC", "AbstractPage:BASIC, label", configs);
		appendToConfigs("ContentPage", "CUSTOM", "uid, name, typeCode, masterTemplate, title, label", configs);
		appendToConfigs("ContentPage", "DEFAULT", "BASIC, defaultPage, restrictions, homepage", configs);
		appendToConfigs("ContentPage", "FULL", "DEFAULT, contentSlots, labelOrId", configs);

		appendToConfigs("AbstractCMSComponent", "BASIC", "uid, name, typeCode, masterTemplate, title, robotTag, label, customAttributeA",
				configs);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(anyString(), anyLong())).thenReturn(360L);

		when(cmsItemTypeAttributeFilterConfigDao.getAllAttributeConfigurations()).thenReturn(configs);

		when(typeService.getTypeForCode(CMSItemModel._TYPECODE)).thenReturn(new TypeModel());
		when(typeService.getTypeForCode(AbstractCMSComponentModel._TYPECODE)).thenReturn(new TypeModel());
		when(typeService.getTypeForCode(ContentPageModel._TYPECODE)).thenReturn(new TypeModel());
		when(typeService.getTypeForCode(SimpleCMSComponentModel._TYPECODE)).thenReturn(new TypeModel());

		when(typeService.getComposedTypeForCode(CMSItemModel._TYPECODE)).thenReturn(cmsItemComposedTypeModel);
		when(typeService.getComposedTypeForCode(ContentPageModel._TYPECODE)).thenReturn(contentPageComposedTypeModel);
		when(typeService.getComposedTypeForCode(AbstractCMSComponentModel._TYPECODE)).thenReturn(abstractCMSComponentComposedTypeModel);
		when(typeService.getComposedTypeForCode(SimpleCMSComponentModel._TYPECODE)).thenReturn(simpleCMSComponentModelComposedTypeModel);

		when(cmsItemComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList());
		when(contentPageComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(cmsItemComposedTypeModel));
		when(abstractCMSComponentComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(cmsItemComposedTypeModel));
		when(simpleCMSComponentModelComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(abstractCMSComponentComposedTypeModel, cmsItemComposedTypeModel));

		when(cmsItemComposedTypeModel.getCode()).thenReturn(CMSItemModel._TYPECODE);
		when(contentPageComposedTypeModel.getCode()).thenReturn(ContentPageModel._TYPECODE);
		when(abstractCMSComponentComposedTypeModel.getCode()).thenReturn(AbstractCMSComponentModel._TYPECODE);
		when(simpleCMSComponentModelComposedTypeModel.getCode()).thenReturn(SimpleCMSComponentModel._TYPECODE);

		when(cmsItemComposedTypeModel.getAllSubTypes()).thenReturn(Arrays.asList(contentPageComposedTypeModel, abstractCMSComponentComposedTypeModel, simpleCMSComponentModelComposedTypeModel));

		cmsItemAttributeFilterEnabler.afterPropertiesSet();
	}

	@Test
	public void shouldReturnEmptyListIfConfigNotProvided()
	{
		// GIVEN
		configs = new ArrayList<>();
		when(cmsItemTypeAttributeFilterConfigDao.getAllAttributeConfigurations()).thenReturn(configs);

		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("CMSItem", "BASIC");

		// THEN
		assertTrue(attributes.isEmpty());
	}

	@Test
	public void shouldReturnListOfBasicAttributes()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("CMSItem", "BASIC");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "catalogVersion", "itemtype"));
	}

	@Test
	public void shouldReturnConfigForSimpleCMSComponentModeltWhichIsInheritedFromAbstractCMSComponent()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("SimpleCMSComponent", "BASIC");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "name", "typeCode", "masterTemplate", "title", "robotTag", "label", "customAttributeA"));
	}

	@Test
	public void shouldContainFieldsForSELECTModeWhichWereInheritedFromCMSItem()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("SimpleCMSComponent", "SELECT");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "catalogVersion", "itemtype", "name"));
	}

	@Test
	public void shouldReturnListOfDefaultAttributesWhenOneAttributeReferencesAnotherMode()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("CMSItem", "DEFAULT");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "catalogVersion", "itemtype", "name"));
	}

	@Test
	public void shouldReturnListOfFullAttributesWhenOnlyOneAttributeAndItReferencesAnotherMode()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("CMSItem", "FULL");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "catalogVersion", "itemtype", "name"));
	}

	@Test
	public void shouldReturnListOAttributesWhenOneAttributeReferencesAnotherTypeMode()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("ContentPage", "BASIC");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "name", "typeCode", "masterTemplate", "title", "robotTag", "label"));
	}

	@Test
	public void shouldReturnListOAttributesForCustomMode()
	{
		// WHEN
		final List<String> attributes = cmsItemAttributeFilterEnabler.getAttributes("ContentPage", "CUSTOM");

		// THEN
		assertThat(attributes, containsInAnyOrder("uid", "name", "typeCode", "masterTemplate", "title", "label"));
	}


	protected void appendToConfigs(final String typeCode, final String mode, final String attributeNames, final List<CMSItemTypeAttributeFilterConfigModel> payload)
	{
		final CMSItemTypeAttributeFilterConfigModel config = new CMSItemTypeAttributeFilterConfigModel();
		config.setAttributes(attributeNames);
		config.setMode(mode);
		config.setTypeCode(typeCode);
		payload.add(config);
	}
}
