/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@IntegrationTest
public class DefaultOutboundIntegrationObjectJsonGeneratorIntegrationTest extends ServicelayerTest
{
	private static final String READ_SERVICE_BEAN = "readService";
	private static final String JSON_GENERATOR_BEAN = "jsonGenerator";
	private static final String TEST_NAME = "IntegrationObjectOutboundJsonGenerator";
	private static final String READ_ONLY_JSON_IO = TEST_NAME + "_ProductIO_1";
	private static final String VA_JSON_IO = TEST_NAME + "_ProductIO_VA";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;
	@Resource
	private SchemaGenerator oDataSchemaGenerator;
	@Resource
	private ReadOnlyAttributesConfiguration defaultIntegrationServicesConfiguration;

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(ReadService.class, READ_SERVICE_BEAN)
	                                                           .register(DefaultOutboundIntegrationObjectJsonGenerator.class,
			                                                           JSON_GENERATOR_BEAN);

	private DefaultOutboundIntegrationObjectJsonGenerator jsonGenerator;
	private Gson gson;

	@Before
	public void setUp()
	{
		ReadService readService = (ReadService) rule.getBean(ReadService.class, READ_SERVICE_BEAN);
		readService.setFlexibleSearchService(flexibleSearchService);
		readService.setTypeService(typeService);
		readService.setODataDefaultSchemaGenerator(oDataSchemaGenerator);
		readService.setReadOnlyAttributesConfiguration(defaultIntegrationServicesConfiguration);
		jsonGenerator = (DefaultOutboundIntegrationObjectJsonGenerator) rule.getBean(
				DefaultOutboundIntegrationObjectJsonGenerator.class, JSON_GENERATOR_BEAN);
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@After
	public void tearDown()
	{
		Arrays.asList(READ_ONLY_JSON_IO)
		      .forEach(objectCode -> IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				      it -> it.getCode().equals(objectCode)));
	}

	private void setReadOnlyJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + READ_ONLY_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;",
				"                                   ; $ioCode; Product       ; Product       ; true;",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                     ; true;",
				"                                            ; $ioCode:Product       ; creationtime  ; Product:creationtime  ;                     ; ;",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                     ; true;",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;",
				"                                            ; $ioCode:Product       ; modifiedtime  ; Product:modifiedtime  ;                     ; ;",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                     ; true;",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;"
		);
	}

	private void setVirtualAttributeJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + VA_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;",
				"                                   ; $ioCode; Product       ; Product       ; true;",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                     ; true;",
				"                                            ; $ioCode:Product       ; creationtime  ; Product:creationtime  ;                     ; ;",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                     ; true;",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;",
				"                                            ; $ioCode:Product       ; modifiedtime  ; Product:modifiedtime  ;                     ; ;",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                     ; true;",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;",

				"INSERT_UPDATE IntegrationObjectItemVirtualAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; retrievalDescriptor(code)",
				"; $ioCode:Product       ; testVa; testVa",

				"INSERT_UPDATE IntegrationObjectVirtualAttributeDescriptor; code[unique = true]; logicLocation; type(code)",
				"; testVa; model://modelScriptForTest; java.lang.String "
		);
	}

	@Test
	public void readOnlyJsonTest() throws FileNotFoundException, ImpExException
	{
		setReadOnlyJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/outbound/ReadOnlyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(READ_ONLY_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void virtualAttributeJsonTest() throws FileNotFoundException, ImpExException
	{
		setVirtualAttributeJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/outbound/VirtualAttributeJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(VA_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	public static Map<String, Object> loadPayload(final String payloadLocation) throws FileNotFoundException
	{
		final ClassLoader classLoader = DefaultPersistenceIntegrationObjectJsonGeneratorIntegrationTest.class.getClassLoader();
		final URL url = classLoader.getResource(payloadLocation);
		if (url != null)
		{
			final File file = new File(url.getFile());
			final Gson gson = new Gson();
			final JsonReader reader = new JsonReader(new FileReader(file));
			return gson.fromJson(reader, Map.class);
		}
		return null;
	}
}
