/*
 *
 *  * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.builders;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractAuditReportBuilderUnitTest
{
	@Spy
	private AbstractAuditReportBuilder auditReportBuilder;

	@Test
	public void setReportViewConverterStrategiesHandlesNull()
	{
		auditReportBuilder.setReportViewConverterStrategies(null);
		assertEquals("Set an empty list if it's set null", Collections.emptyList(),
				auditReportBuilder.getReportViewConverterStrategies());
	}

	@Test
	public void setReportViewConverterStrategiesDoesNotStoreReference()
	{
		List<ReportViewConverterStrategy> strategyList = new ArrayList<>(List.of(mock(ReportViewConverterStrategy.class)));
		auditReportBuilder.setReportViewConverterStrategies(strategyList);
		strategyList.add(mock(ReportViewConverterStrategy.class));
		assertNotEquals("Two lists contain different elements.", strategyList,
				auditReportBuilder.getReportViewConverterStrategies());
		assertNotSame("The references are different.", strategyList, auditReportBuilder.getReportViewConverterStrategies());
	}

	@Test
	public void getReportViewConverterStrategiesDoesNotLeakReference()
	{
		auditReportBuilder.setReportViewConverterStrategies(new ArrayList<>(List.of(mock(ReportViewConverterStrategy.class))));
		// Getter returns an unmodifiable empty list
		List<ReportViewConverterStrategy> rolesList = auditReportBuilder.getReportViewConverterStrategies();
		assertThatThrownBy(() -> rolesList.add(mock(ReportViewConverterStrategy.class)))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
