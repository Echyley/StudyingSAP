/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.widgets.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.collector.RelatedItemsCollector;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.quality.Strictness;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.engine.impl.ComponentWidgetAdapter;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SyncTreeActionTest
{
	@Mock
	private RelatedItemsCollector relatedItemsCollector;
	@Mock
	private ComponentWidgetAdapter componentWidgetAdapter;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private BackofficeTypeUtils backofficeTypeUtils;
	@Mock
	private SynchronizationFacade synchronizationFacade;

	@InjectMocks
	private SyncTreeAction action;

	@Captor
	private ArgumentCaptor<List<Object>> capturedItems;

	MockitoSession mockito;

	@Before
	public void setUp() throws Exception
	{

		//initialize session to start mocking
		mockito = Mockito.mockitoSession()
				.initMocks(this).strictness(Strictness.LENIENT)
				.startMocking();

		Mockito.lenient().when(objectFacade.isModified(any())).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(backofficeTypeUtils.isAssignableFrom(anyString(), eq(null))).thenReturn(true);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		final SyncItemJobModel syncItemJobModel = mock(SyncItemJobModel.class);
		Mockito.lenient().when(synchronizationFacade.getSyncCatalogVersion(any())).thenReturn(Optional.of(catalogVersionModel));
		Mockito.lenient().when(synchronizationFacade.getInboundSynchronizations(catalogVersionModel)).thenReturn(List.of(syncItemJobModel));
		Mockito.lenient().when(synchronizationFacade.canSync(any())).thenReturn(Boolean.TRUE);
	}

	@After 
	public void tearDown() {
		//It is necessary to finish the session so that Mockito
		// can detect incorrect stubbing and validate Mockito usage
		//'finishMocking()' is intended to be used in your test framework's 'tear down' method.
		mockito.finishMocking();
	}

	@Test
	public void shouldRaiseExceptionWhenActionContextPayloadIsEmpty()
	{
		//given
		final ActionContext<Object> context = createActionContext(null);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> action.perform(context));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Sync tree action can only work for action context with ItemModel objects passed as payload");
	}

	@Test
	public void shouldRaiseExceptionWhenActionContextPayloadIsNotItemType()
	{
		//given
		final ActionContext<Object> context = createActionContext("test");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> action.perform(context));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Sync tree action can only work for action context with ItemModel objects passed as payload");
	}

	@Test
	public void shouldRaiseExceptionWhenActionContextPayloadIsNotCollectionOfItemType()
	{
		//given
		final ActionContext<Object> context = createActionContext(newArrayList("test"));
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> action.perform(context));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Sync tree action can only work for action context with ItemModel objects passed as payload");
	}

	@Test
	public void shouldCollectTreeOfItemModel()
	{
		//given
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(bundleTemplate);
		//when
		action.perform(context);
		//then
		verify(relatedItemsCollector).collect(eq(bundleTemplate), anyMap());
	}

	@Test
	public void shouldCollectTreeOfItemModels()
	{
		//given
		final BundleTemplateModel bundleTemplate1 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate2 = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(newArrayList(bundleTemplate1, bundleTemplate2));
		//when
		action.perform(context);
		//then
		verify(relatedItemsCollector).collect(eq(bundleTemplate1), anyMap());
		verify(relatedItemsCollector).collect(eq(bundleTemplate2), anyMap());
	}


	@Test
	public void shouldPerformSyncWithCollectedItemModels()
	{
		//given
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(bundleTemplate);
		final List<ItemModel> items = newArrayList(bundleTemplate, new BundleTemplateModel());
		Mockito.lenient().when(relatedItemsCollector.collect(eq(bundleTemplate), anyMap())).thenReturn(items);
		//when
		action.perform(context);
		//then
		verify(componentWidgetAdapter).sendOutput("currentObjects", items, action);
	}

	@Test
	public void shouldPerformSyncWithCollectedMultipleItemModels()
	{
		//given
		final BundleTemplateModel bundleTemplate1 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate2 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate3 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate4 = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(newArrayList(bundleTemplate1, bundleTemplate2));
		Mockito.lenient().when(relatedItemsCollector.collect(eq(bundleTemplate1), anyMap()))
				.thenReturn(newArrayList(bundleTemplate1, bundleTemplate3));
		Mockito.lenient().when(relatedItemsCollector.collect(eq(bundleTemplate2), anyMap()))
				.thenReturn(newArrayList(bundleTemplate2, bundleTemplate4));
		//when
		action.perform(context);
		//then
		verify(componentWidgetAdapter).sendOutput(eq("currentObjects"), capturedItems.capture(), eq(action));
		Assertions.assertThat(capturedItems.getValue()).containsExactlyInAnyOrder(bundleTemplate1, bundleTemplate2, bundleTemplate3,
				bundleTemplate4);
	}

	@Test
	public void shouldPerformSyncWithNonDuplicatedCollectedMultipleItemModels()
	{
		//given
		final BundleTemplateModel bundleTemplate1 = new BundleTemplateModel();
		final BundleTemplateModel bundleTemplate2 = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(newArrayList(bundleTemplate1, bundleTemplate2));
		Mockito.lenient().when(relatedItemsCollector.collect(eq(bundleTemplate1), anyMap()))
				.thenReturn(newArrayList(bundleTemplate2));
		Mockito.lenient().when(relatedItemsCollector.collect(eq(bundleTemplate2), anyMap()))
				.thenReturn(newArrayList(bundleTemplate1));
		//when
		action.perform(context);
		//then
		verify(componentWidgetAdapter).sendOutput(eq("currentObjects"), capturedItems.capture(), eq(action));
		Assertions.assertThat(capturedItems.getValue()).hasSize(2).containsExactlyInAnyOrder(bundleTemplate1, bundleTemplate2);
	}

	@Test
	public void canPerformWhenPayloadHasItemModel() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(bundleTemplate);
		//when
		final boolean result = action.canPerform(context);
		//then
		assertThat(result).isTrue();
	}

	@Test
	public void canPerformWhenPayloadHasItemModels() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final ActionContext<Object> context = createActionContext(newArrayList(bundleTemplate, bundleTemplate));
		//when
		final boolean result = action.canPerform(context);
		//then
		assertThat(result).isTrue();
	}


	@Test
	public void cannotPerformWhenPayloadIsNotItemModel() throws Exception
	{
		//given
		final ActionContext<Object> context = createActionContext(Integer.valueOf(2));
		//when
		final boolean result = action.canPerform(context);
		//then
		assertThat(result).isFalse();
	}

	@Test
	public void cannotPerformWhenPayloadCollectionIsHoldsNonItemModelItems() throws Exception
	{
		//given
		final ActionContext<Object> context = createActionContext(newArrayList(new BundleTemplateModel(), Integer.valueOf(2)));
		//when
		final boolean result = action.canPerform(context);
		//then
		assertThat(result).isFalse();
	}

	protected <T> ActionContext<T> createActionContext(final T data)
	{
		return new ActionContext<>(data, null, null, null);
	}
}
