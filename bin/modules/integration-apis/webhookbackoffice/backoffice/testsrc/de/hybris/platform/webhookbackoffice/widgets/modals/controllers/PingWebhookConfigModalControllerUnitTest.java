package de.hybris.platform.webhookbackoffice.widgets.modals.controllers;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.util.notifications.NotificationService;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationbackoffice.widgets.modals.generator.IntegrationObjectJsonGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigurationValidationException;
import de.hybris.platform.webhookservices.filter.WebhookFilterService;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.service.WebhookValidationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import static de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants.NOTIFICATION_TYPE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import java.util.Optional;


@DeclaredInput(value = PingWebhookConfigModalController.WEBHOOK_CONFIG_PARAM, socketType = WebhookConfigurationModel.class)
@DeclaredViewEvent(componentID = PingWebhookConfigModalController.WEBHOOK_SEND_BUTTON, eventName = Events.ON_CLICK)
@NullSafeWidget(true)
public class PingWebhookConfigModalControllerUnitTest extends AbstractWidgetUnitTest<PingWebhookConfigModalController>
{

	@Mock(lenient = true)
	private IntegrationObjectJsonGenerator integrationObjectJsonGenerator;
	@Mock(lenient = true)
	private NotificationService notificationService;
	@Mock(lenient = true)
	private WebhookValidationService defaultWebhookValidationService;
	@Mock(lenient = true)
	private WidgetInstanceManager widgetInstanceManager;
	@Mock(lenient = true)
	private WebhookFilterService defaultWebhookFilterService;

	@InjectMocks
	private PingWebhookConfigModalController controller;

	private static final String DESTINATION_URL = "webhook/destination/url";
	private static final String TEST_ITEM_TYPE = "Test Item";
	private static final String RESOURCE_NOT_FOUND_ERROR = "404, the server can not find the requested resource.";
	private static final String[] mockedPayloadSuccessLabels = { DESTINATION_URL };
	private static final String[] mockedPayloadFailureLabels = { DESTINATION_URL, RESOURCE_NOT_FOUND_ERROR };
	private static final String[] ioEntityTypeLabel = { TEST_ITEM_TYPE };
	private static final String FILTER_LOCATION = "model://testLocation";
	private static final String POPUP_SUCCESS_MSG = "Mocked JSON payload successfully sent out";
	private static final String SELECT_ENTITY_ERROR = "Please select an existing entity or create one.";
	private static final String ITEM_FILTERED_OUT_WARNING = "Item has being filter out from sending to destination.";

	protected static final String PING_WEBHOOK_CONFIG_SUCCESS_LABEL = "webhookbackoffice.pingWebhookConfiguration.info.msg.pingSuccess";
	protected static final String PING_WEBHOOK_CONFIG_FAILURE_LABEL = "webhookbackoffice.pingWebhookConfiguration.info.msg.pingFailure";
	protected static final String ERROR_404_LABEL = "webhookbackoffice.pingWebhookConfiguration.error.msg.404";
	protected static final String SELECT_ENTITY_ERROR_LABEL = "webhookbackoffice.pingWebhookConfiguration.info.msg.selectEntityError";
	protected static final String ITEM_FILTERED_OUT_WARNING_LABEL = "webhookbackoffice.pingWebhookConfiguration.info.msg.itemFilteredOutWarning";


	@Before
	public void setUp()
	{
		lenient().when(widgetInstanceManager.getLabel(PING_WEBHOOK_CONFIG_SUCCESS_LABEL, mockedPayloadSuccessLabels)).thenReturn(
				POPUP_SUCCESS_MSG);
		lenient().when(widgetInstanceManager.getLabel(PING_WEBHOOK_CONFIG_FAILURE_LABEL, mockedPayloadFailureLabels)).thenReturn(
				RESOURCE_NOT_FOUND_ERROR);
		lenient().when(widgetInstanceManager.getLabel(ITEM_FILTERED_OUT_WARNING_LABEL, ioEntityTypeLabel)).thenReturn(
				ITEM_FILTERED_OUT_WARNING);

		lenient().when(widgetInstanceManager.getLabel(ERROR_404_LABEL)).thenReturn(RESOURCE_NOT_FOUND_ERROR);
		lenient().when(widgetInstanceManager.getLabel(SELECT_ENTITY_ERROR_LABEL)).thenReturn(SELECT_ENTITY_ERROR);
		lenient().when(widgetInstanceManager.getLabel(ITEM_FILTERED_OUT_WARNING_LABEL)).thenReturn(ITEM_FILTERED_OUT_WARNING);

		controller.webhookbackofficeJsonTextBox = new Textbox();
		controller.webhookbackofficeJsonTextDestinationPathBox = new Textbox();
		controller.webhookbackofficeIODestinationPathBox = new Textbox();
		controller.itemModelInstanceEditor = new Editor();
		controller.webhookbackofficeMetadataTabbox = new Tabbox();
		controller.webhookbackofficeJsonTab = mock(Tab.class);
		controller.webhookbackofficeIntegrationObjectInstanceTab = mock(Tab.class);
	}

