/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.builders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.auditreport.model.AuditReportTemplateModel;
import de.hybris.platform.auditreport.service.ReportConversionData;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;
import de.hybris.platform.auditreport.service.impl.AbstractTemplateViewConverterStrategy;
import de.hybris.platform.auditreport.service.impl.DefaultReportViewConverterStrategy;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationObjectAuditReportBuilderUnitTest
{
	@InjectMocks
	private IntegrationObjectAuditReportBuilder auditReportBuilder = new IntegrationObjectAuditReportBuilder();

	@Mock(lenient = true)
	private RendererService rendererService;
	@Mock(lenient = true)
	private UserService userService;

	@Test
	public void testFilterReportView()
	{
		final Map<String, Object> payload = new HashMap<>();
		final Date timeStamp0 = new Date(10000);
		final Date timeStamp1 = new Date(13000);
		Date timeStamp2 = new Date(16000);
		final Date timeStamp3 = new Date(19000);

		final String testUser0 = "testUser";
		final String testUser1 = "testUser";
		String testUser2 = "testUser";
		final String testUser3 = "testUser";

		final ReportView reportViewTest0 = ReportView.builder(payload, timeStamp0, testUser0).build();
		final ReportView reportViewTest1 = ReportView.builder(payload, timeStamp1, testUser1).build();
		ReportView reportViewTest2 = ReportView.builder(payload, timeStamp2, testUser2).build();
		final ReportView reportViewTest3 = ReportView.builder(payload, timeStamp3, testUser3).build();

		Stream<ReportView> reportStream = Stream.of(reportViewTest0, reportViewTest1, reportViewTest2, reportViewTest3);

		Stream<ReportView> testRes = auditReportBuilder.filterReportView(reportStream);
		assertThat(testRes.toArray()).hasSize(1);

		timeStamp2 = new Date(15999);
		reportViewTest2 = ReportView.builder(payload, timeStamp2, testUser2).build();
		reportStream = Stream.of(reportViewTest0, reportViewTest1, reportViewTest2, reportViewTest3);
		testRes = auditReportBuilder.filterReportView(reportStream);
		assertThat(testRes.toArray()).hasSize(2);

		timeStamp2 = new Date(16000);
		testUser2 = "anotherTestUser";
		reportViewTest2 = ReportView.builder(payload, timeStamp2, testUser2).build();
		reportStream = Stream.of(reportViewTest0, reportViewTest1, reportViewTest2, reportViewTest3);
		testRes = auditReportBuilder.filterReportView(reportStream);
		assertThat(testRes.toArray()).hasSize(3);
	}

	@Test
	public void testTraversePayload()
	{
		final Map<String, Object> composedTypeConfigMap = new HashMap<>(Map.of("ComposedType_", "test1"));
		final Map<String, Object> ComposedTypeOfItemMap = new HashMap<>(Map.of("ComposedTypeOfItem", composedTypeConfigMap));
		final Map<String, Object> itemTypeConfigMap = new HashMap<>(Map.of("returnIntegrationObjectItem", ComposedTypeOfItemMap));

		auditReportBuilder.traversePayload(itemTypeConfigMap);

		assertThat(itemTypeConfigMap).hasSize(1).containsValue("test1");

		final Map<String, Object> anotherMap = new HashMap<>(Map.of("returnIntegrationObjectItem", "test2"));

		auditReportBuilder.traversePayload(anotherMap);
		assertThat(anotherMap).hasSize(1);

		anotherMap.put("returnIntegrationObjectItem", Map.of("key", "value"));
		auditReportBuilder.traversePayload(anotherMap);
		assertThat(anotherMap).isEmpty();
	}

	@Test
	public void testPopulateReportGenerationContext()
	{
		final IntegrationObjectModel testIntegrationObject = mock(IntegrationObjectModel.class);
		auditReportBuilder.setSelectedModel(testIntegrationObject);
		lenient().when(testIntegrationObject.getCode()).thenReturn("testIoCodes");

		final UserModel testUser = mock(UserModel.class);
		lenient().when(userService.getCurrentUser()).thenReturn(testUser);

		final AuditReportTemplateModel testAuditReportTemplate = mock(AuditReportTemplateModel.class);
		lenient().when(auditReportBuilder.getAuditReportTemplate()).thenReturn(testAuditReportTemplate);

		auditReportBuilder.setConfigName("IOReport");

		final Map<String, Object> testResMap = auditReportBuilder.populateReportGenerationContext();
		assertThat(testResMap)
				.containsValue(testIntegrationObject)
				.containsValue("IOReport")
				.containsValue(testUser)
				.containsValue(testAuditReportTemplate);
	}

	@Test
	public void testPopulateReportGenerationContextWithNullTemplate()
	{
		final IntegrationObjectModel testIntegrationObject = mock(IntegrationObjectModel.class);
		auditReportBuilder.setSelectedModel(testIntegrationObject);
		lenient().when(testIntegrationObject.getCode()).thenReturn("testIoCodes");

		final UserModel testUser = mock(UserModel.class);
		lenient().when(userService.getCurrentUser()).thenReturn(testUser);

		lenient().when(auditReportBuilder.getAuditReportTemplate()).thenReturn(null);

		final Map<String, Object> testResMap = auditReportBuilder.populateReportGenerationContext();
		assertThat(testResMap).isNull();
	}

	@Test
	public void testEvaluateStrategiesToStreams()
	{
		final Stream<ReportView> reports = Stream.empty();
		final Map<String, Object> context = new HashMap<>();

		final AuditReportTemplateModel template = mock(AuditReportTemplateModel.class);
		context.put(AbstractTemplateViewConverterStrategy.CTX_TEMPLATE, template);

		final ReportConversionData textData = mock(ReportConversionData.class);
		final List<ReportConversionData> textDataCollection = Collections.singletonList(textData);

		final ReportViewConverterStrategy testStrategy1 = mock(DefaultReportViewConverterStrategy.class);
		final List<ReportViewConverterStrategy> reportViewConverterStrategies = new ArrayList<>();
		reportViewConverterStrategies.add(testStrategy1);

		auditReportBuilder.setReportViewConverterStrategies(reportViewConverterStrategies);
		lenient().when(testStrategy1.convert(reports, context)).thenReturn(textDataCollection);

		final Map<String, InputStream> resMap = auditReportBuilder.evaluateStrategiesToStreams(reports, context);
		assertThat(resMap).isEmpty();
	}

	@Test
	public void testEvaluateStrategiesToStreamsWithNullReturn()
	{
		final Stream<ReportView> reports = Stream.empty();
		final Map<String, Object> context = new HashMap<>();

		final AuditReportTemplateModel template = mock(AuditReportTemplateModel.class);
		context.put(AbstractTemplateViewConverterStrategy.CTX_TEMPLATE, template);

		final ReportViewConverterStrategy testStrategy1 = mock(DefaultReportViewConverterStrategy.class);
		final List<ReportViewConverterStrategy> reportViewConverterStrategies = new ArrayList<>();
		reportViewConverterStrategies.add(testStrategy1);

		auditReportBuilder.setReportViewConverterStrategies(reportViewConverterStrategies);
		lenient().when(testStrategy1.convert(reports, context)).thenReturn(null);

		final Map<String, InputStream> resMap = auditReportBuilder.evaluateStrategiesToStreams(reports, context);
		assertThat(resMap).isEmpty();
	}

	@Test
	public void testGetAuditReportTemplate()
	{
		final AuditReportTemplateModel testAuditReportTemplateModel = mock(AuditReportTemplateModel.class);
		lenient().when(rendererService.getRendererTemplateForCode(anyString())).thenReturn(testAuditReportTemplateModel);

		final AuditReportTemplateModel actualAuditReportTemplateModel = auditReportBuilder.getAuditReportTemplate();

		assertThat(actualAuditReportTemplateModel).isEqualTo(testAuditReportTemplateModel);
	}

	@Test
	public void testGetAuditReportTemplateWithNullReturn()
	{
		lenient().when(rendererService.getRendererTemplateForCode(anyString())).thenReturn(null);

		final AuditReportTemplateModel actualAuditReportTemplateModel = auditReportBuilder.getAuditReportTemplate();
		assertThat(actualAuditReportTemplateModel).isNull();
	}

	@Test
	public void testGetDownloadFileName()
	{
		final IntegrationObjectModel itemModel = mock(IntegrationObjectModel.class);
		auditReportBuilder.setSelectedModel(itemModel);
		lenient().when(itemModel.getCode()).thenReturn("testIntegrationObjectModel");

		assertThat(auditReportBuilder.getDownloadFileName()).isEqualTo("testIntegrationObjectModel");
	}
}
