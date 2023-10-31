/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsyncbackoffice.widgets.models.builders;

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute;
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem;
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject;
import static de.hybris.platform.outboundservices.BasicCredentialBuilder.basicCredentialBuilder;
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder;
import static de.hybris.platform.outboundservices.DestinationTargetBuilder.destinationTarget;
import static de.hybris.platform.outboundservices.EndpointBuilder.endpointBuilder;
import static de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder.outboundChannelConfigurationBuilder;
import static de.hybris.platform.outboundsync.OutboundSyncCronJobBuilder.outboundSyncCronJobBuilder;
import static de.hybris.platform.outboundsync.OutboundSyncJobBuilder.outboundSyncJob;
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder.outboundSyncStreamConfigurationBuilder;
import static de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationContainerBuilder.outboundSyncStreamConfigurationContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.audit.internal.config.DefaultAuditConfigService;
import de.hybris.platform.audit.view.AuditViewService;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.integrationbackoffice.rules.ItemTypeAuditEnableRule;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder;
import de.hybris.platform.outboundsync.OutboundChannelConfigurationBuilder;
import de.hybris.platform.outboundsync.OutboundSyncCronJobBuilder;
import de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationBuilder;
import de.hybris.platform.outboundsync.OutboundSyncStreamConfigurationContainerBuilder;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spock.lang.Shared;

