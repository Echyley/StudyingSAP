/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.impl;

import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.ASM_SESSION_PARAMETER;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_NAME_ASC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_NAME_DESC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_UID_ASC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_UID_DESC;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceCartBindException;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.validator.CustomerSiteValidator;
import de.hybris.platform.commerceservices.model.user.SiteEmployeeGroupModel;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.exceptions.ValidationViolationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;


/**
 * Default implementation of {@link AssistedServiceService}
 */
public class DefaultAssistedServiceService implements AssistedServiceService
{
	private static final Logger LOG = Logger.getLogger(DefaultAssistedServiceService.class);

	private static final String USERNAME = "username";
	private static final String CURRENTDATE = "currentDate";
	private static final String LOGINDISABLED_PARAMETER = "loginDisabled";
	private static final String SITE_PARAMETER = "site";
	public static final String ACTING_USER_UID = "ACTING_USER_UID";
	public static final String NOT_ANONYMOUS_CART_ERROR = "asm.bindCart.error.not_anonymous_cart";
	public static final String MISSING_CUSTOMER_ID = "asm.bindCart.error.missing_customer_id";
	public static final String MISSING_CART_ID = "asm.bindCart.error.missing_cart_id";
	public static final String CUSTOMER_NOT_FOUND = "asm.bindCart.error.unknown_customer_id";
	public static final String UNKNOWN_CART_ERROR = "asm.bindCart.error.unknown_cart_id";


	private CartService cartService;
	private SessionService sessionService;
	private UserService userService;
	private BaseSiteService baseSiteService;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;
	private PagedFlexibleSearchService pagedFlexibleSearchService;
	private CommerceCartService commerceCartService;
	private CustomerAccountService customerAccountService;
	private CommonI18NService commonI18NService;
	private TimeService timeService;

	private ConfigurationService configurationService;

	@Override
	public SearchPageData<CustomerModel> getCustomers(final String searchCriteria, final PageableData pageableData)
	{

		final Map<String, Object> params = new HashMap<>();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		final boolean isSiteIsolated = baseSite != null && baseSite.getDataIsolationEnabled().booleanValue();

		final StringBuilder builder = getCustomerSearchQuery(searchCriteria);

		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);

