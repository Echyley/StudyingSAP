/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.hybris.sapbillinginvoicefacades.document.data.ExternalSystemBillingDocumentData;

import de.hybris.platform.commercefacades.invoice.data.SAPInvoiceData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.enums.ExternalSystemId;

@RunWith(MockitoJUnitRunner.class)
public class ExternalBillingToInvoiceDataPopulatorTest {

  private static final String INVOICEID = "1234";
private static final BigDecimal PRICE = BigDecimal.valueOf(100);
  @Mock
  private ExternalSystemBillingDocumentData source;
  
  @Mock
  private Date date;

  @InjectMocks
  private ExternalBillingToInvoiceDataPopulator populator;
  

  @Test
  public void testPopulate() {
	
    SAPInvoiceData target = new SAPInvoiceData();
    final PriceData priceData = new PriceData();
	priceData.setValue(PRICE);

    when(source.getBillingDocumentId()).thenReturn(INVOICEID);
    when(source.getBillingInvoiceDate()).thenReturn(date);
    when(source.getTotalPrice()).thenReturn(priceData);
    when(source.getNetAmount()).thenReturn(priceData);

    populator.populate(source, target);

    assertEquals(INVOICEID, target.getInvoiceId());
    assertNotNull(target.getInvoiceDate());
    assertEquals(ExternalSystemId.S4SALES.getCode(), target.getExternalSystemId());
    assertEquals(BigDecimal.valueOf(100), target.getTotalAmount().getValue());
    assertEquals(BigDecimal.valueOf(100), target.getNetAmount().getValue());
  }
}
