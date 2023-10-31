/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.widgets.editors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.dao.impl.DefaultDestinationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WebhookConsumedDestinationEditorUnitTest
{
	private static final ConsumedDestinationModel WEBHOOK_DESTINATION = consumedDestination(DestinationChannel.WEBHOOKSERVICES);
	private static final ConsumedDestinationModel NOT_WEBHOOK_DESTINATION = consumedDestination(DestinationChannel.DEFAULT);
	private static final ConsumedDestinationModel NOT_CONFIGURED_DESTINATION = new ConsumedDestinationModel();
	private static final ConsumedDestinationModel WEBHOOK_DESTINATION_WITHOUT_DESTINATION_CHANNEL = consumedDestination(null);

	@Mock
	private DefaultDestinationDao<ConsumedDestinationModel> destinationDao;

	@InjectMocks
	private WebhookConsumedDestinationEditor webhookConsumedDestinationEditor;

	@Test
	public void testUpdateReferencesListBoxModel()
	{
		final var consumedDestinations = List
				.of(WEBHOOK_DESTINATION, NOT_WEBHOOK_DESTINATION, NOT_WEBHOOK_DESTINATION, WEBHOOK_DESTINATION,
						NOT_CONFIGURED_DESTINATION, WEBHOOK_DESTINATION_WITHOUT_DESTINATION_CHANNEL);
		lenient().when(destinationDao.findAllConsumedDestinations()).thenReturn(consumedDestinations);

		webhookConsumedDestinationEditor.updateReferencesListBoxModel();

		assertThat(webhookConsumedDestinationEditor.getPageable().getAllResults())
				.containsExactly(consumedDestinations.get(0), consumedDestinations.get(3));
	}

	@Test
	public void testUpdateReferencesListBoxModelWithNoConsumedDestinations()
	{
		lenient().when(destinationDao.findAllConsumedDestinations()).thenReturn(Lists.emptyList());

		webhookConsumedDestinationEditor.updateReferencesListBoxModel();

		assertThat(webhookConsumedDestinationEditor.getPageable().getAllResults()).isEmpty();
	}

	private static ConsumedDestinationModel consumedDestination(DestinationChannel destinationChannel)
	{
		final var destinationTarget = new DestinationTargetModel();
		destinationTarget.setDestinationChannel(destinationChannel);
		final var consumedDestinationModel = new ConsumedDestinationModel();
		consumedDestinationModel.setDestinationTarget(destinationTarget);
		return consumedDestinationModel;
	}

}
