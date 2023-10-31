/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.data;

import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.composedTypeModel;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.integrationObjectItemModel;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.integrationObjectModel;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.itemModelMock;
import static de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils.mutableSetOfItemModels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationEntityNotSelectedException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.lang.Strings;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.util.notifications.NotificationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ExportConfigurationEditorPresentationUnitTest
{
	private static final String INTEGRATION_SERVICE_IO_CODE = "IntegrationService";
	private static final String WEBHOOK_SERVICE_IO_CODE = "WebhookService";
	private static final String SCRIPT_SERVICE_IO_CODE = "ScriptService";
	private static final String OUTBOUND_CHANNEL_CONFIG_IO_CODE = "OutboundChannelConfig";

	private static final long[] PKs = { 80890730L, 77331938L, 31625393L };

	private static final String ROOT_ITEM_CODE = "InboundChannelConfigurationAlias";
	private static final String ROOT_ITEM_TYPE_CODE = "InboundChannelConfiguration";

	@Mock(lenient = true)
	private NotificationService notificationService;

	private ExportConfigurationEditorPresentation presentation;

	@Before
	public void setup()
	{
		presentation = new ExportConfigurationEditorPresentation(notificationService);
	}

	@Test
	public void warningNotificationWhenGetSelectedEntityButNoneSelected()
	{
		final IntegrationObjectModel selectedEntity = presentation.getSelectedEntity();
		verify(notificationService, times(1)).notifyUser(eq(Strings.EMPTY), eq(IntegrationbackofficeConstants.NOTIFICATION_TYPE),
				eq(NotificationEvent.Level.WARNING), any(ExportConfigurationEntityNotSelectedException.class));
		assertNull("Selected entity is null.", selectedEntity);
	}

	@Test
	public void addMapEntryWhenSetSelectedEntityAndEntityDoesNotExistAsKeyInMap()
	{
		final IntegrationObjectModel integrationObjectModel = integrationObjectModel(INTEGRATION_SERVICE_IO_CODE);
		presentation.setSelectedEntity(integrationObjectModel);
		assertEquals("Argument set as selected entity.", integrationObjectModel, presentation.getSelectedEntity());
		assertTrue("Map entry with empty set added.", presentation.getSelectedEntityInstances().isEmpty());
	}

	@Test
	public void mapEntryUnchangedWhenSetSelectedEntityAndEntityExistsAsKeyInMap()
	{
		// setup
		final IntegrationObjectModel integrationServiceIO = integrationObjectModel(INTEGRATION_SERVICE_IO_CODE);
		final IntegrationObjectModel webhookServiceIO = integrationObjectModel(WEBHOOK_SERVICE_IO_CODE);
		final Set<ItemModel> selectedEntityInstances = Set.of(new ItemModel());
		presentation.setSelectedEntity(integrationServiceIO);
		presentation.setSelectedEntityInstances(selectedEntityInstances);
		presentation.setSelectedEntity(webhookServiceIO);

		// test
		presentation.setSelectedEntity(integrationServiceIO);
		assertEquals("Argument set as selected entity.", integrationServiceIO, presentation.getSelectedEntity());
		assertEquals("Selected instances remains the same.", selectedEntityInstances, presentation.getSelectedEntityInstances());
	}

	@Test
	public void returnRootItemTypeCodeWhenGetSelectedEntityRootTypeAndWhenSelectedEntityNotNull()
	{
		// setup
		final ComposedTypeModel composedTypeModel = composedTypeModel(ROOT_ITEM_TYPE_CODE);
		final IntegrationObjectItemModel integrationObjectItemModel = integrationObjectItemModel(ROOT_ITEM_CODE,
				composedTypeModel);
		final IntegrationObjectModel integrationObjectModel = IntegrationbackofficetestUtils.integrationObjectModelMock(INTEGRATION_SERVICE_IO_CODE,
				integrationObjectItemModel);
		presentation.setSelectedEntity(integrationObjectModel);

		// test
		final String root = presentation.getSelectedEntityRoot();
		assertNotEquals("Does not return root item code (alias).", ROOT_ITEM_CODE, root);
		assertEquals("Returns root item type code.", ROOT_ITEM_TYPE_CODE, root);
	}

	@Test
	public void returnEmptyStringWhenGetSelectedEntityRootTypeAndWhenSelectedEntityNull()
	{
		assertEquals("Returns empty string.", "", presentation.getSelectedEntityRoot());
		verify(notificationService, times(1)).notifyUser(eq(Strings.EMPTY), eq(IntegrationbackofficeConstants.NOTIFICATION_TYPE),
				eq(NotificationEvent.Level.WARNING), any(ExportConfigurationEntityNotSelectedException.class));
	}

	@Test
	public void warningNotificationWhenSetSelectedEntityInstancesSelectedEntityNull()
	{
		presentation.setSelectedEntityInstances(new HashSet<>());
		verify(notificationService, times(1)).notifyUser(eq(Strings.EMPTY), eq(IntegrationbackofficeConstants.NOTIFICATION_TYPE),
				eq(NotificationEvent.Level.WARNING), any(ExportConfigurationEntityNotSelectedException.class));
	}

	@Test
	public void putCopyOfArgumentInMapWhenSetSelectedEntityInstancesArgumentNotNull()
	{
		// setup
		final Set<ItemModel> selectedEntityInstances1 = Set.of(new ItemModel());
		final Set<ItemModel> selectedEntityInstances2 = mutableSetOfItemModels(new ItemModel());
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));

		// test
		presentation.setSelectedEntityInstances(selectedEntityInstances1);
		assertEquals("Argument is put as value in map.", selectedEntityInstances1,
				presentation.getSelectedEntityInstances());

		presentation.setSelectedEntityInstances(selectedEntityInstances2);
		assertEquals("Argument is put as value in map again.", selectedEntityInstances2,
				presentation.getSelectedEntityInstances());

		selectedEntityInstances2.clear();
		assertFalse("Set in presentation class not updated from argument.",
				presentation.getSelectedEntityInstances().isEmpty());
	}

	@Test
	public void putEmptySetInMapWhenSetSelectedEntityInstancesArgumentNull()
	{
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));
		presentation.setSelectedEntityInstances(null);
		assertTrue("Selected entity instances is empty set.", presentation.getSelectedEntityInstances().isEmpty());
	}


	@Test
	public void returnCopyOfSelectedEntityInstancesWhenGetSelectedEntityInstancesAndSelectedEntityNotNull()
	{
		// setup
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));
		presentation.setSelectedEntityInstances(mutableSetOfItemModels(new ItemModel()));

		// test
		final Set<ItemModel> copyOfSelectedEntityInstances = presentation.getSelectedEntityInstances();
		copyOfSelectedEntityInstances.clear();
		assertFalse("Set in presentation class not updated through getter.",
				presentation.getSelectedEntityInstances().isEmpty());
	}

	@Test
	public void returnEmptySetWhenGetSelectedEntityInstancesAndSelectedEntityNull()
	{
		assertTrue("Returns empty set.", presentation.getSelectedEntityInstances().isEmpty());
		verify(notificationService, times(1)).notifyUser(eq(Strings.EMPTY), eq(IntegrationbackofficeConstants.NOTIFICATION_TYPE),
				eq(NotificationEvent.Level.WARNING), any(ExportConfigurationEntityNotSelectedException.class));
	}


	@Test
	public void mapClearedAndSelectedEntitySetToNullWhenClearSelection()
	{
		// setup
		final IntegrationObjectModel integrationObjectModel = integrationObjectModel(INTEGRATION_SERVICE_IO_CODE);
		presentation.setSelectedEntity(integrationObjectModel);
		presentation.setSelectedEntityInstances(Set.of(new ItemModel()));

		// test
		presentation.clearSelection();
		assertNull("Selected entity is null.", presentation.getSelectedEntity());
		presentation.setSelectedEntity(integrationObjectModel);
		assertTrue("Set of instances cleared.", presentation.getSelectedEntityInstances().isEmpty());
	}

	@Test
	public void isAnyInstanceSelectedTrueOnlyIfNonEmptySetOfInstancesExists()
	{
		assertFalse("Empty map should return false.", presentation.isAnyInstanceSelected());
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));
		presentation.setSelectedEntity(integrationObjectModel(WEBHOOK_SERVICE_IO_CODE));
		assertFalse("All empty instances sets should return false.", presentation.isAnyInstanceSelected());
		presentation.setSelectedEntityInstances(Set.of(new ItemModel()));
		assertTrue("Any instances set not empty should return true.", presentation.isAnyInstanceSelected());
	}

	@Test
	public void testGenerateConfigurationBundleEntity()
	{
		// setup
		final Set<ItemModel> integrationServiceInstances = Set.of(itemModelMock(PKs[0]), itemModelMock(PKs[1]));
		final Set<ItemModel> scriptServiceInstances = Set.of(itemModelMock(PKs[2]));
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));
		presentation.setSelectedEntityInstances(integrationServiceInstances);
		presentation.setSelectedEntity(integrationObjectModel(WEBHOOK_SERVICE_IO_CODE));
		presentation.setSelectedEntity(integrationObjectModel(SCRIPT_SERVICE_IO_CODE));
		presentation.setSelectedEntityInstances(scriptServiceInstances);
		presentation.setSelectedEntity(integrationObjectModel(OUTBOUND_CHANNEL_CONFIG_IO_CODE));

		// test
		final ConfigurationBundleEntity configBundleEntity = presentation.generateConfigurationBundleEntity();
		final Set<IntegrationObjectBundleEntity> bundles = configBundleEntity.getIntegrationObjectBundles();
		final IntegrationObjectBundleEntity integrationServiceBundle = findBundle(bundles, INTEGRATION_SERVICE_IO_CODE);
		final IntegrationObjectBundleEntity webhookServiceBundle = findBundle(bundles, WEBHOOK_SERVICE_IO_CODE);
		final IntegrationObjectBundleEntity scriptServiceBundle = findBundle(bundles, SCRIPT_SERVICE_IO_CODE);
		final IntegrationObjectBundleEntity outboundChannelConfigBundle = findBundle(bundles, OUTBOUND_CHANNEL_CONFIG_IO_CODE);

		assertNull(WEBHOOK_SERVICE_IO_CODE + " bundle shouldn't exist", webhookServiceBundle);
		assertNull(OUTBOUND_CHANNEL_CONFIG_IO_CODE + " bundle shouldn't exist", outboundChannelConfigBundle);

		final Set<String> integrationServiceBundleInstancePks = integrationServiceBundle.getRootItemInstancePks();
		final Set<String> scriptServiceBundleInstancePks = scriptServiceBundle.getRootItemInstancePks();

		assertEquals("Num of " + INTEGRATION_SERVICE_IO_CODE + " instances should be the same",
				integrationServiceInstances.size(),
				integrationServiceBundleInstancePks.size());
		assertEquals("Num of " + SCRIPT_SERVICE_IO_CODE + " instances should be the same",
				scriptServiceInstances.size(),
				scriptServiceBundleInstancePks.size());

		assertTrue(INTEGRATION_SERVICE_IO_CODE + " bundle instance pks match",
				integrationServiceBundleInstancePks.containsAll(Set.of(Long.toString(PKs[0]), Long.toString(PKs[1]))));
		assertTrue(SCRIPT_SERVICE_IO_CODE + " bundle instance pks match",
				scriptServiceBundleInstancePks.contains(Long.toString(PKs[2])));
	}

	@Test
	public void testGetSelectedInstanceCountForEntityPresentReturnInstanceCount()
	{
		// setup
		final Set<ItemModel> integrationServiceInstances = Set.of(itemModelMock(PKs[0]), itemModelMock(PKs[1]));
		presentation.setSelectedEntity(integrationObjectModel(INTEGRATION_SERVICE_IO_CODE));
		presentation.setSelectedEntityInstances(integrationServiceInstances);

		final int count = presentation.getSelectedInstancesCountForEntity(INTEGRATION_SERVICE_IO_CODE);


		assertEquals(integrationServiceInstances.size(), count);
	}

	@Test
	public void testGetSelectedInstanceCountForEntityNotPresentReturnZero()
	{
		final int count = presentation.getSelectedInstancesCountForEntity(WEBHOOK_SERVICE_IO_CODE);

		assertEquals(0, count);
	}

	private IntegrationObjectBundleEntity findBundle(final Set<IntegrationObjectBundleEntity> bundles,
	                                                 final String integrationObjectCode)
	{
		return bundles.stream()
		              .filter(bundle -> bundle.getIntegrationObjectCode().equals(integrationObjectCode))
		              .findAny()
		              .orElse(null);
	}
}