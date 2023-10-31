/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicefacades.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer.converters.populator.AutoSuggestionCustomerPopulator;
import de.hybris.platform.assistedservicefacades.user.data.AutoSuggestionCustomerData;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AutoSuggestionCustomerPopulatorTest {
    @InjectMocks
    private AutoSuggestionCustomerPopulator autoSuggestionCustomerPopulator;

    @Mock
    private AssistedServiceService assistedServiceService;

    @Test
    public void undecoratedUidNullTest()
    {
        final CustomerModel customerModel = new CustomerModel();
        customerModel.setUid("testUid@sap.com");
        final AutoSuggestionCustomerData autoSuggestionCustomerData = new AutoSuggestionCustomerData();
        autoSuggestionCustomerPopulator.populate(customerModel, autoSuggestionCustomerData);
        Assert.assertEquals("testUid@sap.com", autoSuggestionCustomerData.getEmail());
    }

    @Test
    public void undecoratedUidNotNullTest()
    {
        final CustomerModel customerModel = new CustomerModel();
        customerModel.setUid("testUid@sap.com");
        customerModel.setUndecoratedUid("testUndecoratedUid@sap.com");
        final AutoSuggestionCustomerData autoSuggestionCustomerData = new AutoSuggestionCustomerData();
        autoSuggestionCustomerPopulator.populate(customerModel, autoSuggestionCustomerData);
        Assert.assertEquals("testUndecoratedUid@sap.com", autoSuggestionCustomerData.getEmail());
    }
}
