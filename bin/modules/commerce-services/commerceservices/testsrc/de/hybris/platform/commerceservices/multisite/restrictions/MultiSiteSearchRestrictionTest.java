package de.hybris.platform.commerceservices.multisite.restrictions;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.auditreport.model.AuditReportDataModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import javax.annotation.Resource;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.SITE_SESSION_ATTRIBUTE_NAME;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;


@IntegrationTest
public class MultiSiteSearchRestrictionTest extends ServicelayerTransactionalTest
{

	private static final String TEST_SITE = "test-site";
	private static final String TEST_SITE_A = "test-site-a";
	private static final String TEST_SITE_B = "test-site-b";
	private static final String FOO_UID = "foo@bar.com";
	private static final String FOOALL_UID = "fooall@bar.com";

	private static final Matcher<BaseSiteModel> SITE_MATCHER = hasProperty(BaseSiteModel.UID, equalTo(TEST_SITE));
	private static final Matcher<BaseSiteModel> SITE_A_MATCHER = hasProperty(BaseSiteModel.UID, equalTo(TEST_SITE_A));
	private static final Matcher<BaseSiteModel> SITE_B_MATCHER = hasProperty(BaseSiteModel.UID, equalTo(TEST_SITE_B));

	private static final Matcher<BaseStoreModel> STORE_MATCHER = hasProperty(BaseStoreModel.UID, equalTo("test-store"));
	private static final Matcher<BaseStoreModel> STORE_A_MATCHER = hasProperty(BaseStoreModel.UID, equalTo("test-store-a"));
	private static final Matcher<BaseStoreModel> STORE_B_MATCHER = hasProperty(BaseSiteModel.UID, equalTo("test-store-b"));
	private static final Matcher<BaseStoreModel> STORE_A_B_MATCHER = hasProperty(BaseSiteModel.UID, equalTo("test-store-a+b"));

	private static final Matcher<CustomerModel> CUSTOMER_FOO_MATCHER = hasProperty(CustomerModel.UID, equalTo(FOO_UID));
	private static final Matcher<CustomerModel> CUSTOMER_FOO_A_MATCHER = hasProperty(CustomerModel.UID,
			equalTo(format("%s|%s", FOO_UID, TEST_SITE_A)));
	private static final Matcher<CustomerModel> CUSTOMER_FOO_B_MATCHER = hasProperty(CustomerModel.UID,
			equalTo(format("%s|%s", FOO_UID, TEST_SITE_B)));
	private static final Matcher<CustomerModel> CUSTOMER_FOOALL_MATCHER = hasProperty(CustomerModel.UID, equalTo(FOOALL_UID));


	private static final Matcher<SitePreferenceModel> SITE_PREFERENCE_FOO_A_MATCHER = hasProperty(SitePreferenceModel.SITE,
			hasProperty(BaseSiteModel.UID, equalTo(TEST_SITE_A)));
	private static final Matcher<SitePreferenceModel> SITE_PREFERENCE_FOO_B_MATCHER = hasProperty(SitePreferenceModel.SITE,
			hasProperty(BaseSiteModel.UID, equalTo(TEST_SITE_B)));

	private static final Matcher<CartModel> CART_ANON_A_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-anon-a"));
	private static final Matcher<CartModel> CART_FOO_A_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-foo-a"));
	private static final Matcher<CartModel> CART_ANON_B_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-anon-b"));
	private static final Matcher<CartModel> CART_FOO_B_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-foo-b"));
	private static final Matcher<CartModel> CART_ANON_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-anon"));
	private static final Matcher<CartModel> CART_FOO_MATCHER = hasProperty(CartModel.CODE, equalTo("test-cart-foo"));

	private static final Matcher<ConsentTemplateModel> CONSENT_TEMPLATE_SITE_MATCHER = hasProperty(ConsentTemplateModel.ID,
			equalTo("MARKETING_NEWSLETTER_SITE"));
	private static final Matcher<ConsentTemplateModel> CONSENT_TEMPLATE_SITE_A_MATCHER = hasProperty(ConsentTemplateModel.ID,
			equalTo("MARKETING_NEWSLETTER_SITE_A"));
	private static final Matcher<ConsentTemplateModel> CONSENT_TEMPLATE_SITE_B_MATCHER = hasProperty(ConsentTemplateModel.ID,
			equalTo("MARKETING_NEWSLETTER_SITE_B"));

