/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters;


import static org.junit.Assert.assertEquals;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

public class ConvertorsTest  extends ServicelayerTest
{
    @Resource
    private BaseSiteService baseSiteService;
    @Resource
    private Converter<TicketData, ServiceRequestData> c4cTicketConverter;
    @Resource
    private Converter<ServiceRequestData, TicketData> c4cyticketConverter;
    @Resource
    private Converter<TicketData, Note> updateMessageConverter;
    @Resource
    private Map<String, StatusData> c4cStatusMapping;

    @Before
    public void setUp() throws ImpExException
    {
        importCsv("/customerticketingfacades/test/testCustomerTicketing.impex", "UTF-8");

        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
        baseSiteService.setCurrentBaseSite(baseSite, true);
    }

    @Test
    public void ecpTicketConvertorTest()
    {
        TicketData ticketData = new TicketData();
        ticketData.setSubject("Some subject");
        ticketData.setMessage("MEssge");
        ticketData.setCartId("cid");

        ServiceRequestData converted = c4cTicketConverter.convert(ticketData);

        assertEquals(converted.getName(), ticketData.getSubject());
        assertEquals(converted.getBuyerPartyID(), ticketData.getCustomerId());
        assertEquals(converted.getRelatedTransactions().get(0).getID(), ticketData.getCartId());
    }

    @Test
    public void c4cTicketConvertorTest()
    {
        String subject = "Some text qwe";
        String cid = "Some cid";
        ServiceRequestData data = new ServiceRequestData();
        data.setBuyerPartyID(cid);
        data.setName(subject);
        Note createRequestSubObject = new Note();
        createRequestSubObject.setText("");
        createRequestSubObject.setTypeCode("10004");
        data.setNotes(Arrays.asList(createRequestSubObject));

	TicketData converted = c4cyticketConverter.convert(data);

        assertEquals(converted.getSubject(), subject);
        assertEquals(converted.getCustomerId(), cid);
    }

    @Test
    public void ticketMessageUpdateConversionTest()
    {
        TicketData ticketData = new TicketData();
        ticketData.setMessage("MessAGEeeeeeeeeee");
        ticketData.setId("object Id 155515454");

        Note result = updateMessageConverter.convert(ticketData);

        assertEquals(result.getText(), ticketData.getMessage());
    }

    @Test
    public void ticketStatusUpdateConversionTest()
    {
        TicketData ticketData = new TicketData();
        StatusData statusData = new StatusData();
        statusData.setId("OPEN");
        ticketData.setStatus(statusData);

        ServiceRequestData result = c4cTicketConverter.convert(ticketData);

	assertEquals(c4cStatusMapping.get(result.getStatusCode()).getId(), statusData.getId());
    }

    @Test
    public void b2bTicketConversionTest()
    {
        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testb2bSite");
        baseSiteService.setCurrentBaseSite(baseSite, true);

        String subject = "Some text qwe";
        String cid = "Some cid";
        ServiceRequestData data = new ServiceRequestData();
        data.setBuyerMainContactPartyID(cid);
        data.setName(subject);
        Note createRequestSubObject = new Note();
        createRequestSubObject.setText("");
        createRequestSubObject.setTypeCode("10004");
        data.setNotes(Arrays.asList(createRequestSubObject));

	TicketData converted = c4cyticketConverter.convert(data);

        assertEquals(subject, converted.getSubject());
        assertEquals(cid, converted.getCustomerId());
    }
}
