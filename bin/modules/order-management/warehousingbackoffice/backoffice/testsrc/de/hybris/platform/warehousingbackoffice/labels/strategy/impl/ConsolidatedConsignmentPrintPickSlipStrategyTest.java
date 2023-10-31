package de.hybris.platform.warehousingbackoffice.labels.strategy.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsolidatedConsignmentPrintPickSlipStrategyTest
{
	@InjectMocks
	private ConsolidatedConsignmentPrintPickSlipStrategy consignmentPrintPickSlipStrategy;

	@Mock
	private PrintMediaService printMediaService;

	@Mock
	private ConsignmentModel consignmentModel;

	@Test()
	public void shouldPrintDocument()
	{
		when(printMediaService.generatePopupScriptForMedia(anyObject(), eq("900"), eq("800"), anyString())).thenReturn("");
		try {
			consignmentPrintPickSlipStrategy.printDocument(Arrays.asList(consignmentModel));
		} catch (Exception e) {
			// NPE is expected because Client.evalJavaScript will throw that in UT, and we cannot mock it as it is static method
			assertThat(e.getClass()).isEqualTo(NullPointerException.class);
		}
		verify(printMediaService).getMediaForTemplate(eq("ConsolidatedPickLabelDocumentTemplate"), any(ConsignmentProcessModel.class));
		verify(printMediaService).generatePopupScriptForMedia(any(), eq("900"), eq("800"), any());
	}
}