	private static final Matcher<ConsentModel> CONSENT_FOOALL_MATCHER = hasProperty(ConsentModel.CODE, equalTo("00000001"));
	private static final Matcher<ConsentModel> CONSENT_FOO_A_MATCHER = hasProperty(ConsentModel.CODE, equalTo("00000002"));
	private static final Matcher<ConsentModel> CONSENT_FOO_B_MATCHER = hasProperty(ConsentModel.CODE, equalTo("00000003"));

	private static final Matcher<ConsignmentModel> CONSIGNTMENT_SITE_MATCHER = hasProperty(ConsignmentModel.CODE,
			equalTo("test-consignment"));
	private static final Matcher<ConsignmentModel> CONSIGNTMENT_SITE_A_MATCHER = hasProperty(ConsignmentModel.CODE,
			equalTo("test-consignment-a"));
	private static final Matcher<ConsignmentModel> CONSIGNTMENT_SITE_B_MATCHER = hasProperty(ConsignmentModel.CODE,
			equalTo("test-consignment-b"));

	private static final Matcher<CustomerReviewModel> CUSTOMER_REVIEW_ANON_MATCHER = hasProperty(CustomerReviewModel.COMMENT,
			equalTo("anon-comment"));
	private static final Matcher<CustomerReviewModel> CUSTOMER_REVIEW_FOOALL_MATCHER = hasProperty(CustomerReviewModel.COMMENT,
			equalTo("fooall-comment"));
	private static final Matcher<CustomerReviewModel> CUSTOMER_REVIEW_FOO_A_MATCHER = hasProperty(CustomerReviewModel.COMMENT,
			equalTo("foo-a-comment"));
	private static final Matcher<CustomerReviewModel> CUSTOMER_REVIEW_FOO_B_MATCHER = hasProperty(CustomerReviewModel.COMMENT,
			equalTo("foo-b-comment"));

	private static final Matcher<AuditReportDataModel> AUDIT_REPORT_DATA_FOO_MATCHER = hasProperty(AuditReportDataModel.CODE,
			equalTo("PDR-foo@bar.com"));
	private static final Matcher<AuditReportDataModel> AUDIT_REPORT_DATA_FOOALL_MATCHER = hasProperty(AuditReportDataModel.CODE,
			equalTo("PDR-fooall@bar.com"));
	private static final Matcher<AuditReportDataModel> AUDIT_REPORT_DATA_FOO_A_MATCHER = hasProperty(AuditReportDataModel.CODE,
			equalTo("PDR-foo@bar.com|test-site-a"));
	private static final Matcher<AuditReportDataModel> AUDIT_REPORT_DATA_FOO_B_MATCHER = hasProperty(AuditReportDataModel.CODE,
			equalTo("PDR-foo@bar.com|test-site-b"));

	private static final Matcher<ReturnRequestModel> RETURN_REQUEST_SITE_A_MATCHER = hasProperty(ReturnRequestModel.CODE,
			equalTo("testReturnRequestA"));
	private static final Matcher<ReturnRequestModel> RETURN_REQUEST_SITE_B_MATCHER = hasProperty(ReturnRequestModel.CODE,
			equalTo("testReturnRequestB"));
	private static final Matcher<ReturnRequestModel> RETURN_REQUEST_UN_ISOLATED_C_MATCHER = hasProperty(ReturnRequestModel.CODE,
			equalTo("testReturnRequestC"));

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private SessionService sessionService;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	private EmployeeModel employee, multiSiteEmployee, multiSiteEmployeeA, multiSiteEmployeeAB;

	private CustomerModel fooCustomer, fooCustomerA, fooCustomerB, fooallCustomer;

	private BaseSiteModel testSiteA, testSiteB;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		importCsv("/servicelayer/test/testDeliveryMode.csv", "utf-8");

		importData("/impex/essentialdata_multisite.impex", StandardCharsets.UTF_8.name());
		importData("/test/multisite-data.impex", StandardCharsets.UTF_8.name());

