/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BBookingLineEntryDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.StandardDateRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the {@link B2BCostCenterService}
 *
 * @spring.bean b2bCostCenterService
 */
public class DefaultB2BCostCenterService implements B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel>
{
	private static final String B2BUNIT_IS_MISSING = " is missing a B2BUnit.";

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BBudgetDao b2bBudgetDao;
	private B2BCostCenterDao b2bCostCenterDao;
	private DefaultB2BBookingLineEntryDao b2bBookingLineEntryDao;
	private SessionService sessionService;
	private SearchRestrictionService searchRestrictionService;
	private PrincipalGroupMembersDao principalGroupMembersDao;

	/**
	 * @deprecated Since 4.4. Use {@link #getCostCentersForUnitBranch(B2BCustomerModel, CurrencyModel)} instead
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public List<B2BCostCenterModel> findCostCentersForUnitBranch(final B2BCustomerModel employee, final CurrencyModel currency)
	{
		return getCostCentersForUnitBranch(employee, currency);
	}

	@Override
	public List<B2BCostCenterModel> getCostCentersForUnitBranch(final B2BCustomerModel employee, final CurrencyModel currency)
	{
		final B2BUnitModel parentUnit = getB2bUnitService().getParent(employee);
		Assert.notNull(parentUnit, employee + B2BUNIT_IS_MISSING);

		return this.getCostCentersForUnitBranch(parentUnit, currency);
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getCostCentersForUnitBranch(B2BUnitModel, CurrencyModel)} instead
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public List<B2BCostCenterModel> findCostCentersForUnitBranch(final B2BUnitModel unit, final CurrencyModel currency)
	{
		return getCostCentersForUnitBranch(unit, currency);
	}

	@Override
	public List<B2BCostCenterModel> getCostCentersForUnitBranch(final B2BUnitModel unit, final CurrencyModel currency)
	{
		final Set<B2BUnitModel> branch = getB2bUnitService().getBranch(unit);
		return getB2bCostCenterDao().findActiveCostCentersByBranchAndCurrency(branch, currency);

	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public List<B2BCostCenterModel> findCostCenters(final B2BUnitModel unit, final CurrencyModel currency)
	{
		return (List<B2BCostCenterModel>) CollectionUtils.select(unit.getCostCenters(), new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final B2BCostCenterModel b2bCostCenterModel = (B2BCostCenterModel) object;
				return b2bCostCenterModel.getCurrency().equals(currency) && b2bCostCenterModel.getActive().booleanValue();
			}
		});
	}

	/**
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.b2b.services.B2BBudgetService#getCurrentBudgets(de.hybris.platform.b2b.model.B2BCostCenterModel)}
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public Collection<B2BBudgetModel> getCurrentBudgets(final B2BCostCenterModel costCenter)
	{
		final Set<B2BBudgetModel> b2bBudgets = new HashSet<B2BBudgetModel>();
		final Date currentDate = new Date();
		for (final B2BBudgetModel b2bBudget : costCenter.getBudgets())
		{
			final StandardDateRange dateRange = b2bBudget.getDateRange();
			if (b2bBudget.getActive().booleanValue() && dateRange.encloses(currentDate)
					&& b2bBudget.getCurrency().equals(costCenter.getCurrency()))
			{
				b2bBudgets.add(b2bBudget);
			}
		}
		return b2bBudgets;

	}

	/**
	 * @deprecated Since 4.4. Use {@link #getAvailableCurrencies(UserModel)} instead
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public Set<CurrencyModel> findAvailableCurrencies(final UserModel user)
	{
		return getAvailableCurrencies(user);
	}

	@Override
	public Set<CurrencyModel> getAvailableCurrencies(final UserModel user)
	{
		if (!(user instanceof B2BCustomerModel))
		{
			return Collections.emptySet();
		}

		final B2BUnitModel parentUnit = getB2bUnitService().getParent((B2BCustomerModel) user);
		Assert.notNull(parentUnit, user + B2BUNIT_IS_MISSING);

		return this.getAvailableCurrencies(parentUnit);
	}

	@Override
	public CurrencyModel getAvailableCurrencyByUser(final UserModel user)
	{
		if (!(user instanceof B2BCustomerModel))
		{
			return null;
		}

		final B2BUnitModel parentUnit = getB2bUnitService().getParent((B2BCustomerModel) user);
		Assert.notNull(parentUnit, user + B2BUNIT_IS_MISSING);

		final List<B2BCostCenterModel> costCenters = parentUnit.getCostCenters();
		if (CollectionUtils.isNotEmpty(costCenters))
		{
			for (B2BCostCenterModel costCenter : costCenters) {
				if (costCenter.getCurrency() != null) {
					return costCenter.getCurrency();
				}
			}
		}

		final Set<B2BCostCenterModel> availableCostCenters = getAvailableCostCentersByB2BUnitHierarchy(parentUnit);
		if (CollectionUtils.isNotEmpty(availableCostCenters))
		{
			return availableCostCenters.iterator().next().getCurrency();
		}
		return null;
	}

	/**
	 * Gets the available b2bCostCenters below the unit hierarchy
	 * @param unit
	 * @return
	 */
	protected Set<B2BCostCenterModel> getAvailableCostCentersByB2BUnitHierarchy(final B2BUnitModel unit)
	{
		final Set<B2BUnitModel> result = new HashSet<>();
		result.add(unit);
		Set<B2BUnitModel> b2bUnits = Collections.singleton(unit);
		do
		{
			// If there is a unit contained cost center at this level, it can be terminated directly
			final Set<B2BCostCenterModel> b2BCostCenterModels = new HashSet<>(
					getB2bCostCenterDao().findAvailableCostCentersByB2BUnit((Set) b2bUnits));
			if (CollectionUtils.isNotEmpty(b2BCostCenterModels))
			{
				return b2BCostCenterModels;
			}
			// grab whole next layer
			final Set<B2BUnitModel> currentLevel = new HashSet<>(
					getPrincipalGroupMembersDao().findHierarchyMembersByType((Set) b2bUnits, B2BUnitModel.class));
			// cycle safe
			currentLevel.removeAll(result);
			// add to result
			result.addAll(currentLevel);
			b2bUnits = currentLevel;
		}
		while (!b2bUnits.isEmpty());
		return new HashSet<>();
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getAvailableCurrencies(B2BUnitModel)} instead
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public Set<CurrencyModel> findAvailableCurrencies(final B2BUnitModel unit)
	{
		return getAvailableCurrencies(unit);
	}

