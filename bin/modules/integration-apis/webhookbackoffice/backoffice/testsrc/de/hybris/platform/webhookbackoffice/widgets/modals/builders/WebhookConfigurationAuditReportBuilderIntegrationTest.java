/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.widgets.modals.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.internal.config.DefaultAuditConfigService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.integrationbackoffice.rules.ItemTypeAuditEnableRule;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spock.lang.Shared;

@IntegrationTest
public class WebhookConfigurationAuditReportBuilderIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "WebhookConfigurationAuditReportBuilder";
	private static final String IO_AUDIT_REPORT_TEST = TEST_NAME + "_ProductIO";
	private static final String EVENT_TYPE = "de.hybris.platform.webhookservices.event.ItemSavedEvent";
	private static final String ENDPOINT_ID = TEST_NAME + "_webhookEndpoint_4";
	private static final String BASIC_CREDENTIALS = TEST_NAME + "_webhookBasicCred_4";
	private static final String DESTINATIONTARGET_ID = TEST_NAME + "webhookDestinationTarget_4";
	private static final String CONSUME_DESTINATION = "CDForWebhookAuditReportTest";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private DefaultAuditConfigService auditConfigService;
	@Shared
	@ClassRule
	public static final ItemTypeAuditEnableRule auditEnableRule = ItemTypeAuditEnableRule.create().destinationTarget().enable();

	private WebhookConfigurationAuditReportBuilder auditReportBuilder = new WebhookConfigurationAuditReportBuilder();

	@Before
	public void setUp() throws Exception
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		final AbstractBeanDefinition validationDefinition = BeanDefinitionBuilder.rootBeanDefinition(
				WebhookConfigurationAuditReportBuilder.class).getBeanDefinition();
		beanFactory.registerBeanDefinition("webhookConfigurationAuditReportBuilder", validationDefinition);
		auditReportBuilder = (WebhookConfigurationAuditReportBuilder) Registry.getApplicationContext()
		                                                                      .getBean("webhookConfigurationAuditReportBuilder");

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE DestinationTarget; id[unique = true]             ; destinationChannel(code); registrationStatus(code)",
				"                               ; " + DESTINATIONTARGET_ID + "  ; WEBHOOKSERVICES          ; REGISTERED",
				"INSERT_UPDATE EventConfiguration; eventClass[unique = true]                               ; exportName                     ; exportFlag  ; extensionName   ; destinationTarget(id)[unique = true]             ; version[unique = true]  ; priority(code)",
				"                                ; " + EVENT_TYPE + "                                      ; webhookservices.ItemSavedEvent ; true        ; webhookservices ; " + DESTINATIONTARGET_ID + "                     ; 1                       ; CRITICAL",
				"",
				"INSERT_UPDATE IntegrationObject; code[unique = true]         ; integrationType(code)",
				"                               ; " + IO_AUDIT_REPORT_TEST + "; INBOUND",
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)       ; root[default = false]; itemTypeMatch(code)",
				"                                   ; " + IO_AUDIT_REPORT_TEST + "          ; Catalog            ; Catalog          ;                      ;  ;",
				"                                   ; " + IO_AUDIT_REPORT_TEST + "          ; CatalogVersion     ; CatalogVersion   ;                      ;  ;",
				"                                   ; " + IO_AUDIT_REPORT_TEST + "          ; Product            ; Product          ; true                 ;  ;",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":Catalog                               ; id                          ; Catalog:id                                          ;                                                          ; true ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":CatalogVersion                        ; version                     ; CatalogVersion:version                              ;                                                          ; true ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":CatalogVersion                        ; active                      ; CatalogVersion:active                               ;                                                          ;      ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":CatalogVersion                        ; catalog                     ; CatalogVersion:catalog                              ; " + IO_AUDIT_REPORT_TEST + ":Catalog                     ; true ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":Product                               ; name                        ; Product:name                                        ;                                                          ;      ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":Product                               ; code                        ; Product:code                                        ;                                                          ; true ;",
				"                                            ; " + IO_AUDIT_REPORT_TEST + ":Product                               ; catalogVersion              ; Product:catalogVersion                              ; " + IO_AUDIT_REPORT_TEST + ":CatalogVersion            	 ; true ;",
				"",
				"INSERT_UPDATE Endpoint; id[unique = true]    ; specUrl                     ; name              ; version",
				"                      ; " + ENDPOINT_ID + "  ; http://webhook.endpoint.com ; " + ENDPOINT_ID + " ; 1",
				"",
				"INSERT_UPDATE BasicCredential; id[unique = true]       ; username   ; password",
				"                             ; " + BASIC_CREDENTIALS + " ; admin      ; nimda",
				"",
				"INSERT_UPDATE ConsumedDestination; id[unique = true]           ; url                             ; destinationTarget(id)             ; endpoint(id)      ; credential(id)",
				"                                 ; " + CONSUME_DESTINATION + " ; https://localhost:8080/Products ; " + DESTINATIONTARGET_ID + "      ; " + ENDPOINT_ID + " ; " + BASIC_CREDENTIALS + "",
				"",
				"INSERT_UPDATE WebhookConfiguration; integrationObject(code)[unique = true]; destination(id)[unique = true]",
				"                                  ; " + IO_AUDIT_REPORT_TEST + "          ; " + CONSUME_DESTINATION + "");

		importCsv("/impex/essentialdata-DefaultAuditReportBuilderTemplate.impex",
				"UTF-8");  //This file is too big to use IntegrationTestUtil.importImpEx.

		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(
				classLoader.getResource("webhookbackoffice-WebhookConfiguration-audit.xml").getFile());
		final String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
		auditConfigService.storeConfiguration("WebhookConfigurationReport", content);

		auditReportBuilder.setConfigName("WebhookConfigurationReport");
	}

	@Test
	public void generateAndCompareAuditReportTest() throws IOException
	{
		// generate first audit report from one integration object
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = '" + IO_AUDIT_REPORT_TEST + "')");
		final IntegrationObjectModel IOForTest = search.getResult().get(0);
		final SearchResult<WebhookConfigurationModel> searchResult = flexibleSearchService.search(String.format(
				"SELECT PK FROM {WebhookConfiguration} WHERE (p_integrationobject = '%s')", IOForTest.getPk()));
		final WebhookConfigurationModel webhook = searchResult.getResult().get(0);

		final Map<String, InputStream> reportGenerateRes = auditReportBuilder.generateAuditReport(webhook);
		final String htmlContent = getReportContent(new String(getBytesFromReport(reportGenerateRes)));
		final JsonParser parser = new JsonParser();
		final JsonObject jsonObjectNew = parser.parse(htmlContent).getAsJsonObject();

		// ensure the report is generated by checking some key object codes.
		assertEquals("\"anonymous\"", getReportUser(jsonObjectNew));
		final JsonObject webhookJson = getWebhookConfiguration(jsonObjectNew);
		assertEquals("\"" + IO_AUDIT_REPORT_TEST + "\"", getIOCode(webhookJson));
		assertEquals("\"" + EVENT_TYPE + "\"", getEventType(webhookJson));
		assertEquals("\"" + ENDPOINT_ID + "\"", getEndpointId(webhookJson));
		assertEquals("\"" + DESTINATIONTARGET_ID + "\"", getDestinationTargetId(webhookJson));
	}

	private String getReportContent(String htmlFile)
	{
		htmlFile = htmlFile.substring(htmlFile.indexOf("<script>") + 8, htmlFile.indexOf("</script>"));
		htmlFile = htmlFile.substring(htmlFile.indexOf("=") + 1, htmlFile.lastIndexOf(";"));
		return htmlFile.substring(htmlFile.indexOf("[") + 1, htmlFile.lastIndexOf("]"));
	}

	private byte[] getBytesFromReport(final Map<String, InputStream> reportGenerateRes) throws IOException
	{
		assertEquals(1, reportGenerateRes.values().size());
		byte[] arr1 = null;
		for (final InputStream inputStream : reportGenerateRes.values())
		{
			arr1 = inputStream.readAllBytes();
		}
		assertNotNull("Audit report is not generated. No data found.", arr1);
		return arr1;
	}

	private JsonObject getWebhookConfiguration(final JsonObject jsonObject)
	{
		return jsonObject.getAsJsonObject("payload").getAsJsonObject("WebhookConfiguration");
	}

	private String getIOCode(final JsonObject webhookJson)
	{
		return webhookJson.getAsJsonObject("IntegrationObject").get("IntegrationObject_id").toString();
	}

	private String getEventType(final JsonObject webhookJson)
	{
		return webhookJson.get("eventType").toString();
	}

	private String getEndpointId(final JsonObject webhookJson)
	{
		return webhookJson.getAsJsonObject("ConsumedDestination").getAsJsonObject("Endpoint").get("Endpoint_id").toString();
	}

	private String getDestinationTargetId(final JsonObject webhookJson)
	{
		return webhookJson.getAsJsonObject("ConsumedDestination")
		                  .getAsJsonObject("DestinationTarget")
		                  .get("DestinationTarget_id")
		                  .toString();
	}

	private String getReportUser(final JsonObject jsonObject)
	{
		return jsonObject.get("changingUser").toString();
	}
}