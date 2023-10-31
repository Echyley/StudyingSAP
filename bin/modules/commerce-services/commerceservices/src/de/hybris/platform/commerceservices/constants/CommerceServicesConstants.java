/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.constants;

import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.core.PK;
import de.hybris.platform.util.DiscountValue;


/**
 * Global class for all CommerceServices constants. You can add global constants for your extension into this class.
 */
public final class CommerceServicesConstants extends GeneratedCommerceServicesConstants
{
	/**
	 * If true, configured hooks will be called before and after addToCart calls in commerce services.
	 */
	public static final String ADDTOCARTHOOK_ENABLED = "commerceservices.commerceaddtocartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart calculation calls in commerce services.
	 */
	public static final String CARTCALCULATIONHOOK_ENABLED = "commerceservices.commercecartcalculationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after authorize payment calls in commerce services.
	 */
	public static final String AUTHORIZEPAYMENTHOOK_ENABLED = "commerceservices.authorizepaymentmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after place order calls in commerce services.
	 */
	public static final String PLACEORDERHOOK_ENABLED = "commerceservices.commerceplaceordermethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after update cart entries calls in commerce services.
	 */
	public static final String UPDATECARTENTRYHOOK_ENABLED = "commerceservices.commerceupdatecartentryhook.enabled";
	/**
	 * If true, configured hooks will be called before and after addToCart calls in commerce services.
	 */
	public static final String SAVECARTHOOK_ENABLED = "commerceservices.commercesavecartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after flag for deletion calls in commerce services.
	 */
	public static final String FLAGFORDELETIONHOOK_ENABLED = "commerceservices.commerceflagfordeletionmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after
	 * {@link CommerceSaveCartService#cloneSavedCart(de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter)}
	 * calls in commerce services.
	 */
	public static final String CLONESAVEDCARTHOOK_ENABLED = "commerceservices.commerceclonesavedcartmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before cart restoration in commerce services.
	 */
	public static final String SAVECARTRESTORATIONHOOK_ENABLED = "commerceservices.commercesavecartrestorationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart validation calls in commerce services.
	 */
	public static final String CARTVALIDATIONHOOK_ENABLED = "commerceservices.commercecartvalidationmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart validation calls in commerce services.
	 */
	public static final String REMOVEENTRYGROUPHOOK_ENABLED = "commerceservices.commerceremoveentrygroupmethodhook.enabled";
	/**
	 * If true, configured hooks will be called before and after cart merge calls in commerce services.
	 */
	public static final String CARTMERGEHOOK_ENABLED = "commerceservices.commercecartmergemethodhook.enabled";
	/**
	 * Default parent group id for all seller agents
	 */
	public static final String SELLER_AGENT_GROUP_UID = "salesemployeegroup";
	/**
	 * Default parent group id for all seller approver agents
	 */
	public static final String SELLER_APPROVER_AGENT_GROUP_UID = "salesapprovergroup";
	/**
	 * Defines organization related user groups(=roles).
	 */
	public static final String ORGANIZATION_ROLES = "commerceservices.organization.roles";
	/**
	 * Defines organization related admin groups.
	 */
	public static final String ORGANIZATION_ROLES_ADMIN_GROUPS = "commerceservices.organization.roles.admin.groups";
	/**
	 * Defines quote seller auto approval's threshold amount
	 */
	public static final String QUOTE_APPROVAL_THRESHOLD = "commerceservices.quote.seller.auto.approval.threshold";
	/**
	 * Defines the configurable default offer validity period in days
	 */
	public static final String QUOTE_DEFAULT_OFFER_VALIDITY_PERIOD_IN_DAYS = "commerceservices.quote.default.offer.validity.period.days";
	/**
	 * Defines the minimum offer validity period in days
	 */
	public static final String QUOTE_MIN_OFFER_VALIDITY_PERIOD_IN_DAYS = "commerceservices.quote.min.offer.validity.period.days";
	/**
	 * Discount code for quote specific {@link DiscountValue} objects.
	 */
	public static final String QUOTE_DISCOUNT_CODE = "QuoteDiscount";
	/**
	 * Defines the default minimum threshold for initiating a quote negotiation.
	 */
	public static final String QUOTE_REQUEST_INITIATION_THRESHOLD = "quote.request.initiation.threshold";
	/**
	 * If true, OrgUnit path will be generated or updated.
	 */
	public static final String ORG_UNIT_PATH_GENERATION_ENABLED = "commerceservices.org.unit.path.generation.enabled";
	
	/**
	 * If true, applies additional filter to filter find orders query by parent attribute.
	 */
	public static final String FIND_ORDERS_ADDITIONAL_FILTER_ENABLED = "commerceservices.find.orders.additional.filter.enabled";

	/**
	 * Defines additional filter to filter find orders query by parent attribute.
	 */
	public static final String FIND_ORDERS_ADDITIONAL_FILTER = " AND {parent} IS NULL";

	public static final String USER_CONSENTS = "user-consents";

	public static final String CONSENT_GIVEN = "GIVEN";

	public static final String POPULATING_CONSENTS_ENABLED = "populating-consents-enabled";

	public static final String CART_CALCULATION_RESET_DELIVERY_COST_ENABLED = "commerceservices.cartCalculation.resetDeliveryCost.enabled";
	/**
	 * Definition of the dummy sites, used in search restriction
	 */
	public static final PK NO_SITES_AVAILABLE_DUMMY = PK.NULL_PK;

	/**
	 * Definition of the sites name in session, that could be used in search restriction such as ?session.sites
	 */
	public static final String SITE_SESSION_ATTRIBUTE_NAME = "sites";

	/**
	 * If set, new created SiteEmployeeGroup will inherit from this group
	 */
	public static final String SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT = "commerceservices.siteemployeegroup.groups.default";

	public static final String MULTI_SITE_GROUP_GROUP_DEFAULT = "commerceservices.multisitegroup.group.default";

	public static final String MULTI_SITE_GROUP_DEFAULT_NAME = "multisitegroup";

	/**
	 * Definition of the minimum time delay, that could be used in some specific operations based on requirement
	 */
	public static final String DELAY_MS_EMAIL_CHANGE = "commerceservices.delay_ms.emailchange";

	/**
	 * The default/min/max value for property: commerceservices.delay_ms.emailchange
	 */
	public static final long DELAY_MS_EMAIL_CHANGE_DEFAULT = 200L;
	public static final long DELAY_MS_EMAIL_CHANGE_MIN = 0L;
	public static final long DELAY_MS_EMAIL_CHANGE_MAX = 60 * 1000L;
}
