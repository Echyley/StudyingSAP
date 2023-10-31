/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsyncbackoffice.widgets.models.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.Gson;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OutboundChannelConfigurationAuditReportBuilderUnitTest
{
	@InjectMocks
	private OutboundChannelConfigurationAuditReportBuilder auditReportBuilder = new OutboundChannelConfigurationAuditReportBuilder();

	@Test
	public void testTraversePayload()
	{
		final Gson jsonToMap = new Gson();

		final HashMap<String, String> emptyMap = new HashMap<>();
		auditReportBuilder.traversePayload(emptyMap);
		assertThat(emptyMap).isEmpty();

		final Map<String, String> map1 = Maps.newHashMap("test_id", "test");
		auditReportBuilder.traversePayload(map1);
		assertThat(map1).containsKey("test_id");

		final Map<String, ArrayList<Object>> map2 = Maps.newHashMap("testList", new ArrayList<>());
		auditReportBuilder.traversePayload(map2);
		assertThat(map2).containsEntry("testList", new ArrayList<>());

		// use type_id as key and remove list.
		final HashMap map3 = jsonToMap.fromJson("{CList: [{\"C1_id\": \"1\"}]}", HashMap.class);
		auditReportBuilder.traversePayload(map3);
		final HashMap expected = jsonToMap.fromJson("{\"C1_id : 1\": {}}", HashMap.class);
		assertThat(map3).isEqualTo(expected);

		final HashMap map4 = jsonToMap.fromJson(
				"{CList: [{C1_id: \"1\", C1Name: \"cn\", C1Url: \"sap.com\"}, {C2_id: \"2\", C2Name: \"x\", C2Url: \"Griffin\"}]}",
				HashMap.class);
		auditReportBuilder.traversePayload(map4);
		final HashMap expected4 = jsonToMap.fromJson(
				"{\"C1_id : 1\": {C1Name: \"cn\", C1Url: \"sap.com\"}, \"C2_id : 2\": {C2Name: \"x\", C2Url: \"Griffin\"}}",
				HashMap.class);
		assertThat(map4).isEqualTo(expected4);

		final HashMap map5 = jsonToMap.fromJson(
				"{l1: [{O1_id: \"v1\"}, {O2_id: \"O2_v\", O2_l: [{subO2_key_id: \"v\"}, {subO3_key_id: \"v\"}]}, {}]}",
				HashMap.class);
		auditReportBuilder.traversePayload(map5);
		final HashMap expected5 = jsonToMap.fromJson(
				"{\"O1_id : v1\": {}, \"O2_id : O2_v\": {\"subO3_key_id : v\": {}, \"subO2_key_id : v\": {}}}",
				HashMap.class);
		assertThat(map5).isEqualTo(expected5);

		//use typeA_id of typeA as key in a map.
		final HashMap map6 = jsonToMap.fromJson(
				"{integration: {integration_id: \"A\"}}",
				HashMap.class);
		auditReportBuilder.traversePayload(map6);
		final HashMap expected6 = jsonToMap.fromJson("{\"integration_id : A\": {}}",
				HashMap.class);
		assertThat(map6).isEqualTo(expected6);

		final HashMap map7 = jsonToMap.fromJson(
				"{team: {team_id: \"1\", teamProp: {object: {object_id: \"stout\"}}}}",
				HashMap.class);
		auditReportBuilder.traversePayload(map7);
		final HashMap expected7 = jsonToMap.fromJson("{\"team_id : 1\": {teamProp: {\"object_id : stout\": {}}}}",
				HashMap.class);
		assertThat(map7).isEqualTo(expected7);

	}

	@Test
	public void testGetDownloadFileName()
	{
		final OutboundChannelConfigurationModel itemModel = mock(OutboundChannelConfigurationModel.class);
		auditReportBuilder.setSelectedModel(itemModel);

		lenient().when(itemModel.getCode()).thenReturn("testOutboundChannelConfiguration");

		assertThat(auditReportBuilder.getDownloadFileName()).isEqualTo("testOutboundChannelConfiguration");
	}
}