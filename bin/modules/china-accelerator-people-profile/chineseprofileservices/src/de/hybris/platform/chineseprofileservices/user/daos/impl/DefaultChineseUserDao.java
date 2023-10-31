/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileservices.user.daos.impl;


import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultUserDao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultChineseUserDao extends DefaultUserDao
{
	private static final Pattern MOBILE_NO_PATTERN = Pattern.compile("^(\\+)?(\\d{2,3})?(\\s)?(\\d{11})$");
	private static final Pattern UID_DECORATION_DELIMITER = Pattern.compile("\\|");
	private static final String FIND_USER_BY_UID = String.format("SELECT {%s} FROM {%s} WHERE {%s}=?%s", UserModel.PK,
			UserModel._TYPECODE, UserModel.UID, UserModel.UID);
	private static final String FIND_ISOLATED_USER_BY_MOBILE = String.format(
			"SELECT {%s} FROM {%s} WHERE {%s}=?%s AND {%s} IN ({{SELECT {%s} FROM {%s} WHERE {%s}=?%s}})", CustomerModel.PK,
			CustomerModel._TYPECODE, CustomerModel.MOBILENUMBER, CustomerModel.MOBILENUMBER, CustomerModel.SITE, BaseSiteModel.PK,
			BaseSiteModel._TYPECODE, BaseSiteModel.UID, BaseSiteModel.UID);
	private static final String FIND_NON_ISOLATED_USER_BY_MOBILE = String.format(
			"SELECT {%s} FROM {%s} WHERE {%s}=?%s AND {%s} IS NULL", CustomerModel.PK, CustomerModel._TYPECODE,
			CustomerModel.MOBILENUMBER, CustomerModel.MOBILENUMBER, CustomerModel.SITE);

	@Override
	public UserModel findUserByUID(final String loginName)
	{
		if(loginName == null)
		{
			return null;
		}
		final String[] loginNameElements = UID_DECORATION_DELIMITER.split(loginName);
		final Matcher matcher = MOBILE_NO_PATTERN.matcher(loginNameElements[0]);
		final FlexibleSearchQuery query;
		if (matcher.matches())
		{
			if (loginNameElements.length > 1)
			{
				query = new FlexibleSearchQuery(FIND_ISOLATED_USER_BY_MOBILE);
				query.addQueryParameter(CustomerModel.MOBILENUMBER, loginNameElements[0]);
				query.addQueryParameter(BaseSiteModel.UID, loginNameElements[1]);
			}
			else
			{
				query = new FlexibleSearchQuery(FIND_NON_ISOLATED_USER_BY_MOBILE);
				query.addQueryParameter(CustomerModel.MOBILENUMBER, loginNameElements[0]);
			}
		}
		else
		{
			query = new FlexibleSearchQuery(FIND_USER_BY_UID);
			query.addQueryParameter(UserModel.UID, loginName);
		}

		final List<UserModel> resList = getFlexibleSearchService().<UserModel>search(query).getResult();
		if (resList.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					"Found " + resList.size() + " users with the unique uid or mobile No. '" + loginName + "'");
		}
		else
		{
			return resList.isEmpty() ? null : resList.get(0);
		}
	}

}
