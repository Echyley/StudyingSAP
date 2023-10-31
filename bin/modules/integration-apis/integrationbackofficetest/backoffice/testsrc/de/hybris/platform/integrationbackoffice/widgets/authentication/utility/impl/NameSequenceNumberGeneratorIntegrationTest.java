/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.authentication.utility.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

@IntegrationTest
public class NameSequenceNumberGeneratorIntegrationTest extends ServicelayerTest
{
	private static final String NUMBER_GENERATOR_BEAN = "nameSequenceNumberGenerator";
	private static final String TEST_NAME = "NameSequenceNumberGenerator";
	private static final String END_POINT_TABLE = "EndPoint";
	private static final String EXPOSED_DESTINATION_TABLE = "ExposedDestination";
	private static final String DESTINATION_TARGET = TEST_NAME + "_DestinationTarget_1";

	private static final String DESTINATION_1 = "iocode";
	private static final String DESTINATION_2 = "cc-iocode-1";
	private static final String DESTINATION_3 = "cc-iocode-1a";
	private static final String DESTINATION_4 = "cc-iocode-abc";
	private static final String DESTINATION_5 = "someotheriocode";
	private static final String DESTINATION_6 = "cc-IOCODE-1";
	private static final String DESTINATION_7 = "cc-someotheriocode-1";
	private static final Set<String> destinations = Set.of(DESTINATION_1, DESTINATION_2, DESTINATION_3, DESTINATION_4,
			DESTINATION_5, DESTINATION_6, DESTINATION_7);

	private static final String ENDPOINT_1 = "iocode";
	private static final String ENDPOINT_2 = "cc-" + ENDPOINT_1;
	private static final String ENDPOINT_3 = ENDPOINT_2 + "-metadata";
	private static final String ENDPOINT_4 = ENDPOINT_2 + "-1-metadata";
	private static final String ENDPOINT_5 = "cc-" + ENDPOINT_1.toUpperCase() + "1-metadata";
	private static final String ENDPOINT_6 = ENDPOINT_2 + "-123";
	private static final String ENDPOINT_7 = ENDPOINT_2 + "-abc-metadata";
	private static final String ENDPOINT_8 = "someotheriocode";
	private static final String ENDPOINT_9 = "cc-" + ENDPOINT_8 + "-1-metadata";
	private static final Set<String> endpoints = Set.of(ENDPOINT_1, ENDPOINT_2, ENDPOINT_3, ENDPOINT_4, ENDPOINT_5, ENDPOINT_6,
			ENDPOINT_7, ENDPOINT_8, ENDPOINT_9);

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(NameSequenceNumberGenerator.class,
			NUMBER_GENERATOR_BEAN);

	private NameSequenceNumberGenerator occNameConvention;

	@Before
	public void setUp() throws Exception
	{
		occNameConvention = (NameSequenceNumberGenerator) rule.getBean(NameSequenceNumberGenerator.class, NUMBER_GENERATOR_BEAN);
		insertRecords();
	}

	private void insertRecords() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"$destinationTarget=" + DESTINATION_TARGET,
				"INSERT_UPDATE DestinationTarget;id[unique = true]  ;",
				"                               ;$destinationTarget ;",

				"$version=v1",
				"$specUrl1=https://<your-host-name>/odata2webservices/iocode/$metadata",
				"$specUrl2=https://<your-host-name>/odata2webservices/someotheriocode/$metadata",
				"$name1=iocode-endpoint",
				"$name2=someotheriocode-endpoint",
				"$endpointId1=" + ENDPOINT_1, //iocode
				"$endpointId2=" + ENDPOINT_2, //cc-iocode
				"$endpointId3=" + ENDPOINT_3, //cc-iocode-metadata
				"$endpointId4=" + ENDPOINT_4, //cc-iocode-1-metadata"
				"$endpointId5=" + ENDPOINT_5, //cc-IOCODE-1-metadata"
				"$endpointId6=" + ENDPOINT_6, //cc-iocode-123
				"$endpointId7=" + ENDPOINT_7, //cc-iocode-abc-metadata
				"$endpointId8=" + ENDPOINT_8, //someotheriocode
				"$endpointId9=" + ENDPOINT_9, //cc-someotheriocode-1-metadata

				"INSERT_UPDATE EndPoint ; id[unique = true]; name   ; version  ; specUrl;",
				"                       ; $endpointId1     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId2     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId3     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId4     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId5     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId6     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId7     ; $name1 ; $version ; $specUrl1",
				"                       ; $endpointId8     ; $name2 ; $version ; $specUrl2",
				"                       ; $endpointId9     ; $name2 ; $version ; $specUrl2",

				"$url1=https://<your-host-name>/odata2webservices/iocode",
				"$url2=https://<your-host-name>/odata2webservices/someotheriocode",

				"$destination1=" + DESTINATION_1,
				"$destination2=" + DESTINATION_2,
				"$destination3=" + DESTINATION_3,
				"$destination4=" + DESTINATION_4,
				"$destination5=" + DESTINATION_5,
				"$destination6=" + DESTINATION_6,
				"$destination7=" + DESTINATION_7,
				"INSERT_UPDATE ExposedDestination ; id[unique = true] ; endpoint(id) ; destinationTarget(id) ; url;",
				"                                 ;$endpointId2       ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination1      ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination2      ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination3      ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination4      ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination5      ; $endpointId9 ; $destinationTarget ; $url2",
				"                                 ;$destination6      ; $endpointId3 ; $destinationTarget ; $url1",
				"                                 ;$destination7      ; $endpointId9 ; $destinationTarget ; $url2"
		);
	}

	@After
	public void deleteRecords()
	{
		IntegrationTestUtil.remove(ExposedDestinationModel.class, m -> destinations.contains(m.getId()));
		IntegrationTestUtil.remove(EndpointModel.class, m -> endpoints.contains(m.getId()));
		IntegrationTestUtil.remove(DestinationTargetModel.class, obj -> obj.getId().equals(DESTINATION_TARGET));
	}

	@Test
	public void checkListOfEndPointIDs()
	{
		final List<String> actualEndPointIDs = callGetIDsMethod(END_POINT_TABLE, ENDPOINT_1);
		final List<String> expectedEndPointIDs = Arrays.asList(ENDPOINT_4, ENDPOINT_3);
		assertEquals(actualEndPointIDs, expectedEndPointIDs);
	}

	@Test
	public void checkListOfExposedDestinationIDs()
	{
		final List<String> actualExposedDestinationIDs = callGetIDsMethod(EXPOSED_DESTINATION_TABLE, DESTINATION_1);
		final List<String> expectedExposedDestinationIDs = Arrays.asList(ENDPOINT_2, DESTINATION_2);
		assertEquals(actualExposedDestinationIDs, expectedExposedDestinationIDs);
	}

	private List<String> callGetIDsMethod(final String tableName, final String ioCode)
	{
		final Method method = ReflectionUtils.findMethod(NameSequenceNumberGenerator.class, "getIDs",
				new Class<?>[]{ String.class, String.class });
		method.setAccessible(true);
		return (List<String>) ReflectionUtils.invokeMethod(method, occNameConvention, tableName, ioCode);
	}
}