	@Test
	public void loadTestWebhookConfigurationModalIgnoresNullWebhookConfigurations()
	{
		final String previousTextBoxValue = "some previously set text box value";

		controller.webhookbackofficeJsonTextBox.setText(previousTextBoxValue);

		executeInputSocketEvent(PingWebhookConfigModalController.WEBHOOK_CONFIG_PARAM, (Object) null);

		assertThat(controller.webhookConfiguration).isNull();
		assertThat(controller.webhookbackofficeJsonTextBox.getText()).isEqualTo(previousTextBoxValue);
	}

	@Test
	public void displaysTheIntegrationObjectJsonPresentationWhenWebhookConfigurationIsNotNull()
	{
		final String generatedJSON = "{json IO presentation}";
		final WebhookConfigurationModel webhookConfiguration = givenWebhookConfigResultsInJsonGeneration(generatedJSON);

		executeInputSocketEvent(PingWebhookConfigModalController.WEBHOOK_CONFIG_PARAM, webhookConfiguration);

		assertThat(controller.webhookConfiguration).isEqualTo(webhookConfiguration);
		assertThat(controller.webhookbackofficeJsonTextBox.getText()).isEqualTo(generatedJSON);
		assertThat(controller.webhookbackofficeJsonTextDestinationPathBox.getText()).isEqualTo(DESTINATION_URL);
	}

	@Test
	public void popUpSuccessNotificationWhenMockedPayloadSent() throws WebhookConfigurationValidationException
	{

		final String generatedJSON = "{mocked IO payload}";

		controller.webhookbackofficeJsonTextBox.setText(generatedJSON);
		controller.webhookConfiguration = getWebhookConfig();

		lenient().when(controller.webhookbackofficeJsonTab.getTabbox()).thenReturn(controller.webhookbackofficeMetadataTabbox);

		controller.webhookbackofficeMetadataTabbox.setSelectedTab(controller.webhookbackofficeJsonTab);
		controller.setWidgetInstanceManager(widgetInstanceManager);

		executeViewEvent(PingWebhookConfigModalController.WEBHOOK_SEND_BUTTON, Events.ON_CLICK);

		then(notificationService).should()
		                         .notifyUser(eq(Strings.EMPTY), eq(NOTIFICATION_TYPE), eq(NotificationEvent.Level.SUCCESS),
				                         eq(POPUP_SUCCESS_MSG));
	}

	@Test
	public void popUpFailureNotificationWhenSendMockedPayloadWithServerCannotFindRequestedResource()
			throws WebhookConfigurationValidationException
	{
		final String generatedJSON = "{mocked IO payload}";

		controller.webhookbackofficeJsonTextBox.setText(generatedJSON);
		controller.webhookConfiguration = getWebhookConfig();

		lenient().when(controller.webhookbackofficeJsonTab.getTabbox()).thenReturn(controller.webhookbackofficeMetadataTabbox);

		controller.webhookbackofficeMetadataTabbox.setSelectedTab(controller.webhookbackofficeJsonTab);
		controller.setWidgetInstanceManager(widgetInstanceManager);
		doThrow(new WebhookConfigurationValidationException("NOT_FOUND",
				new HttpClientErrorException(HttpStatus.NOT_FOUND))).when(defaultWebhookValidationService)
		                                                            .pingWebhookDestination(controller.webhookConfiguration,
				                                                            generatedJSON);

		executeViewEvent(PingWebhookConfigModalController.WEBHOOK_SEND_BUTTON, Events.ON_CLICK);

		then(notificationService).should()
		                         .notifyUser(eq(Strings.EMPTY), eq(NOTIFICATION_TYPE), eq(NotificationEvent.Level.FAILURE),
				                         eq(RESOURCE_NOT_FOUND_ERROR));
	}