		if (baseSite != null)
		{
			if (isSiteIsolated)
			{
				builder.append("AND {p:" + CustomerModel.SITE + "} = ?" + SITE_PARAMETER + " ");
				params.put(SITE_PARAMETER, baseSite);
			}
			else
			{
				builder.append("AND {p:" + CustomerModel.SITE + "} IS NULL ");
			}
		}
		if (StringUtils.isNotBlank(searchCriteria))
		{
			params.put(USERNAME, searchCriteria.toLowerCase());
		}

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_UID_ASC, builder.toString() + " ORDER BY {p." + CustomerModel.UID + "} ASC"),
				createSortQueryData(SORT_BY_UID_DESC, builder.toString() + " ORDER BY {p." + CustomerModel.UID + "} DESC"),
				createSortQueryData(SORT_BY_NAME_ASC, builder.toString() + " ORDER BY {p." + CustomerModel.NAME + "} ASC"),
				createSortQueryData(SORT_BY_NAME_DESC, builder.toString() + " ORDER BY {p." + CustomerModel.NAME + "} DESC"));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_UID_ASC, params, pageableData);
	}

	protected StringBuilder getCustomerSearchQuery(final String searchCriteria)
	{
		final StringBuilder builder = new StringBuilder();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();

		builder.append("SELECT ");
		builder.append("{p:" + CustomerModel.PK + "} ");
		builder.append("FROM {" + CustomerModel._TYPECODE + " AS p} ");
		builder.append("WHERE NOT {" + CustomerModel.UID + "}='" + UserConstants.ANONYMOUS_CUSTOMER_UID + "' ");
		builder.append("AND {p:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER + " ");
		if (baseSite != null)
		{
			final boolean isSiteIsolated = baseSite.getDataIsolationEnabled().booleanValue();
			if (isSiteIsolated)
			{
				builder.append("AND {p:" + CustomerModel.SITE + "} = ?" + SITE_PARAMETER + " ");
			}
			else
			{
				builder.append("AND {p:" + CustomerModel.SITE + "} IS NULL ");
			}
		}
		builder.append("AND ({p:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL OR {p:" + CustomerModel.DEACTIVATIONDATE + "} > ?"
				+ CURRENTDATE + ") ");

		if (!StringUtils.isBlank(searchCriteria))
		{
			builder.append("AND (LOWER({p:" + CustomerModel.UID + "}) LIKE CONCAT(?username, '%') ");
			builder.append("OR LOWER({p:name}) LIKE CONCAT('%', CONCAT(?username, '%'))) ");
		}
		return builder;
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	@Override
	public void bindCustomerToCart(final String customerId, final String cartId) throws AssistedServiceException
	{
		final UserModel customer = customerId == null ? getUserService().getCurrentUser()
				: getUserService().getUserForUID(customerId);
		final CartModel cart = cartId == null ? getCartService().getSessionCart()
				: getCartByCode(cartId, getUserService().getAnonymousUser());

		validateCustomerAndCart(customer, cart);

		getUserService().setCurrentUser(customer);
		getCartService().setSessionCart(cart);
		getCartService().changeCurrentCartUser(customer);
		getAsmSession().setEmulatedCustomer(customer);
	}

	private void validateCustomerAndCart(UserModel customer, CartModel cart) throws AssistedServiceException
	{
		if (cart == null)
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(UNKNOWN_CART_ERROR));
		}
		if (!getUserService().isAnonymousUser(cart.getUser()))
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(NOT_ANONYMOUS_CART_ERROR));
		}
		if (customer instanceof CustomerModel && !isCustomerForCurrentSite((CustomerModel) customer))
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(CUSTOMER_NOT_FOUND));
		}
	}

	@Override
	public void bindCustomerToCartWithoutEmulating(final String customerId, final String cartId) throws AssistedServiceException
	{
		if (StringUtils.isEmpty(customerId))
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(MISSING_CUSTOMER_ID));
		}
		if (StringUtils.isEmpty(cartId))
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(MISSING_CART_ID));
		}

		try
		{
			final UserModel customer = getUserService().getUserForUID(customerId);
			final CartModel cart = getCartByCode(cartId, getUserService().getAnonymousUser());
			validateCustomerAndCart(customer, cart);
			getUserService().setCurrentUser(customer);
			getCartService().setSessionCart(cart);
			getCartService().changeCurrentCartUser(customer);
		}
		catch (final UnknownIdentifierException e)
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString(CUSTOMER_NOT_FOUND));
		}
	}

	@Override
	public AssistedServiceSession getAsmSession()
	{
		return getSessionService().getAttribute(ASM_SESSION_PARAMETER);
	}

	@Override
	public void restoreCartToUser(final CartModel cart, final UserModel user)
	{
		if (user != null && CollectionUtils.isNotEmpty(cart.getEntries()))
		{
			getCartService().changeCurrentCartUser(user);
			// refresh persistence context after cart user manipulations
			// without this step - customer will not have modified cart
			getModelService().refresh(user);
		}
	}

	@Override
	public CustomerModel createNewCustomer(final String customerId, final String customerName)
			throws DuplicateUidException, AssistedServiceException
	{
		final CustomerModel customerModel = getModelService().create(CustomerModel.class);

		customerModel.setName(customerName.trim());
		customerModel.setUid(customerId);
		customerModel.setLoginDisabled(false);
		customerModel.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		customerModel.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		try
		{
			getCustomerAccountService().register(customerModel, null);
			LOG.info(String.format("New customer has been created via ASM: uid [%s]", customerId));
			return customerModel;
		}
		catch (final ModelSavingException e)
		{
			if (e.getCause() instanceof ValidationViolationException)
			{
				final List<HybrisConstraintViolation> hybrisConstraintViolationList = ((ValidationViolationException) e.getCause()).getHybrisConstraintViolations().stream()
						.filter(violation -> violation.getConstraintViolation().getConstraintDescriptor().getConstraintValidatorClasses().contains(
								CustomerSiteValidator.class)).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(hybrisConstraintViolationList))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("Can not create customer with the site: %s", getBaseSiteService().getCurrentBaseSite().getUid()));
					}
					throw new AssistedServiceException(String.format("Can not create customer with the site: %s", getBaseSiteService().getCurrentBaseSite().getUid()), e);
				}
			}
			throw e;
		}
	}

	@Override
	public Collection<CartModel> getCartsForCustomer(final CustomerModel customer)
	{
		final BaseSiteModel paramBaseSiteModel = getBaseSiteService().getCurrentBaseSite();
		return getCommerceCartService().getCartsForSiteAndUser(paramBaseSiteModel, customer);
	}

	@Override
	public UserModel getCustomer(final String customerId)
	{
		if (StringUtils.isBlank(customerId))
		{
			return getUserService().getAnonymousUser();
		}
		else
		{
			final StringBuilder buf = new StringBuilder();
			// this rouinte may called with undecoratedUiD and decorated. Adding code here 
			// to ensure we support undecoratedUiD
			if (getUserService().isUserExisting(customerId))
			{
				return getUserService().getUserForUID(customerId);
			}
			// select the chosen customer using his UID or CustomerId
			buf.append("SELECT DISTINCT {p:" + CustomerModel.PK + "} ");
			buf.append("FROM {" + CustomerModel._TYPECODE + " as p } ");
			buf.append("WHERE {p:" + CustomerModel.UID + "} = ?customerId ");
			buf.append("OR {p:" + CustomerModel.CUSTOMERID + "} = ?customerId ");
			final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
			query.addQueryParameter("customerId", customerId);
			final List<CustomerModel> matchCustomers = getFlexibleSearchService().<CustomerModel> search(query).getResult();
			if (CollectionUtils.isEmpty(matchCustomers))
			{
				throw new UnknownIdentifierException(
						(new StringBuilder("Cannot find user with id '")).append(customerId).append("'").toString());
			}
			if (matchCustomers.size() > 1)
			{
				LOG.warn("More than two customers were found with id=[" + customerId + "]");
			}
			return matchCustomers.iterator().next();
		}
	}

	@Override
	public CartModel getLatestModifiedCart(final UserModel customer)
	{
		return getCommerceCartService().getCartForGuidAndSiteAndUser(null, getBaseSiteService().getCurrentBaseSite(), customer);
	}

	/**
	 * @deprecated since 6.6, use {@link AssistedServiceService#getOrderByCode(String, UserModel)} instead
	 *
	 * @param orderCode
	 * 			  the order's code
	 * @param customer
	 * 			  customer model whose order to be returned
     */
	@Deprecated(since = "6.6", forRemoval = true)
	@Override
	public OrderModel gerOrderByCode(final String orderCode, final UserModel customer)
	{
		return getOrderByCode(orderCode, customer);
	}

	@Override
	public OrderModel getOrderByCode(final String orderCode, final UserModel customer)
	{
		final StringBuilder buf = new StringBuilder();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();

		// select the chosen order using his code
		buf.append("SELECT DISTINCT {p:" + OrderModel.PK + "} ");
		buf.append("FROM {" + OrderModel._TYPECODE + " as p } ");
		buf.append("WHERE ({p:" + OrderModel.CODE + "} = ?orderCode ");
		buf.append("OR {p:" + OrderModel.GUID + "} = ?orderCode )");

		if (baseSite != null)
		{
			buf.append("AND {p:" + OrderModel.SITE + "} = ?" + SITE_PARAMETER + " ");
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
		query.addQueryParameter("orderCode", orderCode);

		if (baseSite != null)
		{
			query.addQueryParameter(SITE_PARAMETER, baseSite);
		}
		final List<OrderModel> matchedOrder = getFlexibleSearchService().<OrderModel> search(query).getResult();
		if (CollectionUtils.isEmpty(matchedOrder))
		{
			return null;
		}
		if (matchedOrder.size() > 1)
		{
			LOG.warn("More than two orders were found with code=[" + orderCode + "]"); // how??
		}
		final OrderModel order = matchedOrder.iterator().next();
		if (!isAbstractOrderMatchBaseSite(order)
				|| ((!getUserService().isAnonymousUser(customer)) && !order.getUser().getUid().equals(customer.getUid())))
		{
			return null;
		}
		return order;
	}

	@Override
	public CartModel getCartByCode(final String cartCode, final UserModel customer)
	{
		final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(cartCode, customer);
		if (cartModel != null)
		{
			return isAbstractOrderMatchBaseSite(cartModel) ? cartModel : null;
		}
		return getCommerceCartService().getCartForGuidAndSiteAndUser(cartCode, getBaseSiteService().getCurrentBaseSite(), customer);
	}

	@Override
	public PointOfServiceModel getAssistedServiceAgentStore()
	{
		return getAssistedServiceAgentStore(getAsmSession().getAgent());
	}

	@Override
	public PointOfServiceModel getAssistedServiceAgentStore(final UserModel agent)
	{
		if (agent != null && CollectionUtils.isNotEmpty(agent.getAllGroups()))
		{
			final List<StoreEmployeeGroupModel> storeEmployeeGroups = getUserService()
					.getAllUserGroupsForUser(agent, StoreEmployeeGroupModel.class).stream().filter(group -> group.getStore() != null)
					.collect(Collectors.toList());
			if (!storeEmployeeGroups.isEmpty())
			{
				return storeEmployeeGroups.get(0).getStore();
			}

		}
		return null;
	}

	@Override
	public List<PointOfServiceModel> getAllStoresForAgent(final String agentId)
	{
		checkAccessRightToAgent(agentId);
		return getUserService().getAllUserGroupsForUser(getUserService().getUserForUID(agentId), StoreEmployeeGroupModel.class).stream()
						.filter(group -> group.getStore() != null)
						.map(StoreEmployeeGroupModel::getStore)
						.collect(Collectors.toList());
	}

	private void checkAccessRightToAgent(final String targetAgentId) throws AccessDeniedException
	{
		final String actingUserId = sessionService.getCurrentSession().getAttribute(ACTING_USER_UID);
		if (StringUtils.isEmpty(actingUserId))
		{
			// Keep compatibility if acting user is not set(Back office and other scenario
			return;
		}
		final UserModel actingUser = userService.getUserForUID(actingUserId);
		if (isBelongToMultiSiteGroup(actingUser))
		{
			final UserModel targetAgent = userService.getUserForUID(targetAgentId);
			if (!isBelongToMultiSiteGroup(targetAgent))
			{
				throw new AccessDeniedException("Access is denied");
			}
			else
			{
				final Set<SiteEmployeeGroupModel> actingUserSiteEmployeeGroups = getUserService().getAllUserGroupsForUser(actingUser,
						SiteEmployeeGroupModel.class), targetAgentSiteEmployeeGroups = getUserService().getAllUserGroupsForUser(
						targetAgent, SiteEmployeeGroupModel.class);
				if (!actingUserSiteEmployeeGroups.containsAll(targetAgentSiteEmployeeGroups))
				{
					throw new AccessDeniedException("Access is denied");
				}
			}
		}
	}

	/*
	 * If site isolated, we check whether customer site is same to current
	 * else, only customer's site is null is belong to current site
	 */
	private boolean isCustomerForCurrentSite(CustomerModel customer)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		// corner case, we can't judge the current site
		if (currentBaseSite == null)
		{
			return true;
		}
		if (currentBaseSite.getDataIsolationEnabled().booleanValue())
		{
			final String siteUid = currentBaseSite.getUid();
			final BaseSiteModel customSite = customer.getSite();
			if (siteUid != null && customSite != null)
			{
				return siteUid.equals(customSite.getUid());
			}
			// for isolated site, return false if siteUid is null or customerSite is null or not equal
			return false;
		}
		else
		{
			// for non-isolated site, non-isolated customer can access
			return customer.getSite() == null;
		}
	}

	@Override
	public boolean isAbstractOrderMatchBaseSite(final AbstractOrderModel abstractOrderModel)
	{
		return abstractOrderModel.getSite() != null
				&& getBaseSiteService().getCurrentBaseSite().getUid().equals(abstractOrderModel.getSite().getUid());
	}

	@Override
	public boolean isAgentCanLogin(UserModel agent) {
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		return (!isBelongToMultiSiteGroup(agent))|| (isSiteIsolated(baseSite) && isBelongToCurrentIsolatedSite(agent, baseSite));
	}

	protected boolean isBelongToMultiSiteGroup(UserModel user)
	{
		final String multiSiteGroupName = getConfigurationService().getConfiguration()
				.getString(CommerceServicesConstants.MULTI_SITE_GROUP_GROUP_DEFAULT,
						CommerceServicesConstants.MULTI_SITE_GROUP_DEFAULT_NAME);

		final Set<UserGroupModel> allUserGroups = getUserService().getAllUserGroupsForUser(user);

		return allUserGroups.stream()
				.anyMatch(userGroupModel -> StringUtils.equals(userGroupModel.getUid(), multiSiteGroupName));
	}

	protected boolean isBelongToCurrentIsolatedSite(UserModel user, BaseSiteModel site)
	{
		final Set<SiteEmployeeGroupModel> siteEmployeeGroups = getUserService()
				.getAllUserGroupsForUser(user, SiteEmployeeGroupModel.class);
		if (!CollectionUtils.isEmpty(siteEmployeeGroups))
		{
			return siteEmployeeGroups.stream().anyMatch(siteEmployee -> StringUtils.equals(siteEmployee.getVisibleSite().getUid(), site.getUid()));
		}
		return false;
	}

	protected boolean isSiteIsolated(BaseSiteModel site)
	{
		return site != null && site.getDataIsolationEnabled().booleanValue();
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
