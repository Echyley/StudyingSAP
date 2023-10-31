/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.daos.impl;

import de.hybris.platform.b2b.punchout.daos.PunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;


/**
 * Default implementation for {@link PunchOutCredentialDao}.
 */
public class DefaultPunchOutCredentialDao extends DefaultGenericDao<PunchOutCredentialModel> implements PunchOutCredentialDao
{
	private List<String> caseInsensitiveDomains;

	public DefaultPunchOutCredentialDao()
	{
		super(PunchOutCredentialModel._TYPECODE);
	}

	@Override
	public PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity)
			throws AmbiguousIdentifierException
	{
		final Map<String, String> params = new HashMap<>();
		params.put(PunchOutCredentialModel.DOMAIN, StringUtils.lowerCase(domain));
		String queryString =
				"select {pk} from {" + PunchOutCredentialModel._TYPECODE + "} where lower({" + PunchOutCredentialModel.DOMAIN
						+ "}) = ?domain";

		if (getCaseInsensitiveDomains().contains(StringUtils.lowerCase(domain)))
		{
			queryString += " and lower({" + PunchOutCredentialModel.IDENTITY + "}) = ?identity";
			params.put(PunchOutCredentialModel.IDENTITY, StringUtils.lowerCase(identity));
		}
		else
		{
			queryString += " and {" + PunchOutCredentialModel.IDENTITY + "} = ?identity";
			params.put(PunchOutCredentialModel.IDENTITY, identity);
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameters(params);
		final SearchResult<PunchOutCredentialModel> searchResult = getFlexibleSearchService().search(query);
		final List<PunchOutCredentialModel> resList = searchResult.getResult();
		if (resList.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					"Found " + resList.size() + " PunchOut Credentials with domain '" + domain + "' and identity '" + identity + "'");
		}
		return resList.isEmpty() ? null : resList.get(0);
	}

	@Override
	public List<PunchOutCredentialModel> getExpiredPunchOutCredentials(final int expiredDays)
	{
		final Map<String, Date> params = new HashMap<>();
		final Date expireDate = LocalDateTime.now().minusDays(expiredDays).toDate();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"select {pk} from {PunchOutCredential as p} where {p:sharedsecretmodifiedtime}<?sharedsecretmodifiedtime");
		params.put(PunchOutCredentialModel.SHAREDSECRETMODIFIEDTIME, expireDate);
		query.addQueryParameters(params);
		final SearchResult<PunchOutCredentialModel> searchResult = getFlexibleSearchService().search(query);
		return searchResult.getResult();
	}

	protected List<String> getCaseInsensitiveDomains()
	{
		return caseInsensitiveDomains;
	}

	public void setCaseInsensitiveDomains(final List<String> caseInsensitiveDomains)
	{
		this.caseInsensitiveDomains = caseInsensitiveDomains;
	}

}