	@Override
	public Set<CurrencyModel> getAvailableCurrencies(final B2BUnitModel unit)
	{

		final Set<B2BUnitModel> branch = getB2bUnitService().getBranch(unit);
		final Set<CurrencyModel> currencies = new HashSet<CurrencyModel>();
		final List<B2BCostCenterModel> costCenters = new ArrayList<B2BCostCenterModel>();

		for (final B2BUnitModel b2bUnitModel : branch)
		{
			costCenters.addAll(b2bUnitModel.getCostCenters());
		}
		for (final B2BCostCenterModel costCenter : costCenters)
		{
			currencies.add(costCenter.getCurrency());
		}

		return Collections.unmodifiableSet(currencies);
	}

	@Override
	public boolean isCostCenterExisting(final String code)
	{
		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return Boolean.valueOf(getCostCenterForCode(code) != null);
			}
		})).booleanValue();
	}

	/**
	 * @deprecated Since 4.4. Use {@link B2BCostCenterService#getCostCenterForCode(String)} instead
	 *
	 * @see de.hybris.platform.b2b.services.B2BCostCenterService#getB2BCostCenterForCode(java.lang.String)
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public B2BCostCenterModel getB2BCostCenterForCode(final String code)
	{
		return getB2bCostCenterDao().findByCode(code);
	}

	@Override
	public Double getTotalCost(final B2BCostCenterModel costCenter, final Date startDate, final Date endDate)
	{
		return getB2bBookingLineEntryDao().findTotalCostByCostCenterAndDate(costCenter, startDate, endDate);
	}

	/**
	 * @deprecated Since 4.4. Use {@link #getCostCenterForCode(String)} instead
	 */
	@Override
	@Deprecated(since = "4.4", forRemoval = true)
	public B2BCostCenterModel findByCode(final String code)
	{
		return getCostCenterForCode(code);
	}

	@Override
	public B2BCostCenterModel getCostCenterForCode(final String code)
	{
		return getB2bCostCenterDao().findByCode(code);
	}

	@Override
	public List<B2BCostCenterModel> getAllCostCenters()
	{
		return getB2bCostCenterDao().find();
	}

	/**
	 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgets()}
	 */
	@Deprecated(since = "4.4", forRemoval = true)
	@Override
	public Set<B2BBudgetModel> getB2BBudgets()
	{
		return new HashSet<B2BBudgetModel>(getB2bBudgetDao().find());
	}

	/**
	 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgetForCode(String)}
	 */
	@Deprecated(since = "6.0", forRemoval = true)
	@Override
	public B2BBudgetModel getB2BBudgetForCode(final String code)
	{
		return getB2bBudgetDao().findBudgetByCode(code);
	}

	@Override
	public Set<B2BCostCenterModel> getB2BCostCenters(final List<AbstractOrderEntryModel> entries)
	{
		final Set<B2BCostCenterModel> costCenters = new HashSet<B2BCostCenterModel>();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getCostCenter() != null)
			{
				costCenters.add(abstractOrderEntryModel.getCostCenter());
			}
		}
		return costCenters;
	}

	@Required
	public void setB2bBookingLineEntryDao(final DefaultB2BBookingLineEntryDao b2bBookingLineEntryDao)
	{
		this.b2bBookingLineEntryDao = b2bBookingLineEntryDao;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	protected DefaultB2BBookingLineEntryDao getB2bBookingLineEntryDao()
	{
		return b2bBookingLineEntryDao;
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

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	@Required
	public void setB2bCostCenterDao(final B2BCostCenterDao b2bCostCenterDao)
	{
		this.b2bCostCenterDao = b2bCostCenterDao;
	}

	protected B2BCostCenterDao getB2bCostCenterDao()
	{
		return b2bCostCenterDao;
	}

	@Required
	public void setB2bBudgetDao(final B2BBudgetDao b2bBudgetDao)
	{
		this.b2bBudgetDao = b2bBudgetDao;
	}

	protected B2BBudgetDao getB2bBudgetDao()
	{
		return b2bBudgetDao;
	}

	protected PrincipalGroupMembersDao getPrincipalGroupMembersDao()
	{
		return principalGroupMembersDao;
	}

	public void setPrincipalGroupMembersDao(final PrincipalGroupMembersDao principalGroupMembersDao)
	{
		this.principalGroupMembersDao = principalGroupMembersDao;
	}
}