	@Test
	public void popUpSelectOrCreateEntityNotificationWhenNoIntegrationObjectInstanceSelectedInTheEditor()
	{
		controller.webhookConfiguration = getWebhookConfig();

		lenient().when(controller.webhookbackofficeIntegrationObjectInstanceTab.getTabbox()).thenReturn(controller.webhookbackofficeMetadataTabbox);

		controller.webhookbackofficeMetadataTabbox.setSelectedTab(controller.webhookbackofficeIntegrationObjectInstanceTab);
		controller.setWidgetInstanceManager(widgetInstanceManager);

		executeViewEvent(PingWebhookConfigModalController.WEBHOOK_SEND_BUTTON, Events.ON_CLICK);

		then(notificationService).should()
		                         .notifyUser(eq(Strings.EMPTY), eq(NOTIFICATION_TYPE), eq(NotificationEvent.Level.FAILURE),
				                         eq(SELECT_ENTITY_ERROR));
	}

	@Test
	public void popUpWarningNotificationWhenIOEntityFilteredOut()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		controller.webhookConfiguration = getWebhookConfig();
		controller.itemModelInstanceEditor.setValue(itemModel);

		lenient().when(controller.webhookbackofficeIntegrationObjectInstanceTab.getTabbox()).thenReturn(controller.webhookbackofficeMetadataTabbox);

		controller.webhookbackofficeMetadataTabbox.setSelectedTab(controller.webhookbackofficeIntegrationObjectInstanceTab);
		controller.setWidgetInstanceManager(widgetInstanceManager);

		lenient().when(defaultWebhookFilterService.filter(itemModel, controller.webhookConfiguration.getFilterLocation())).thenReturn(
				Optional.empty());
		lenient().when(itemModel.getItemtype()).thenReturn(TEST_ITEM_TYPE);

		executeViewEvent(PingWebhookConfigModalController.WEBHOOK_SEND_BUTTON, Events.ON_CLICK);

		then(notificationService).should()
		                         .notifyUser(eq(Strings.EMPTY), eq(NOTIFICATION_TYPE), eq(NotificationEvent.Level.WARNING),
				                         eq(ITEM_FILTERED_OUT_WARNING));
	}

	private WebhookConfigurationModel givenWebhookConfigResultsInJsonGeneration(final String generatedJSON)
	{
		final var ioModel = mock(IntegrationObjectModel.class);
		lenient().when(integrationObjectJsonGenerator.generateJson(ioModel)).thenReturn(generatedJSON);
		final var webhookConfiguration = mock(WebhookConfigurationModel.class);
		lenient().when(webhookConfiguration.getIntegrationObject()).thenReturn(ioModel);
		final var ioItemModel = mock(IntegrationObjectItemModel.class);
		final String typeCode = "Product";
		lenient().when(webhookConfiguration.getIntegrationObject().getRootItem()).thenReturn(ioItemModel);
		lenient().when(ioItemModel.getCode()).thenReturn(typeCode);

		final var consumedDestinationModel = getConsumedDestinationModel();
		lenient().when(webhookConfiguration.getDestination()).thenReturn(consumedDestinationModel);

		return webhookConfiguration;
	}

	private WebhookConfigurationModel getWebhookConfig()
	{
		final ConsumedDestinationModel destination = getConsumedDestinationModel();

		final WebhookConfigurationModel webhookConfiguration = mock(WebhookConfigurationModel.class);
		lenient().when(webhookConfiguration.getDestination()).thenReturn(destination);
		lenient().when(webhookConfiguration.getFilterLocation()).thenReturn(FILTER_LOCATION);

		return webhookConfiguration;
	}

	private ConsumedDestinationModel getConsumedDestinationModel()
	{
		final ConsumedDestinationModel destination = mock(ConsumedDestinationModel.class);
		lenient().when(destination.getUrl()).thenReturn(DESTINATION_URL);

		return destination;
	}

	@Override
	protected PingWebhookConfigModalController getWidgetController()
	{
		return controller;
	}
}
