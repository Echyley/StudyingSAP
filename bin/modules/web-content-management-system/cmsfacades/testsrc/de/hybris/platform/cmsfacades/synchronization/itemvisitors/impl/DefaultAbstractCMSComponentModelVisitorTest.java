/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import static com.google.common.collect.Lists.newLinkedList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultAbstractCMSComponentModelVisitorTest
{
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	@Mock
	private TypeService typeService;
	@Mock
	private ModelService modelService;

	private List<AttributeDescriptorModel> attributeDescriptors;

	@Mock
	private AttributeDescriptorModel slotAttributeDescriptorModel;
	@Mock
	private AttributeDescriptorModel cmsItemAttributeDescriptorModel;
	@Mock
	private AttributeDescriptorModel cmsItemCollectionAttributeDescriptorModel;
	@Mock
	private AttributeDescriptorModel navigationNodeAttributeDescriptorModel;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private AbstractCMSComponentModel component;
	@Mock
	private ComposedTypeModel componentComposedType;
	@Mock
	private ComposedTypeModel componentSuperTypeComposedType;
	@Mock
	private CMSItemModel cmsItemValue;
	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private AbstractPageModel otherValue;
	@Mock
	private CMSItemModel cmsItem2Value;
	@Mock
	private CMSItemModel cmsItem3Value;
	@Mock
	private SynchronizationCacheService synchronizationCacheService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	protected Configuration configuration;

	private Map<String, List<String>> ignoreAttributeTypeCodeConfigs;

	private List<CMSItemModel> cmsItemCollectionValue;


	@InjectMocks
	@Spy
	private DefaultAbstractCMSComponentModelVisitor visitor;

	private final String slotAttributeQualifier = "slotQualifier";
	private final String cmsItemAttributeQualifier = "cmsItemQualifier";
	private final String navigationNodeAttributeQualifier = "navigationNodeQualifier";
	private final String cmsItemCollectionAttributeQualifier = "cmsItemCollectionQualifier";

	@Before
	public void setUp()
	{
		// we have configuration to ignore some attributes for the following types.
		ignoreAttributeTypeCodeConfigs = new HashMap<>();
		ignoreAttributeTypeCodeConfigs.put(
				AbstractCMSComponentModel._TYPECODE, Arrays.asList(ContentSlotModel._TYPECODE, AbstractPageModel._TYPECODE, CMSNavigationNodeModel._TYPECODE)
		);
		ignoreAttributeTypeCodeConfigs.put(
			CMSItemModel._TYPECODE, Arrays.asList(ContentPageModel._TYPECODE)
		);

		when(cmsItemAttributeDescriptorModel.getQualifier()).thenReturn(cmsItemAttributeQualifier);
		when(navigationNodeAttributeDescriptorModel.getQualifier()).thenReturn(navigationNodeAttributeQualifier);
		when(cmsItemCollectionAttributeDescriptorModel.getQualifier()).thenReturn(cmsItemCollectionAttributeQualifier);

		attributeDescriptors = newLinkedList();
		attributeDescriptors.add(cmsItemAttributeDescriptorModel);
		attributeDescriptors.add(navigationNodeAttributeDescriptorModel);
		attributeDescriptors.add(cmsItemCollectionAttributeDescriptorModel);

		doReturn(ContentSlotModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(slotAttributeDescriptorModel);
		doReturn(CMSItemModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(cmsItemAttributeDescriptorModel);
		doReturn(CMSNavigationNodeModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(navigationNodeAttributeDescriptorModel);
		doReturn(CMSItemModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(cmsItemCollectionAttributeDescriptorModel);

		cmsItemCollectionValue = newLinkedList();
		cmsItemCollectionValue.add(cmsItem2Value);
		cmsItemCollectionValue.add(cmsItem3Value);

		// component
		when(modelService.getAttributeValue(component, slotAttributeQualifier)).thenReturn(contentSlot);
		when(modelService.getAttributeValue(component, cmsItemAttributeQualifier)).thenReturn(cmsItemValue);
		when(modelService.getAttributeValue(component, navigationNodeAttributeQualifier)).thenReturn(otherValue);
		when(modelService.getAttributeValue(component, cmsItemCollectionAttributeQualifier)).thenReturn(cmsItemCollectionValue);
		when(typeService.getComposedTypeForClass(component.getClass())).thenReturn(composedTypeModel);

		when(component.getItemtype()).thenReturn(SimpleCMSComponentModel._TYPECODE);
		when(typeService.getComposedTypeForCode(SimpleCMSComponentModel._TYPECODE)).thenReturn(componentComposedType);
		when(componentComposedType.getAllSuperTypes()).thenReturn(Arrays.asList(componentSuperTypeComposedType));
		when(componentComposedType.getCode()).thenReturn(AbstractCMSComponentModel._TYPECODE);
		when(componentSuperTypeComposedType.getCode()).thenReturn(CMSItemModel._TYPECODE);


		when(composedTypeModel.getDeclaredattributedescriptors()).thenReturn(attributeDescriptors);

		// return class by type code
		final ItemModel dummyContentSlot = new ContentSlotModel();
		final ItemModel dummyAbstractPageModel = new AbstractPageModel();
		final ItemModel dummyCMSNavigationNodeModel = new CMSNavigationNodeModel();
		final ItemModel dummyContentPageModel = new ContentPageModel();
		when(typeService.getModelClass(ContentSlotModel._TYPECODE)).thenReturn((Class<ItemModel>) dummyContentSlot.getClass());
		when(typeService.getModelClass(AbstractPageModel._TYPECODE)).thenReturn((Class<ItemModel>) dummyAbstractPageModel.getClass());
		when(typeService.getModelClass(CMSNavigationNodeModel._TYPECODE)).thenReturn((Class<ItemModel>) dummyCMSNavigationNodeModel.getClass());
		when(typeService.getModelClass(ContentPageModel._TYPECODE)).thenReturn((Class<ItemModel>) dummyContentPageModel.getClass());


		doAnswer((Answer<Object>) invocation -> {
			final Supplier<List<Object>> supplier = (Supplier) invocation.getArguments()[0];
			return supplier.get();
		}).when(synchronizationCacheService).getOrSetItemListCache(any(), anyString());

		// configuration
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(anyString(), anyLong())).thenReturn(1l);

		visitor.setIgnoreAttributeTypeCodeConfigs(ignoreAttributeTypeCodeConfigs);
		visitor.afterPropertiesSet();
	}

	@Test
	public void willCollectAllCMSItemChildrenExceptSlotsAndNavigationNodes()
	{
		final List<ItemModel> visit = visitor.visit(component, null, null);
		
		assertThat(visit, containsInAnyOrder(cmsItemValue, cmsItem2Value, cmsItem3Value));
	}

	@Test
	public void shouldRetrieveIgnoredAttributesFromCacheIfExecutedSecondTime()
	{
		// GIVEN
		visitor.visit(component, null, null);
		verify(typeService, times(1)).getComposedTypeForClass(component.getClass());

		// WHEN
		visitor.visit(component, null, null);

		// THEN
		verify(typeService,times(1)).getComposedTypeForClass(component.getClass());
	}
}
