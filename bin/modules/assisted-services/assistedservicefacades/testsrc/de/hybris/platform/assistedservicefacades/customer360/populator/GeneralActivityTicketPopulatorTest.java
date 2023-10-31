/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicefacades.customer360.populators.GeneralActivityTicketPopulator;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class GeneralActivityTicketPopulatorTest
{
    @InjectMocks
    private GeneralActivityTicketPopulator populator = new GeneralActivityTicketPopulator();

    @Mock
    private BaseSiteService baseSiteService;

    @Test
    public void populateTest()
    {
        final String headline = "HEADLINE";
        final String ticketId = "123";
        final CsTicketState aNew = CsTicketState.NEW;
        final Date created = new Date();
        final Date update = new Date();

        final CsTicketModel csTicketModel = new CsTicketModel();
        csTicketModel.setTicketID(ticketId);
        csTicketModel.setState(aNew);
        csTicketModel.setHeadline(headline);
        csTicketModel.setCategory(CsTicketCategory.COMPLAINT);
        csTicketModel.setCreationtime(created);
        csTicketModel.setModifiedtime(update);

        final GeneralActivityData generalActivityData = new GeneralActivityData();
        Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
        populator.populate(csTicketModel, generalActivityData);

        Assert.assertEquals(ticketId, generalActivityData.getId());
        Assert.assertEquals(aNew.getCode(), generalActivityData.getStatus());
        Assert.assertEquals(created, generalActivityData.getCreated());
        Assert.assertEquals(update, generalActivityData.getUpdated());
        Assert.assertEquals(headline, generalActivityData.getDescription());
    }
}

