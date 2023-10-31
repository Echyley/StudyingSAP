/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.*;
import de.hybris.platform.sap.saprevenuecloudorder.populators.subscription.DefaultSubscriptionSummaryPopulator;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSubscriptionSummaryPopulatorTest {

    @Mock
    private CMSSiteService cmsSiteService;
    @Mock
    private SapRevenueCloudProductService sapRevenueCloudProductService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private Map<String, SubscriptionStatus> subscriptionStatusMap;
    @Mock
    private Map<String, String> billingFrequencyMap;
    @Mock
    private ProductModel productModel;
    @Mock
    private ProductData productData;
    @Mock
    private BillingFrequencyModel billingFrequencyModel;
    @Mock
    private SubscriptionTermModel subscriptionTermModel;

    private static Subscription subscription = new Subscription();
    private static SubscriptionData subscriptionData = new SubscriptionData();
    private static RenewalTerm renewalTerm = new RenewalTerm();
    private static Payment payment = new Payment();
    private static Product product = new Product();
    private static CancellationPolicy cancellationPolicy = new CancellationPolicy();
    private static Metadata metadata = new Metadata();
    private static BusinessPartner customer = new BusinessPartner();
    private static Snapshot snapshot = new Snapshot();
    private static Item item = new Item();
    private static RatePlan ratePlan = new RatePlan();
    private static Pricing pricing = new Pricing();
    private static PricingParameter pricingParameter = new PricingParameter();
    private static PriceData priceData = new PriceData();
    private static CatalogVersionModel currentCatalog = new CatalogVersionModel();
    private static ExternalObjectReference externalObjectReference = new ExternalObjectReference();
    private static SubscriptionPricePlanModel pricePlanModel = new SubscriptionPricePlanModel();
    private static BillingPlanModel billingPlanModel = new BillingPlanModel();

    private static List<Snapshot> snapshotList = new ArrayList<>();
    private static List<PricingParameter> pricingParameterList = new ArrayList<>();
    private static List<Item> itemList = new ArrayList<>();
    private static List<ExternalObjectReference> externalObjectReferenceList = new ArrayList<>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Date startDate;
    private static Date endDate;
    private static Calendar calendar = Calendar.getInstance();

    private static final String STATUS ="pending";
    private static final String DUMMY_CODE ="dummy_code";
    private static final String DUMMY_NAME ="dummy_name";
    private static final String DUMMY_VAL ="dummy_val";
    private static final String PROD_NAME ="prod_name_sample";
    private static final String RATE_PLAN_ID ="rp_id";
    private static final String BILLING_PLAN_ID ="bp_id";
    private static final String DUMMY_URL ="dummy_url";
    private static final String CONTRACT_FREQUENCY ="contractFrequency";
    private static final String EXTERNAL_ID ="ext_1234";
    private static final String EXTERNAL_ID_TYPECODE ="ext_tp_1234";
    private static final String EXTERNAL_SYSTEM_ID ="ext_sys_id_1234";
    private static final String BUSS_PARTNER_ID ="partner321";
    private static final String SUBSCRIPTION_ID = "sub123";
    private static final int DOC_NUMBER = 123456;

    @InjectMocks
    DefaultSubscriptionSummaryPopulator<Subscription,SubscriptionData> defaultSubscriptionSummaryPopulator;

    @Before
    public void setUp() {

        try {
            startDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            fail("Error : Failed to parse date ");
        }


        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        endDate = calendar.getTime();
        productModel.setSubscriptionTerm(subscriptionTermModel);
        pricePlanModel.setProduct(productModel);
        cancellationPolicy.setWithdrawalPeriodEndDate(endDate);
        metadata.setVersion(2);
        customer.setId(BUSS_PARTNER_ID);
        ratePlan.setId(RATE_PLAN_ID);
        pricingParameter.setCode(DUMMY_CODE);
        pricingParameter.setValue(DUMMY_VAL);
        pricing.setPricingDate(startDate);
        pricingParameterList.add(pricingParameter);
        pricing.setPricingParameters(pricingParameterList);

        billingPlanModel.setId(BILLING_PLAN_ID);
        product.setCode(DUMMY_CODE);
        product.setId(EXTERNAL_ID);
        product.setName(DUMMY_NAME);
        item.setPricing(pricing);
        item.setRatePlan(ratePlan);
        item.setProduct(product);
        itemList.add(item);
        snapshot.setItems(itemList);
        snapshotList.add(snapshot);
        externalObjectReference.setExternalId(EXTERNAL_ID);
        externalObjectReference.setExternalIdTypeCode(EXTERNAL_ID_TYPECODE);
        externalObjectReference.setExternalSystemId(EXTERNAL_SYSTEM_ID);
        externalObjectReferenceList.add(externalObjectReference);

        subscription.setRenewalTerm(renewalTerm);
        subscription.setPayment(payment);
        subscription.setCancellationPolicy(cancellationPolicy);
        subscription.setWithdrawnAt(startDate);
        subscription.setStatus(STATUS);
        subscription.setValidFrom(startDate);
        subscription.setValidUntil(endDate);
        subscription.setSubscriptionId(SUBSCRIPTION_ID);
        subscription.setDocumentNumber(DOC_NUMBER);
        subscription.setMetadata(metadata);
        subscription.setCustomer(customer);
        subscription.setSnapshots(snapshotList);
        subscription.setExternalObjectReferences(externalObjectReferenceList);

        subscriptionData.setProductCode(DUMMY_CODE);
        subscriptionData.setName(PROD_NAME);
        subscriptionData.setRatePlanId(RATE_PLAN_ID);
        subscriptionData.setContractFrequency(CONTRACT_FREQUENCY);

        //mock
        Mockito.lenient().when(cmsSiteService.getCurrentCatalogVersion()).thenReturn(currentCatalog);
        Mockito.lenient().when(productModel.getName()).thenReturn(PROD_NAME);
        Mockito.lenient().when(productData.getUrl()).thenReturn(DUMMY_URL);
        Mockito.lenient().when(productData.getPrice()).thenReturn(priceData);
        Mockito.lenient().when(productModel.getSubscriptionTerm()).thenReturn(subscriptionTermModel);
        Mockito.lenient().when(subscriptionTermModel.getBillingPlan()).thenReturn(billingPlanModel);
        Mockito.lenient().when(billingFrequencyMap.getOrDefault(any(), any())).thenReturn(CONTRACT_FREQUENCY);
        try {
        	Mockito.lenient().when(subscriptionService.getBillingFrequency(any(ProductModel.class))).thenReturn(billingFrequencyModel);
        } catch (SubscriptionServiceException e) {

            fail("Error: Failed to mock SubscriptionService ");
        }
        Mockito.lenient().when(billingFrequencyModel.getNameInCart()).thenReturn(PROD_NAME);
        Mockito.lenient().when(sapRevenueCloudProductService.getSubscriptionPricesWithEffectiveDate(any(String.class),any(CatalogVersionModel.class),any(Date.class))).thenReturn(pricePlanModel);

    }
    @Test
    public void test() {
        Mockito.lenient().when(subscriptionStatusMap.getOrDefault(any(), any())).thenReturn(SubscriptionStatus.ACTIVE);
        //execute
        defaultSubscriptionSummaryPopulator.populate(subscription,subscriptionData);

        //verify
        Assert.assertEquals(DUMMY_CODE,subscriptionData.getProductCode());
        Assert.assertEquals(PROD_NAME,subscriptionData.getName());
        Assert.assertEquals(RATE_PLAN_ID,subscriptionData.getRatePlanId());
        Assert.assertEquals(CONTRACT_FREQUENCY,subscriptionData.getContractFrequency());
        Assert.assertEquals(BUSS_PARTNER_ID,subscriptionData.getCustomerId());
        Assert.assertEquals(String.valueOf(DOC_NUMBER),subscriptionData.getDocumentNumber());
    }

}
