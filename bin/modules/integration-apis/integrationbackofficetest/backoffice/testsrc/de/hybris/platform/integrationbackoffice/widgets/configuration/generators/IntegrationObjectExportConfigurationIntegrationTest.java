/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.generators;

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute;
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem;
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject;
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder;
import static de.hybris.platform.outboundservices.DestinationTargetBuilder.destinationTarget;
import static de.hybris.platform.webhookservices.EventConfigurationBuilder.eventConfiguration;
import static de.hybris.platform.webhookservices.WebhookConfigurationBuilder.webhookConfiguration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.enums.EventPriority;
import de.hybris.platform.apiregistryservices.enums.RegistrationStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonObject;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity;
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity;
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder;
import de.hybris.platform.outboundservices.DestinationTargetBuilder;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webhookservices.EventConfigurationBuilder;
import de.hybris.platform.webhookservices.WebhookConfigurationBuilder;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import de.hybris.platform.webhookservices.util.WebhookServicesEssentialData;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class IntegrationObjectExportConfigurationIntegrationTest extends ServicelayerTest
{
	private static final String EXPORT_CONFIG_GENERATOR = "exportConfigGenerator";
	private static final String TEST_NAME = "IntegrationObject";
	private static final String IO_CODE = TEST_NAME + "_IO4ExportConfig";
	private static final String integrationObjectItemCode = "PriceRow";
	private static final String integrationObjectAttribute = "price";
	private static final String destinationTargetId = "webhookServicesForTest";
	private static final String consumedDestinationId = "webhooks-cd-exportConfig";
	private static final String itemSavedEvent = "de.hybris.platform.webhookservices.event.ItemSavedEvent";

	private ExportConfigurationGenerator exportConfigGenerator;

	@Rule
	public final ModuleEssentialData essentialData = WebhookServicesEssentialData.webhookServicesEssentialData()
	                                                                             .withDependencies();

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(DefaultExportConfigurationGenerator.class,
			EXPORT_CONFIG_GENERATOR);

	private EventConfigurationBuilder eventConfigurationBuilder;
	private WebhookConfigurationBuilder webhookConfigurationBuilder;

	@Before
	public void setUp() throws ImpExException
	{
		setupEventConfig();
		setupWebhookConfig();
		exportConfigGenerator = (ExportConfigurationGenerator) rule.getBean(DefaultExportConfigurationGenerator.class,
				EXPORT_CONFIG_GENERATOR);
	}

	private void setupEventConfig() throws ImpExException
	{
		final DestinationTargetBuilder destinationTargetBuilder = destinationTarget()
				.withId(destinationTargetId)
				.withDestinationChannel(DestinationChannel.WEBHOOKSERVICES)
				.withRegistrationStatus(RegistrationStatus.REGISTERED);
		eventConfigurationBuilder = eventConfiguration()
				.withEventClass(de.hybris.platform.webhookservices.event.ItemSavedEvent.class)
				.withExportName("webhookservices.ItemSavedEvent")
				.withExport(true)
				.withExtensionName("webhookservices")
				.withDestination(destinationTargetBuilder)
				.withVersion(1)
				.withPriority(EventPriority.CRITICAL);
		eventConfigurationBuilder.build();
	}

	private void setupWebhookConfig() throws ImpExException
	{
		final IntegrationObjectItemAttributeModelBuilder attributeBuilder =
				integrationObjectItemAttribute(integrationObjectAttribute).unique();
		final IntegrationObjectItemModelBuilder itemBuilder = integrationObjectItem()
				.withCode(integrationObjectItemCode)
				.root()
				.withAttribute(attributeBuilder);
		final IntegrationObjectModelBuilder integrationObjectBuilder = integrationObject()
				.withCode(IO_CODE)
				.withItem(itemBuilder);

		final ConsumedDestinationBuilder consumedDestinationBuilder = consumedDestinationBuilder()
				.withId(consumedDestinationId)
				.withDestinationTarget(destinationTargetId);

		webhookConfigurationBuilder = webhookConfiguration()
				.withIntegrationObject(integrationObjectBuilder)
				.withDestination(consumedDestinationBuilder);
		webhookConfigurationBuilder.build();
	}

	@After
	public void cleanUp()
	{
		webhookConfigurationBuilder.cleanup();
		eventConfigurationBuilder.cleanup();
	}

	@Test
	public void testExportMethod()
	{
		final String WEBHOOK_META_SERVICE = "WebhookService";
		final ConfigurationBundleEntity configurationBundleEntity = createConfigurationBundle(contextWebhookConfiguration(),
				WEBHOOK_META_SERVICE);

		final byte[] bytes = exportConfigGenerator.generateExportConfig(configurationBundleEntity);
		final JsonObject jsonData = JsonObject.createFrom(new ByteArrayInputStream(bytes));

		Assert.assertTrue(jsonData.getString("item[0].request.body.raw").contains(integrationObjectAttribute));
		Assert.assertTrue(jsonData.getString("item[0].request.body.raw").contains(integrationObjectItemCode));
		Assert.assertTrue(jsonData.getString("item[1].name").contains(consumedDestinationId));
		Assert.assertTrue(jsonData.getString("item[1].name").contains(destinationTargetId));
		Assert.assertTrue(jsonData.getString("item[1].name").contains(IO_CODE));
		Assert.assertTrue(jsonData.getString("item[1].name").contains(itemSavedEvent));
	}

	private WebhookConfigurationModel contextWebhookConfiguration()
	{
		return IntegrationTestUtil.findAny(WebhookConfigurationModel.class,
				it -> it.getIntegrationObject().getCode().equals(IO_CODE))
		                          .orElseThrow(() -> new IllegalStateException("Context Webhook Configuration not found"));
	}

	private ConfigurationBundleEntity createConfigurationBundle(final WebhookConfigurationModel webhookConfigurationModel,
	                                                            final String WEBHOOK_META_SERVICE)
	{
		final IntegrationObjectBundleEntity integrationObjectBundleEntity = new IntegrationObjectBundleEntity();
		integrationObjectBundleEntity.setIntegrationObjectCode(WEBHOOK_META_SERVICE);
		integrationObjectBundleEntity.setRootItemInstancePks(Set.of(webhookConfigurationModel.getPk().toString()));
		final Set<IntegrationObjectBundleEntity> ioeSet = new HashSet<IntegrationObjectBundleEntity>();
		ioeSet.add(integrationObjectBundleEntity);
		final ConfigurationBundleEntity configurationBundleEntity = new ConfigurationBundleEntity();
		configurationBundleEntity.setIntegrationObjectBundles(ioeSet);
		return configurationBundleEntity;
	}
}
