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
package de.hybris.platform.configurablebundlebackoffice.widgets.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundlebackoffice.factory.ExplorerTreeDynamicNodeFactory;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class BundleTemplatesModelPopulatorTest extends AbstractCockpitngUnitTest<BundleTemplatesModelPopulator>
{
	@InjectMocks
	private BundleTemplatesModelPopulator populator;
	private ExplorerTreeDynamicNodeFactory nodeFactory;
	@Mock
	private CatalogService catalogService;
	@Mock
	private CatalogModel catalog;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private UserService userService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private EmployeeModel admin;
	@Mock
	private LabelService labelService;
	@Mock
	private CatalogVersionService catalogVersionService;

	private SessionService sessionService;

	@Before
	public void setUp()
	{
		sessionService = Mockito.spy(new MockSessionService());
		populator.setSessionService(sessionService);

		Mockito.lenient().when(catalogVersion.getItemtype()).thenReturn("CatalogVersion");

		Mockito.lenient().when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		Mockito.lenient().when(catalog.getItemtype()).thenReturn(CatalogModel._TYPECODE);

		Mockito.lenient().when(catalogService.getAllCatalogs()).thenReturn(Lists.newArrayList(catalog));
		Mockito.lenient().when(catalogVersionService.getAllCatalogVersions()).thenReturn(Lists.newArrayList(catalogVersion));
		Mockito.lenient().when(catalogVersionService.getAllReadableCatalogVersions(any(EmployeeModel.class))).thenReturn(Lists.newArrayList(catalogVersion));

		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(admin);
		Mockito.lenient().when(userService.getAdminUser()).thenReturn(admin);
		Mockito.lenient().when(userService.getUserForUID("admin")).thenReturn(admin);
		Mockito.lenient().when(Boolean.valueOf(userService.isAdmin(admin))).thenReturn(Boolean.TRUE);

		Mockito.lenient().when(Boolean.valueOf(permissionFacade.canReadInstance(any()))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(Boolean.valueOf(permissionFacade.canReadType(any()))).thenReturn(Boolean.TRUE);

		nodeFactory = new ExplorerTreeDynamicNodeFactory(populator);

	}

	@Test
	public void shouldPopulateRootNodeContext()
	{
		final NavigationNode node = nodeFactory.createAllCatalogsNode();
		//when
		final List<NavigationNode> nodes = populator.findChildrenNavigationNodes(node);
		//then
		assertThat(nodes).hasSize(2);
		final Iterator<NavigationNode> iterator = nodes.iterator();
		assertThat(iterator.next().getData()).isEqualTo("allcatalogs_allCatalogs");
		assertThat(iterator.next().getData()).isEqualTo(catalog);
	}

	@Test
	public void shouldPopulateCatalogNodeContext()
	{
		final NavigationNode node = nodeFactory.createCatalogNode(catalog);
		//when
		final List<NavigationNode> nodes = populator.findChildrenNavigationNodes(node);
		//then
		assertThat(nodes).hasSize(1);
		final Iterator<NavigationNode> iterator = nodes.iterator();
		assertThat(iterator.next().getData()).isEqualTo(catalogVersion);
	}

	@Test
	public void shouldBeEmptyInCatalogVersionNodeContext()
	{
		final NavigationNode node = nodeFactory.createCatalogVersionNode(catalogVersion);
		//when
		final List<NavigationNode> nodes = populator.findChildrenNavigationNodes(node);
		//then
		assertThat(nodes).isEmpty();
	}
}