@IntegrationTest
public class OutboundChannelConfigurationAuditReportBuilderIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "OutboundChannelConfigurationAuditReportBuilder";
	private static final String IO = TEST_NAME + "_Product";
	private static final String OUTBOUND_SYNC_STREAM_CONFIGURATION_CONTAINER = TEST_NAME + "_StreamContainer";
	private static final String ODATA_OUTBOUND_SYNC_JOB = TEST_NAME + "_OutboundSyncJob";
	private static final String ODATA_OUTBOUND_SYNC_CRONJOB = TEST_NAME + "_OutboundSyncCronJob";
	private static final String BASIC_CREDENTIAL = TEST_NAME + "_BasicCredentialTest";
	private static final String DESTINATION_TARGET = TEST_NAME + "_DestinationTarget";
	private static final String ENDPOINT = TEST_NAME + "_Endpoint";
	private static final String ENDPOINT_URL = "https://localhost:9002/odata2webservices/OutboundProduct/$metadata?Product";
	private static final String CONSUME_DESTINATION = TEST_NAME + "_ConsumeDestination";
	private static final String CONSUME_DESTINATION_URL = "https://localhost:9002/odata2webservices/OutboundProduct/Products";
	private static final String OUTBOUND_SYNC_STREAM_CONFIGURATION = TEST_NAME + "_OutboundSyncStreamConfiguration";
	private static final String OUTBOUND_CHANNEL_CONFIGURATION = TEST_NAME + "_OutboundChannelConfiguration";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private AuditViewService auditViewService;
	@Resource
	private RendererService rendererService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private UserService userService;
	@Resource
	private List<ReportViewConverterStrategy> reportViewConverterStrategies;
	@Resource
	private DefaultAuditConfigService auditConfigService;
	@Shared
	@ClassRule
	public static final ItemTypeAuditEnableRule auditEnableRule = ItemTypeAuditEnableRule.create().integrationObject().enable();

	private final OutboundChannelConfigurationAuditReportBuilder auditReportBuilder = new OutboundChannelConfigurationAuditReportBuilder();

	@Shared
	private final IntegrationObjectModelBuilder ioBuilder =
			integrationObject().withCode(IO)
			                   .withItem(integrationObjectItem("Product").withType("Product").root()
			                                                             .withAttribute(integrationObjectItemAttribute(
					                                                             "code").withQualifier("code"))
			                                                             .withAttribute(integrationObjectItemAttribute(
					                                                             "catalogVersion").withQualifier("catalogVersion")
			                                                                                      .withReturnItem(
					                                                                                      "CatalogVersion"))
			                                                             .withAttribute(integrationObjectItemAttribute(
					                                                             "name").withQualifier("name"))
			                                                             .withAttribute(integrationObjectItemAttribute(
					                                                             "supercategories").withQualifier(
					                                                             "supercategories").withReturnItem("Category")))
			                   .withItem(integrationObjectItem("Catalog").withType("Catalog")
			                                                             .withAttribute(integrationObjectItemAttribute(
					                                                             "id").withQualifier("id")))
			                   .withItem(integrationObjectItem("CatalogVersion").withType("CatalogVersion")
			                                                                    .withAttribute(integrationObjectItemAttribute(
					                                                                    "catalog").withQualifier("catalog")
			                                                                                      .withReturnItem("Catalog"))
			                                                                    .withAttribute(integrationObjectItemAttribute(
					                                                                    "version").withQualifier("version"))
			                                                                    .withAttribute(integrationObjectItemAttribute(
					                                                                    "active").withQualifier("active")))
			                   .withItem(integrationObjectItem("Category").withType("Category")
			                                                              .withAttribute(integrationObjectItemAttribute(
					                                                              "code").withQualifier("code"))
			                                                              .withAttribute(integrationObjectItemAttribute(
					                                                              "name").withQualifier("name")));
	@Shared
	private final ConsumedDestinationBuilder consumedDestinationBuilder =
			consumedDestinationBuilder().withId(CONSUME_DESTINATION).withUrl(CONSUME_DESTINATION_URL)
			                            .withEndpoint(
					                            endpointBuilder().withId(ENDPOINT).withName(ENDPOINT).withSpecUrl(ENDPOINT_URL))
			                            .withCredential(basicCredentialBuilder().withUsername("abc")
			                                                                    .withId(BASIC_CREDENTIAL)
			                                                                    .withPassword("123"))
			                            .withDestinationTarget(destinationTarget().withId(DESTINATION_TARGET)
			                                                                      .withDestinationChannel(
					                                                                      DestinationChannel.DEFAULT));
	@Shared
	private final OutboundSyncStreamConfigurationContainerBuilder outboundSyncStreamConfigContainerBuilder =
			outboundSyncStreamConfigurationContainer().withId(OUTBOUND_SYNC_STREAM_CONFIGURATION_CONTAINER);
	@Shared
	private final OutboundSyncCronJobBuilder outboundSyncCronJobBuilder =
			outboundSyncCronJobBuilder().withCronJobCode(ODATA_OUTBOUND_SYNC_CRONJOB)
			                            .withSyncJob(outboundSyncJob().withJobCode(ODATA_OUTBOUND_SYNC_JOB)
			                                                          .withOutboundSyncStreamConfigurationContainer(
					                                                          outboundSyncStreamConfigContainerBuilder));
	@Shared
	private final OutboundChannelConfigurationBuilder outboundChannelConfigurationBuilder =
			outboundChannelConfigurationBuilder().withCode(OUTBOUND_CHANNEL_CONFIGURATION)
			                                     .withIntegrationObjectCode(IO)
			                                     .withConsumedDestination(consumedDestinationBuilder)
			                                     .withBatch();
	@Shared
	private final OutboundSyncStreamConfigurationBuilder outboundSyncStreamConfigurationBuilder =
			outboundSyncStreamConfigurationBuilder().withId(OUTBOUND_SYNC_STREAM_CONFIGURATION)
			                                        .withOutboundSyncStreamConfigurationContainer(
					                                        outboundSyncStreamConfigContainerBuilder)
			                                        .withItemType("Product")
			                                        .withOutboundChannelConfiguration(outboundChannelConfigurationBuilder);


	@Before
	public void setUp() throws Exception
	{
		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(
				classLoader.getResource("outboundsyncbackoffice-OutboundChannelConfiguration-audit.xml").getFile());
		final String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);

		ioBuilder.build();
		consumedDestinationBuilder.build();
		outboundSyncCronJobBuilder.build();
		outboundSyncStreamConfigurationBuilder.build();

		importCsv("/impex/essentialdata-DefaultAuditReportBuilderTemplate.impex",
				"UTF-8");  //This file is too big to use IntegrationTestUtil.importImpEx.

		auditConfigService.storeConfiguration("OutboundChannelConfigurationReport", content);
		auditReportBuilder.setAuditViewService(auditViewService);
		auditReportBuilder.setCommonI18NService(commonI18NService);
		auditReportBuilder.setRendererService(rendererService);
		auditReportBuilder.setReportViewConverterStrategies(reportViewConverterStrategies);
		auditReportBuilder.setUserService(userService);
		auditReportBuilder.setConfigName("OutboundChannelConfigurationReport");
		auditReportBuilder.setIsDownload(false);
	}

	@After
	public void cleanup()
	{
		outboundSyncStreamConfigurationBuilder.cleanup();
		outboundSyncStreamConfigContainerBuilder.cleanup();
		outboundChannelConfigurationBuilder.cleanup();
		outboundSyncCronJobBuilder.cleanup();
		consumedDestinationBuilder.reset();
		ioBuilder.cleanup();
	}

	@Test
	public void generateAndCompareAuditReportTest() throws IOException
	{
		// generate first audit report from one integration object
		final SearchResult<OutboundChannelConfigurationModel> search = flexibleSearchService.search(
				"SELECT PK FROM {OutboundChannelConfiguration} WHERE (p_code = '" + OUTBOUND_CHANNEL_CONFIGURATION + "')");
		final OutboundChannelConfigurationModel outboundChannelConfigurationModel = search.getResult().get(0);
		final Map<String, InputStream> reportGenerateRes = auditReportBuilder.generateAuditReport(
				outboundChannelConfigurationModel);
		byte[] arr1 = null;
		assertEquals(1, reportGenerateRes.values().size());
		for (final InputStream inputStream : reportGenerateRes.values())
		{
			arr1 = inputStream.readAllBytes();
		}
		if (arr1 == null)
		{
			fail("Audit report is not generated. No data found.");
		}

		//  if baseline changed, update it
		//  Files.write(Paths.get("./outboundChannelConfigAuditReportBuilderBaseline.html"), arr1);

		final JsonParser parser = new JsonParser();

		// get json object from data just fetched
		String htmlContent = new String(arr1);
		htmlContent = htmlContent.substring(htmlContent.indexOf("<script>") + 8, htmlContent.indexOf("</script>"));
		htmlContent = htmlContent.substring(htmlContent.indexOf("=") + 1, htmlContent.lastIndexOf(";"));
		htmlContent = htmlContent.substring(htmlContent.indexOf("["), htmlContent.lastIndexOf("]") + 1);

		final ClassLoader classLoader = getClass().getClassLoader();
		final String htmlPath = "test/text/OutboundChannelConfigAuditReportBuilderBaseline.html";
		final File file = new File(classLoader.getResource(htmlPath).getFile());
		String htmlContentBaseline = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("<script>") + 8,
				htmlContentBaseline.indexOf("</script>"));
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("=") + 1,
				htmlContentBaseline.lastIndexOf(";"));
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("["),
				htmlContentBaseline.lastIndexOf("]") + 1);
		final JsonArray jsonObjectListNew = parser.parse(htmlContent).getAsJsonArray();
		final JsonObject jsonObjectNew = jsonObjectListNew.get(jsonObjectListNew.size() - 1).getAsJsonObject();
		final JsonArray jsonObjectListBaseline = parser.parse(htmlContentBaseline).getAsJsonArray();
		final JsonObject jsonObjectBaseline = jsonObjectListBaseline.get(jsonObjectListBaseline.size() - 1).getAsJsonObject();

		final JsonObject OBChannelConfigNew = jsonObjectNew.getAsJsonObject("payload")
		                                                   .getAsJsonObject("OutboundChannelConfiguration");
		final JsonObject OBChannelConfigBaseline = jsonObjectBaseline.getAsJsonObject("payload")
		                                                             .getAsJsonObject("OutboundChannelConfiguration");
		assertEquals(OBChannelConfigNew.get("code").toString(), OBChannelConfigBaseline.get("code").toString());
		assertTrue(OBChannelConfigNew.get("batch").getAsBoolean());
		assertEquals(OBChannelConfigNew.get("batch").toString(), OBChannelConfigBaseline.get("batch").toString());

		final JsonObject ConsumedDestinationNew = OBChannelConfigNew.getAsJsonObject(
				"ConsumedDestination_id : " + CONSUME_DESTINATION);
		final JsonObject ConsumedDestinationBase = OBChannelConfigBaseline.getAsJsonObject(
				"ConsumedDestination_id : " + CONSUME_DESTINATION);
		assertEquals(ConsumedDestinationNew.getAsJsonObject("Credential_id : " + BASIC_CREDENTIAL).toString(),
				ConsumedDestinationBase.getAsJsonObject("Credential_id : " + BASIC_CREDENTIAL).toString());
		assertEquals(ConsumedDestinationNew.getAsJsonObject("DestinationTarget_id : " + DESTINATION_TARGET).toString(),
				ConsumedDestinationBase.getAsJsonObject("DestinationTarget_id : " + DESTINATION_TARGET).toString());
		assertEquals(ConsumedDestinationNew.getAsJsonObject("Endpoint_id : " + ENDPOINT).toString(),
				ConsumedDestinationBase.getAsJsonObject("Endpoint_id : " + ENDPOINT).toString());
		assertEquals(ConsumedDestinationNew.getAsJsonObject("Endpoint_id : " + ENDPOINT).toString(),
				ConsumedDestinationBase.getAsJsonObject("Endpoint_id : " + ENDPOINT).toString());
		assertEquals(ConsumedDestinationNew.get("url").toString(),
				ConsumedDestinationBase.get("url").toString());
		assertEquals(ConsumedDestinationNew.get("active").toString(),
				ConsumedDestinationBase.get("active").toString());
		assertEquals(ConsumedDestinationNew.get("additional Properties").toString(),
				ConsumedDestinationBase.get("additional Properties").toString());

		final JsonObject IntegrationObjectNew = OBChannelConfigNew.getAsJsonObject("IntegrationObject_id : " + IO);
		final JsonObject IntegrationObjectBaseline = OBChannelConfigBaseline.getAsJsonObject(
				"IntegrationObject_id : " + IO);
		assertEquals(IntegrationObjectNew.toString(), IntegrationObjectBaseline.toString());

		final JsonObject OutboundSyncStreamConfigurationNew = OBChannelConfigNew.getAsJsonObject(
				"OutboundSyncStreamConfiguration_id : " + OUTBOUND_SYNC_STREAM_CONFIGURATION);
		final JsonObject OutboundSyncStreamConfigurationNewBaseline = OBChannelConfigBaseline.getAsJsonObject(
				"OutboundSyncStreamConfiguration_id : " + OUTBOUND_SYNC_STREAM_CONFIGURATION);
		assertEquals(OutboundSyncStreamConfigurationNew.getAsJsonObject(
						"OutboundSyncStreamConfigurationContainer_id : " + OUTBOUND_SYNC_STREAM_CONFIGURATION_CONTAINER).toString(),
				OutboundSyncStreamConfigurationNewBaseline.getAsJsonObject(
						                                          "OutboundSyncStreamConfigurationContainer_id : " + OUTBOUND_SYNC_STREAM_CONFIGURATION_CONTAINER)
				                                          .toString());
	}
}
