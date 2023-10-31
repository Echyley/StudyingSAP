/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.handlers;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.config.jaxb.wizard.PrepareType;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ProductWizardCatalogVersionPopulatorTest
{
	private static final String CATALOG_VERSION = "catalogVersion";
	private static final String ACTION_CONTEXT = "actionContext";
	private static final String NEW_PRODUCT_PROPERTY = "newProduct";
	private static final String CTX_PROPERTY = "ctx";
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	@Mock
	private EmployeeModel user;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ProductModel newProduct;
	@InjectMocks
	private ProductWizardCatalogVersionPopulator handler;
	private WidgetInstanceManager wim;

	@Before
	public void setUp()
	{
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		given(userService.getCurrentUser()).willReturn(user);
	}

	@Test
	public void shouldPopulateCatalogVersionFromActionContext()
	{
		//given
		final WidgetModel widgetModel = wim.getModel();
		final Map<Object, Object> actionContext = Map.of(CATALOG_VERSION, catalogVersion);
		final Map<String, Object> context = Map.of(ACTION_CONTEXT, actionContext);

		widgetModel.setValue(CTX_PROPERTY, context);
		widgetModel.setValue(NEW_PRODUCT_PROPERTY, newProduct);
		given(userService.isAdmin(user)).willReturn(true);
		given(catalogVersionService.getAllCatalogVersions()).willReturn(List.of(catalogVersion));

		//when
		handler.prepareFlow(mock(PrepareType.class), wim);

		//then
		then(newProduct).should().setCatalogVersion(catalogVersion);
	}

	@Test
	public void shouldPopulateCatalogVersionFromContext()
	{
		//given
		PrepareWidgetModel(Map.of(CATALOG_VERSION, catalogVersion));
		given(userService.isAdmin(user)).willReturn(true);
		given(catalogVersionService.getAllCatalogVersions()).willReturn(List.of(catalogVersion));

		//when
		handler.prepareFlow(mock(PrepareType.class), wim);

		//then
		then(newProduct).should().setCatalogVersion(catalogVersion);
	}

	@Test
	public void shouldNotPopulateCatalogVersionIfUserHasNoPermission()
	{
		//given
		PrepareWidgetModel(Map.of(CATALOG_VERSION, catalogVersion));
		given(userService.isAdmin(user)).willReturn(false);
		given(catalogVersionService.getAllWritableCatalogVersions(user)).willReturn(List.of(mock(CatalogVersionModel.class)));

		//when
		handler.prepareFlow(mock(PrepareType.class), wim);

		//then
		then(newProduct).should(never()).setCatalogVersion(any());
	}

	@Test
	public void shouldNotPopulateIfContextHasNotCatalogVersion()
	{
		//given
		PrepareWidgetModel(Map.of());

		//when
		handler.prepareFlow(mock(PrepareType.class), wim);

		//then
		then(newProduct).should(never()).setCatalogVersion(any());
		then(userService).should(never()).getCurrentUser();
	}

	private void PrepareWidgetModel(final Map<String, Object> context)
	{
		final WidgetModel widgetModel = wim.getModel();
		widgetModel.setValue(CTX_PROPERTY, context);
		widgetModel.setValue(NEW_PRODUCT_PROPERTY, newProduct);
	}

}