		employee = userService.getUserForUID("employee", EmployeeModel.class);
		multiSiteEmployee = userService.getUserForUID("multisite-employee", EmployeeModel.class);
		multiSiteEmployeeA = userService.getUserForUID("multisite-employee-a", EmployeeModel.class);
		multiSiteEmployeeAB = userService.getUserForUID("multisite-employee-a+b", EmployeeModel.class);
		fooCustomer = userService.getUserForUID(FOO_UID, CustomerModel.class);
		fooCustomerA = userService.getUserForUID(format("%s|%s", FOO_UID, TEST_SITE_A), CustomerModel.class);
		fooCustomerB = userService.getUserForUID(format("%s|%s", FOO_UID, TEST_SITE_B), CustomerModel.class);
		fooallCustomer = userService.getUserForUID(FOOALL_UID, CustomerModel.class);
		testSiteA = baseSiteService.getBaseSiteForUID(TEST_SITE_A);
		testSiteB = baseSiteService.getBaseSiteForUID(TEST_SITE_B);
	}

	protected <T> SearchResult<T> getAllResults(final String typeCode)
	{
		return flexibleSearchService.search(format("SELECT {%s} FROM {%s}", ItemModel.PK, typeCode));
	}

	protected <T> void searchItemsAndMatch(final String typeCode, final Class<T> modelClass,
			final Collection<Matcher<? super T>> itemMatchers)
	{
		final SearchResult<T> searchResult = getAllResults(typeCode);
		final List<T> result = searchResult.getResult();
		final String reason = format("'%s' item type", typeCode);
		assertThat(reason, result, everyItem(instanceOf(modelClass)));
		assertThat(reason, result, hasSize(itemMatchers.size()));
		assertThat(reason, result, containsInAnyOrder(itemMatchers));
	}

	protected void searchAllItemsAndMatch(final Collection<Matcher<? super BaseSiteModel>> siteItemMatchers,
			final Collection<Matcher<? super BaseStoreModel>> storeItemMatchers,
			final Collection<Matcher<? super CustomerModel>> customerItemMatchers,
			final Collection<Matcher<? super CartModel>> cartItemMatchers,
			final Collection<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers,
			final Collection<Matcher<? super ConsentModel>> consentItemMatchers,
			final Collection<Matcher<? super ConsignmentModel>> consignmentItemMatchers,
			final Collection<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers,
			final Collection<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers,
			final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers,
			final Collection<Matcher<? super ReturnRequestModel>> returnRequestMatchers)
	{
		searchItemsAndMatch(BaseSiteModel._TYPECODE, BaseSiteModel.class, siteItemMatchers);
		searchItemsAndMatch(BaseStoreModel._TYPECODE, BaseStoreModel.class, storeItemMatchers);
		searchItemsAndMatch(CustomerModel._TYPECODE, CustomerModel.class, customerItemMatchers);
		searchItemsAndMatch(CartModel._TYPECODE, CartModel.class, cartItemMatchers);
		searchItemsAndMatch(ConsentTemplateModel._TYPECODE, ConsentTemplateModel.class, consentTemplateItemMatchers);
		searchItemsAndMatch(ConsentModel._TYPECODE, ConsentModel.class, consentItemMatchers);
		searchItemsAndMatch(ConsignmentModel._TYPECODE, ConsignmentModel.class, consignmentItemMatchers);
		searchItemsAndMatch(CustomerReviewModel._TYPECODE, CustomerReviewModel.class, customerReviewItemMatchers);
		searchItemsAndMatch(SitePreferenceModel._TYPECODE, SitePreferenceModel.class, sitePreferenceMatchers);
		searchItemsAndMatch(AuditReportDataModel._TYPECODE, AuditReportDataModel.class, auditReportDataMatchers);
		searchItemsAndMatch(ReturnRequestModel._TYPECODE, ReturnRequestModel.class, returnRequestMatchers);

	}

	protected void searchAllItemsAndMatchWithUserAndSites(final UserModel user, final Set<?> sites,
			final Collection<Matcher<? super BaseSiteModel>> siteItemMatchers,
			final Collection<Matcher<? super BaseStoreModel>> storeItemMatchers,
			final Collection<Matcher<? super CustomerModel>> customerItemMatchers,
			final Collection<Matcher<? super CartModel>> cartItemMatchers,
			final Collection<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers,
			final Collection<Matcher<? super ConsentModel>> consentItemMatchers,
			final Collection<Matcher<? super ConsignmentModel>> consignmentItemMatchers,
			final Collection<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers,
			final Collection<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers,
			final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers,
			final Collection<Matcher<? super ReturnRequestModel>> returnRequestMatchers)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(user);

				sessionService.setAttribute(SITE_SESSION_ATTRIBUTE_NAME, sites);
				searchAllItemsAndMatch(siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
						consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
						sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
			}
		});
	}

	protected void searchAllItemsAndMatchWithUser(final UserModel user,
			final Collection<Matcher<? super BaseSiteModel>> siteItemMatchers,
			final Collection<Matcher<? super BaseStoreModel>> storeItemMatchers,
			final Collection<Matcher<? super CustomerModel>> customerItemMatchers,
			final Collection<Matcher<? super CartModel>> cartItemMatchers,
			final Collection<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers,
			final Collection<Matcher<? super ConsentModel>> consentItemMatchers,
			final Collection<Matcher<? super ConsignmentModel>> consignmentItemMatchers,
			final Collection<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers,
			final Collection<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers,
			final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers,
			final Collection<Matcher<? super ReturnRequestModel>> returnRequestMatchers)
	{
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				userService.setCurrentUser(user);

				searchAllItemsAndMatch(siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
						consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
						sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
			}
		});
	}

	@Test
	public void testSearchWithAnonymousShouldReturnAllItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_MATCHER, SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_MATCHER, STORE_A_MATCHER, STORE_B_MATCHER,
				STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(new AnonymousUserMatcher(userService),
				CUSTOMER_FOO_MATCHER, CUSTOMER_FOO_A_MATCHER, CUSTOMER_FOO_B_MATCHER, CUSTOMER_FOOALL_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER, CART_ANON_MATCHER, CART_FOO_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_MATCHER,
				CONSENT_TEMPLATE_SITE_A_MATCHER, CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOOALL_MATCHER, CONSENT_FOO_A_MATCHER,
				CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_MATCHER,
				CONSIGNTMENT_SITE_A_MATCHER, CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_ANON_MATCHER,
				CUSTOMER_REVIEW_FOOALL_MATCHER, CUSTOMER_REVIEW_FOO_A_MATCHER, CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_MATCHER,
				AUDIT_REPORT_DATA_FOOALL_MATCHER, AUDIT_REPORT_DATA_FOO_A_MATCHER, AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER, RETURN_REQUEST_UN_ISOLATED_C_MATCHER);
		searchAllItemsAndMatchWithUser(userService.getAnonymousUser(), siteItemMatchers, storeItemMatchers, customerItemMatchers,
				cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithEmployeeShouldReturnAllItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_MATCHER, SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_MATCHER, STORE_A_MATCHER, STORE_B_MATCHER,
				STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_MATCHER, CUSTOMER_FOO_A_MATCHER,
				CUSTOMER_FOO_B_MATCHER, CUSTOMER_FOOALL_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER, CART_ANON_MATCHER, CART_FOO_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_MATCHER,
				CONSENT_TEMPLATE_SITE_A_MATCHER, CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOOALL_MATCHER, CONSENT_FOO_A_MATCHER,
				CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_MATCHER,
				CONSIGNTMENT_SITE_A_MATCHER, CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_ANON_MATCHER,
				CUSTOMER_REVIEW_FOOALL_MATCHER, CUSTOMER_REVIEW_FOO_A_MATCHER, CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_MATCHER,
				AUDIT_REPORT_DATA_FOOALL_MATCHER, AUDIT_REPORT_DATA_FOO_A_MATCHER, AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER, RETURN_REQUEST_UN_ISOLATED_C_MATCHER);

		searchAllItemsAndMatchWithUser(employee, siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
				consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
				sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeAndNoSiteInSessionShouldReturnNoItem()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = emptyList();
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = emptyList();
		final List<Matcher<? super CustomerModel>> customerItemMatchers = emptyList();
		final List<Matcher<? super CartModel>> cartItemMatchers = emptyList();
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = emptyList();
		final List<Matcher<? super ConsentModel>> consentItemMatchers = emptyList();
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = emptyList();
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = emptyList();
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = emptyList();
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = emptyList();
		final Collection<Matcher<? super ReturnRequestModel>> returnRequestMatchers = emptyList();

		searchAllItemsAndMatchWithUserAndSites(multiSiteEmployee, singleton(0l), siteItemMatchers, storeItemMatchers,
				customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeAndSingleSiteInSessionShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER);

		searchAllItemsAndMatchWithUserAndSites(multiSiteEmployee, singleton(testSiteA), siteItemMatchers, storeItemMatchers,
				customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeAndAllSiteInSessionShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_B_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER, CUSTOMER_FOO_B_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER,
				CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER, CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER,
				CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER,
				CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER,
				AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER);

		searchAllItemsAndMatchWithUserAndSites(multiSiteEmployee, new HashSet<>(asList(testSiteA, testSiteB)), siteItemMatchers,
				storeItemMatchers, customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers,
				consignmentItemMatchers, customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers,
				returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeShouldReturnNoItem()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = emptyList();
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = emptyList();
		final List<Matcher<? super CustomerModel>> customerItemMatchers = emptyList();
		final List<Matcher<? super CartModel>> cartItemMatchers = emptyList();
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = emptyList();
		final List<Matcher<? super ConsentModel>> consentItemMatchers = emptyList();
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = emptyList();
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = emptyList();
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = emptyList();
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = emptyList();
		final Collection<Matcher<? super ReturnRequestModel>> returnRequestMatchers = emptyList();
		searchAllItemsAndMatchWithUser(multiSiteEmployee, siteItemMatchers, storeItemMatchers, customerItemMatchers,
				cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeAShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER);

		searchAllItemsAndMatchWithUser(multiSiteEmployeeA, siteItemMatchers, storeItemMatchers, customerItemMatchers,
				cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithMultiSiteEmployeeABShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_B_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER, CUSTOMER_FOO_B_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER,
				CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER, CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER,
				CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER,
				CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER,
				AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER);
		searchAllItemsAndMatchWithUser(multiSiteEmployeeAB, siteItemMatchers, storeItemMatchers, customerItemMatchers,
				cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooAllCustomerShouldReturnAllItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_MATCHER, SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_MATCHER, STORE_A_MATCHER, STORE_B_MATCHER,
				STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(new AnonymousUserMatcher(userService),
				CUSTOMER_FOO_MATCHER, CUSTOMER_FOO_A_MATCHER, CUSTOMER_FOO_B_MATCHER, CUSTOMER_FOOALL_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER, CART_ANON_MATCHER, CART_FOO_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_MATCHER,
				CONSENT_TEMPLATE_SITE_A_MATCHER, CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOOALL_MATCHER, CONSENT_FOO_A_MATCHER,
				CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_MATCHER,
				CONSIGNTMENT_SITE_A_MATCHER, CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_ANON_MATCHER,
				CUSTOMER_REVIEW_FOOALL_MATCHER, CUSTOMER_REVIEW_FOO_A_MATCHER, CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_MATCHER,
				AUDIT_REPORT_DATA_FOOALL_MATCHER, AUDIT_REPORT_DATA_FOO_A_MATCHER, AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER, RETURN_REQUEST_UN_ISOLATED_C_MATCHER);

		searchAllItemsAndMatchWithUser(fooallCustomer, siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
				consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
				sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerAndNoSiteInSessionShouldReturnNoItem()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = emptyList();
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = emptyList();
		final List<Matcher<? super CustomerModel>> customerItemMatchers = emptyList();
		final List<Matcher<? super CartModel>> cartItemMatchers = emptyList();
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = emptyList();
		final List<Matcher<? super ConsentModel>> consentItemMatchers = emptyList();
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = emptyList();
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = emptyList();
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = emptyList();
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = emptyList();
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = emptyList();

		searchAllItemsAndMatchWithUserAndSites(fooCustomer, singleton(0l), siteItemMatchers, storeItemMatchers,
				customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerAndSingleSiteInSessionShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER);

		searchAllItemsAndMatchWithUserAndSites(fooCustomer, singleton(testSiteA), siteItemMatchers, storeItemMatchers,
				customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers,
				customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerAndAllSiteInSessionShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER, SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_B_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER, CUSTOMER_FOO_B_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER,
				CART_ANON_B_MATCHER, CART_FOO_B_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER,
				CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER, CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER,
				CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER,
				CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER,
				SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER,
				AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER,
				RETURN_REQUEST_SITE_B_MATCHER);


		searchAllItemsAndMatchWithUserAndSites(fooCustomer, new HashSet<>(asList(testSiteA, testSiteB)), siteItemMatchers,
				storeItemMatchers, customerItemMatchers, cartItemMatchers, consentTemplateItemMatchers, consentItemMatchers,
				consignmentItemMatchers, customerReviewItemMatchers, sitePreferenceMatchers, auditReportDataMatchers,
				returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerShouldReturnNoItem()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = emptyList();
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = emptyList();
		final List<Matcher<? super CustomerModel>> customerItemMatchers = emptyList();
		final List<Matcher<? super CartModel>> cartItemMatchers = emptyList();
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = emptyList();
		final List<Matcher<? super ConsentModel>> consentItemMatchers = emptyList();
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = emptyList();
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = emptyList();
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = emptyList();
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = emptyList();
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = emptyList();
		searchAllItemsAndMatchWithUser(fooCustomer, siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
				consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
				sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerAShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_A_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_A_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_A_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_A_MATCHER, CART_FOO_A_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_A_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_A_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_A_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_A_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_A_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_A_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_A_MATCHER);

		searchAllItemsAndMatchWithUser(fooCustomerA, siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
				consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
				sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	@Test
	public void testSearchWithFooCustomerBShouldReturnRestrictedItems()
	{
		final List<Matcher<? super BaseSiteModel>> siteItemMatchers = asList(SITE_B_MATCHER);
		final List<Matcher<? super BaseStoreModel>> storeItemMatchers = asList(STORE_B_MATCHER, STORE_A_B_MATCHER);
		final List<Matcher<? super CustomerModel>> customerItemMatchers = asList(CUSTOMER_FOO_B_MATCHER);
		final List<Matcher<? super CartModel>> cartItemMatchers = asList(CART_ANON_B_MATCHER, CART_FOO_B_MATCHER);
		final List<Matcher<? super ConsentTemplateModel>> consentTemplateItemMatchers = asList(CONSENT_TEMPLATE_SITE_B_MATCHER);
		final List<Matcher<? super ConsentModel>> consentItemMatchers = asList(CONSENT_FOO_B_MATCHER);
		final List<Matcher<? super ConsignmentModel>> consignmentItemMatchers = asList(CONSIGNTMENT_SITE_B_MATCHER);
		final List<Matcher<? super CustomerReviewModel>> customerReviewItemMatchers = asList(CUSTOMER_REVIEW_FOO_B_MATCHER);
		final List<Matcher<? super SitePreferenceModel>> sitePreferenceMatchers = asList(SITE_PREFERENCE_FOO_B_MATCHER);
		final Collection<Matcher<? super AuditReportDataModel>> auditReportDataMatchers = asList(AUDIT_REPORT_DATA_FOO_B_MATCHER);
		final List<Matcher<? super ReturnRequestModel>> returnRequestMatchers = asList(RETURN_REQUEST_SITE_B_MATCHER);

		searchAllItemsAndMatchWithUser(fooCustomerB, siteItemMatchers, storeItemMatchers, customerItemMatchers, cartItemMatchers,
				consentTemplateItemMatchers, consentItemMatchers, consignmentItemMatchers, customerReviewItemMatchers,
				sitePreferenceMatchers, auditReportDataMatchers, returnRequestMatchers);
	}

	private static class AnonymousUserMatcher extends TypeSafeMatcher<UserModel>
	{

		private final UserService userService;

		private AnonymousUserMatcher(final UserService userService)
		{
			this.userService = userService;
		}

		@Override
		public void describeTo(final Description description)
		{
			description.appendText("an anonymous customer");
		}

		@Override
		protected boolean matchesSafely(final UserModel user)
		{
			return userService.isAnonymousUser(user);
		}
	}
}
