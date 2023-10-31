/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.catalogsynchronization;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.SyncItemJobModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class SyncJobSelectionEditorTest
{
	@Spy
	private Combobox syncJobsComboBox = new Combobox();
	@Spy
	private SyncJobSelectionEditor syncJobSelectionEditor = new SyncJobSelectionEditor();

	private List<SyncItemJobModel> getAvailableSyncJobs()
	{
		return Arrays.asList(new SyncItemJobModel(), new SyncItemJobModel(), new SyncItemJobModel());
	}

	@Test
	public void shouldSetSelectedItemNotBeCalledWhenSelectedItemIsNull()
	{
		// given
		final List<SyncItemJobModel> availableSyncJobs = getAvailableSyncJobs();

		// when
		syncJobSelectionEditor.fillComboBox(syncJobsComboBox, availableSyncJobs, null);

		// then
		verify(syncJobsComboBox, never()).setSelectedItem(any());
	}

	@Test
	public void shouldSetSelectedItemBeCalledWhenSelectedItemIsOnTheList()
	{
		// given
		final List<SyncItemJobModel> availableSyncJobs = getAvailableSyncJobs();
		final SyncItemJobModel selectedSyncItem = availableSyncJobs.get(0);

		// when
		syncJobSelectionEditor.fillComboBox(syncJobsComboBox, availableSyncJobs, selectedSyncItem);

		// then
		verify(syncJobsComboBox).setSelectedItem(any());
	}

	@Test
	public void shouldNotInvokeListenerIfSelectedItemIsNull()
	{
		// given
		final Editor parent = new Editor();
		final EditorListener listener = mock(EditorListener.class);
		final List<SyncItemJobModel> availableSyncJobs = getAvailableSyncJobs();
		final SyncItemJobModel selectedSyncItem = availableSyncJobs.get(0);
		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		final HashMap<String, Object> startSyncMap = new HashMap<>();

		startSyncMap.put("syncItemJobs", availableSyncJobs);
		startSyncMap.put("selectedSyncItemJob", selectedSyncItem);
		wim.getModel().put("startSyncForm", startSyncMap);
		parent.setWidgetInstanceManager(wim);
		doNothing().when(syncJobSelectionEditor).fillComboBox(any(), any(), any());
		syncJobSelectionEditor.render(parent, mock(EditorContext.class), listener);
		final Combobox comboBox = findComboBox(parent);

		// when
		CockpitTestUtil.simulateEvent(comboBox, new InputEvent(Events.ON_CHANGE, comboBox, null, "oldValue"));

		// then
		verify(listener, never()).onValueChanged(any());
	}

	@Test
	public void shouldChangeValueIfItemFromComboBoxWasSelected()
	{
		// given
		final Editor parent = new Editor();
		final EditorListener listener = mock(EditorListener.class);
		final List<SyncItemJobModel> availableSyncJobs = getAvailableSyncJobs();
		final SyncItemJobModel selectedSyncItem = availableSyncJobs.get(0);
		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		final HashMap<String, Object> startSyncMap = new HashMap<>();

		startSyncMap.put("syncItemJobs", availableSyncJobs);
		startSyncMap.put("selectedSyncItemJob", selectedSyncItem);
		wim.getModel().put("startSyncForm", startSyncMap);
		parent.setWidgetInstanceManager(wim);
		doNothing().when(syncJobSelectionEditor).fillComboBox(any(), any(), any());
		syncJobSelectionEditor.render(parent, mock(EditorContext.class), listener);
		final Combobox comboBox = findComboBox(parent);
		final Comboitem comboItem = new Comboitem("newValue");
		comboItem.setParent(comboBox);
		comboBox.setSelectedItem(comboItem);

		// when
		CockpitTestUtil.simulateEvent(comboBox, new InputEvent(Events.ON_CHANGE, comboBox, "newValue", "oldValue"));

		// then
		verify(listener).onValueChanged(any());
	}

	private Combobox findComboBox(final Editor parent)
	{
		return parent.getChildren().stream().filter(component -> component instanceof Combobox).map(Combobox.class::cast)
				.findFirst().get();
	}

}
