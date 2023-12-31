/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import org.junit.Before;
import org.junit.Test;
import javax.annotation.Resource;
import static org.fest.assertions.Assertions.assertThat;

@IntegrationTest
public class DefaultB2CSapCustomerAccountServiceAspectTest extends ServicelayerTest {

  private static final String TEST_NEW_PASS = "newPass";
  private static final String REG_EXPRESSION = "[0-9]{1,10}";

  @Resource(name = "customerAccountService")
  private CustomerAccountService defaultCustomerAccountService;

  private CustomerModel customer_a;
  private CustomerModel customer_b;

  @Before
  public void setUp() {

  	customer_a = new CustomerModel();
  	customer_a.setUid("customerUid_a");

  	customer_b = new CustomerModel();
  	customer_b.setUid("customerUid_b");
  }

  @Test
  public void interceptRegister() throws DuplicateUidException {

    defaultCustomerAccountService.register(customer_a, TEST_NEW_PASS);
    // Assert that the customer Id has been generated by the sapCustomerIdGenerator
    assertThat(customer_a.getCustomerID().matches(REG_EXPRESSION)).isTrue();

  }

  @Test
  public void interceptRegisterGuestForAnonymousCheckout() throws DuplicateUidException {

    defaultCustomerAccountService.registerGuestForAnonymousCheckout(customer_b, TEST_NEW_PASS);
    // Assert that the customer Id has been generated by the sapCustomerIdGenerator
    assertThat(customer_b.getCustomerID().matches(REG_EXPRESSION)).isTrue();

  }

}