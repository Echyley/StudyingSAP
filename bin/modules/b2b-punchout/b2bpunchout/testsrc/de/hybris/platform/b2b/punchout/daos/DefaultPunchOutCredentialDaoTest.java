/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.daos.impl.DefaultPunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutCredentialDaoTest
{
	private static final int EXPIRED_DAYS = 180;
	private static final String PUNCHOUT_USER_ID = "punchoutUserId@cxml.org";
	private static final String PUNCHOUT_DOMAIN = "punchoutDomain";
	private static final String PUNCHOUT_DUNS_DOMAIN = "DUNS";

	@InjectMocks
	private DefaultPunchOutCredentialDao defaultPunchOutCredentialDao;

	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private SearchResult searchResult;
	@Mock
	private List<String> caseInsensitiveDomains;

	@Before
	public void setUp()
	{
		doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
	}

	@Test
	public void testGetPunchOutCredentialNull()
	{
		final PunchOutCredentialModel model = defaultPunchOutCredentialDao.getPunchOutCredential(PUNCHOUT_DOMAIN, PUNCHOUT_USER_ID);
		assertThat(model).isNull();
	}

	@Test
	public void testGetPunchOutCredential()
	{
		final List<PunchOutCredentialModel> result = new ArrayList<>();
		final PunchOutCredentialModel punchOutCredentialModel = new PunchOutCredentialModel();
		result.add(punchOutCredentialModel);
		ArgumentCaptor<FlexibleSearchQuery> captor = ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		doReturn(searchResult).when(flexibleSearchService).search(captor.capture());
		doReturn(result).when(searchResult).getResult();

		final PunchOutCredentialModel model = defaultPunchOutCredentialDao.getPunchOutCredential(PUNCHOUT_DOMAIN, PUNCHOUT_USER_ID);
		assertThat(model).isNotNull();
		assertThat(captor.getValue().getQuery()).isEqualTo("select {pk} from {PunchOutCredential} where lower({domain}) = ?domain and {identity} = ?identity");
	}

	@Test
	public void testGetPunchOutCredentialTooMany()
	{
		final List<PunchOutCredentialModel> result = new ArrayList<>();
		final PunchOutCredentialModel punchOutCredentialModel = new PunchOutCredentialModel();
		result.add(punchOutCredentialModel);
		final PunchOutCredentialModel punchOutCredentialModel2 = new PunchOutCredentialModel();
		result.add(punchOutCredentialModel2);
		doReturn(searchResult).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
		doReturn(result).when(searchResult).getResult();

		assertThatThrownBy(() -> defaultPunchOutCredentialDao.getPunchOutCredential(PUNCHOUT_DOMAIN, PUNCHOUT_USER_ID))
			.isInstanceOf(AmbiguousIdentifierException.class);
	}

	@Test
	public void testGetPunchOutCredentialWithDUNSDomain()
	{
		final List<PunchOutCredentialModel> result = new ArrayList<>();
		final PunchOutCredentialModel punchOutCredentialModel = new PunchOutCredentialModel();
		result.add(punchOutCredentialModel);
		doReturn(true).when(caseInsensitiveDomains).contains("duns");
		ArgumentCaptor<FlexibleSearchQuery> captor = ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		doReturn(searchResult).when(flexibleSearchService).search(captor.capture());
		doReturn(result).when(searchResult).getResult();

		final PunchOutCredentialModel model = defaultPunchOutCredentialDao.getPunchOutCredential(PUNCHOUT_DUNS_DOMAIN,
				PUNCHOUT_USER_ID);
		assertThat(model).isNotNull();
		assertThat(captor.getValue().getQuery()).isEqualTo(
				"select {pk} from {PunchOutCredential} where lower({domain}) = ?domain and lower({identity}) = ?identity");
	}

	@Test
	public void testGetExpiredPunchOutCredentials()
	{
		final List<PunchOutCredentialModel> result = new ArrayList<>();
		final PunchOutCredentialModel punchOutCredentialModel = new PunchOutCredentialModel();
		result.add(punchOutCredentialModel);
		ArgumentCaptor<FlexibleSearchQuery> captor = ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		doReturn(searchResult).when(flexibleSearchService).search(captor.capture());
		doReturn(result).when(searchResult).getResult();
		final List<PunchOutCredentialModel> resList = defaultPunchOutCredentialDao.getExpiredPunchOutCredentials(EXPIRED_DAYS);
		assertThat(resList.size()).isEqualTo(1);
		assertThat(captor.getValue().getQuery()).isEqualTo("select {pk} from {PunchOutCredential as p} where {p:sharedsecretmodifiedtime}<?sharedsecretmodifiedtime");

	}
}
