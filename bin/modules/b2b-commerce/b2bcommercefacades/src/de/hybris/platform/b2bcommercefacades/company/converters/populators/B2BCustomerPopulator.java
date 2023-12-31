/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CustomerData} with data form {@link B2BCustomerModel}.
 */
public class B2BCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private CommonI18NService commonI18NService;
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{

		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		if (source instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) source;

			if (customer.getTitle() != null)
			{
				target.setTitleCode(customer.getTitle().getCode());
			}
			target.setUid(customer.getUid());
			target.setName(customer.getName());
			target.setCustomerId(customer.getCustomerID());
			target.setActive(Boolean.TRUE.equals(customer.getActive()));
			target.setCurrency(getCurrencyConverter().convert(getCommonI18NService().getCurrentCurrency()));
			target.setEmail(customer.getEmail());

			populateUnit(customer, target);

			populateRoles(customer, target);
			populatePermissionGroups(customer, target);

			if (customer.getOriginalUid() != null)
			{
				target.setDisplayUid(customer.getOriginalUid());
			}
		}
	}

	protected void populateUnit(final B2BCustomerModel customer, final CustomerData target)
	{
		// minimal properties are populated, as require by customer paginated page.
		final B2BUnitModel parent = getB2bUnitService().getParent(customer);
		if (parent != null)
		{
			final B2BUnitData b2BUnitData = new B2BUnitData();
			b2BUnitData.setUid(parent.getUid());
			b2BUnitData.setName(parent.getLocName());
			b2BUnitData.setActive(Boolean.TRUE.equals(parent.getActive()));

			// unit's cost centers
			if (CollectionUtils.isNotEmpty(parent.getCostCenters()))
			{
				final List<B2BCostCenterData> costCenterDataCollection = new ArrayList<>();
				for (final B2BCostCenterModel costCenterModel : parent.getCostCenters())
				{
					final B2BCostCenterData costCenterData = new B2BCostCenterData();
					costCenterData.setCode(costCenterModel.getCode());
					costCenterData.setName(costCenterModel.getName());
					costCenterDataCollection.add(costCenterData);
				}
				b2BUnitData.setCostCenters(costCenterDataCollection);
			}
			target.setUnit(b2BUnitData);
		}
	}

	protected void populateRoles(final B2BCustomerModel source, final CustomerData target)
	{
		final List<String> roles = new ArrayList<String>();
		final Set<PrincipalGroupModel> roleModels = new HashSet<>(source.getGroups());
		CollectionUtils.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		CollectionUtils.filter(roleModels,
				PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUserGroupModel.class)));
		for (final PrincipalGroupModel role : roleModels)
		{
			// only display allowed usergroups
			if (getB2BUserGroupsLookUpStrategy().getUserGroups().contains(role.getUid()))
			{
				roles.add(role.getUid());
			}
		}
		target.setRoles(roles);
	}

	protected void populatePermissionGroups(final B2BCustomerModel source, final CustomerData target)
	{
		final Collection<B2BUserGroupModel> permissionGroups = CollectionUtils.select(source.getGroups(),
				PredicateUtils.instanceofPredicate(B2BUserGroupModel.class));
		final List<B2BUserGroupData> permissionGroupData = new ArrayList<>(permissionGroups.size());

		for (final B2BUserGroupModel group : permissionGroups)
		{
			final B2BUserGroupData b2BUserGroupData = new B2BUserGroupData();
			b2BUserGroupData.setName(group.getName());
			b2BUserGroupData.setUid(group.getUid());
			final B2BUnitData b2BUnitData = new B2BUnitData();
			b2BUnitData.setUid(group.getUnit().getUid());
			b2BUnitData.setName(group.getUnit().getLocName());
			b2BUnitData.setActive(Boolean.TRUE.equals(group.getUnit().getActive()));

			b2BUserGroupData.setUnit(b2BUnitData);
			permissionGroupData.add(b2BUserGroupData);
		}

		target.setPermissionGroups(permissionGroupData);
	}

	protected B2BUserGroupsLookUpStrategy getB2BUserGroupsLookUpStrategy()
	{
		return b2BUserGroupsLookUpStrategy;
	}

	@Required
	public void setB2BUserGroupsLookUpStrategy(final B2BUserGroupsLookUpStrategy b2bUserGroupsLookUpStrategy)
	{
		b2BUserGroupsLookUpStrategy = b2bUserGroupsLookUpStrategy;
	}

	protected B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
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

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}
}
