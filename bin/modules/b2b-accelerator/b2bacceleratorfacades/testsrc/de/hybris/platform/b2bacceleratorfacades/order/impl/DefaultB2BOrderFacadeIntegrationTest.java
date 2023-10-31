/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.order.B2BOrderFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

@IntegrationTest
public class DefaultB2BOrderFacadeIntegrationTest extends BaseCommerceBaseTest
{
    private static final String UNIT_VIEWER_CUSTOMER2_UID = "customer2";

    private static final String UNIT_VIEWER_CUSTOMER_UID = "customer1";

    private static final String REGULAR_CUSTOMER3_UID = "customer3";

    private static final String ORDER1_CODE = "order1";

    private static final String ORDER2_CODE = "order2";

    private static final String ORDER3_CODE = "order3";

    private static final String ORDER4_CODE = "order4";
    private static final String ORDER5_CODE = "order5";

    private static final String ROOT_ORG_UNIT_UID = "rootUnit";

    private static final String MIDDLE_ORG_UNIT_UID = "middleUnitWest";

    private static final String CHILD_ORG_UNIT_UID = "childUnit";
    private static final String TEST_BASESITE_UID = "testSite";

    @Resource
    private B2BOrderFacade b2bOrderFacade;
    @Resource
    private BaseSiteService baseSiteService;
    @Resource
    private UserService userService;

    private UserModel user;

    @Before
    public void setUp() throws ImpExException
    {
        userService.setCurrentUser(userService.getAdminUser());
        importCsv("/b2bacceleratorfacades/test/testDefaultB2BOrderFacade.impex", "utf-8");

        BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
        baseSiteService.setCurrentBaseSite(baseSite, false);

        user = userService.getUserForUID(UNIT_VIEWER_CUSTOMER_UID);
        userService.setCurrentUser(user);
    }

    @Test
    public void shouldReturnOrderDetailsForCodeWithPermission()
    {
        final String orderCode = ORDER1_CODE;
        OrderData orderDetails = b2bOrderFacade.getBranchOrderForCode(orderCode);
        Assert.assertNotNull(orderDetails);
        Assert.assertEquals(orderCode, orderDetails.getCode());
        Assert.assertEquals(userService.getCurrentUser().getUid(), orderDetails.getUser().getUid());
        Assert.assertEquals(ROOT_ORG_UNIT_UID,orderDetails.getOrgUnit().getUid());
    }

    @Test
    public void shouldReturnUnitOrderDetailsForCodeWithPermission()
    {
        final String orderCode = ORDER2_CODE;
        OrderData orderDetails = b2bOrderFacade.getBranchOrderForCode(orderCode);
        Assert.assertNotNull(orderDetails);
        Assert.assertEquals(orderCode, orderDetails.getCode());
        Assert.assertEquals(UNIT_VIEWER_CUSTOMER2_UID, orderDetails.getUser().getUid());
        Assert.assertEquals(MIDDLE_ORG_UNIT_UID,orderDetails.getOrgUnit().getUid());
        OrderData orderDetails3 = b2bOrderFacade.getBranchOrderForCode(ORDER3_CODE);
        Assert.assertNotNull(orderDetails);
        Assert.assertEquals(orderCode, orderDetails.getCode());
        Assert.assertEquals(REGULAR_CUSTOMER3_UID, orderDetails3.getUser().getUid());
        Assert.assertEquals(CHILD_ORG_UNIT_UID,orderDetails3.getOrgUnit().getUid());
    }

    @Test(expected = UnknownIdentifierException.class)
    public void shouldNotReturnUnitOrderDetailsForCodeNull()
    {
        b2bOrderFacade.getBranchOrderForCode(ORDER5_CODE);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void shouldNotReturnOrderDetailsForCodeAboveTheUnit()
    {
        user = userService.getUserForUID(UNIT_VIEWER_CUSTOMER2_UID);
        userService.setCurrentUser(user);
        b2bOrderFacade.getBranchOrderForCode(ORDER1_CODE);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void shouldNotReturnUnitOrderDetailsForCodeOutsideTheBranch()
    {
        user = userService.getUserForUID(UNIT_VIEWER_CUSTOMER2_UID);
        userService.setCurrentUser(user);
        b2bOrderFacade.getBranchOrderForCode(ORDER4_CODE);
    }
}
