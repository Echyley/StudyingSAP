/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.catalog.jalo.synchronization;

import static org.mockito.ArgumentMatchers.nullable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.SynchronizationPersistenceAdapter;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.ComposedType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class GenericCatalogCopyContextTest
{
	private GenericCatalogCopyContext context;

	@Mock
	private SessionContext ctxMock;

	@Mock
	private CatalogVersionSyncJob syncJob;

	@Mock
	private CatalogVersionSyncCronJob syncCronJob;

	@Mock
	private FlexibleSearch flexibleSerachMock;

	@Mock
	private JaloSession jaloSessionMock;

	@Mock
	private Language langMock;

	@Mock
	private SearchResult searchResultMock;

	@Mock
	private ComposedType dependentMock;

	@Mock
	private AttributeDescriptor attributeDescriptorMock;

	@Mock
	private ComposedType targetTypeMock;

	private static final String DEFAULT_TARGET_TYPE_CODE = "12345";
	private static final String DEFAULT_ATTRIBUTE_NAME = "attributeName";
	private static final Boolean DEFAULT_LOCALIZED = false;

	public class TestGenericCatalogCopyContext extends GenericCatalogCopyContext
	{
		public TestGenericCatalogCopyContext(final SessionContext ctx, final CatalogVersionSyncJob job,
		                                     final CatalogVersionSyncCronJob cronJob, final Level logLevel,
		                                     final boolean forceUpdate)
		{
			super(ctx, job, cronJob, logLevel, forceUpdate);
		}

		@Override
		protected int getCurrentTurn()
		{
			return 0;
		}

		@Override
		protected JaloSession createSession()
		{
			return jaloSessionMock;
		}

		@Override
		protected Set<AttributeDescriptor> getCreationAttributes()
		{
			return Collections.EMPTY_SET;
		}

		@Override
		protected void setTargetLanguages(final java.util.Collection<Language> languages)
		{
			//noop
		}

		@Override
		protected String getRootCatalogType(final ComposedType composedType)
		{
			return "mou";
		}

		@Override
		protected SynchronizationPersistenceAdapter initPersistenceAdapter()
		{
			return null;
		}

		//
	}

	@Before
	public void prepare()
	{
		Mockito.when(syncJob.getSyncLanguages(ctxMock)).thenReturn(Collections.singleton(langMock));

		context = Mockito.spy(new TestGenericCatalogCopyContext(ctxMock, syncJob, syncCronJob, Level.OFF, true));
		Mockito.doReturn(flexibleSerachMock).when(context).getFlexibleSearch();

	}

	@Test
	public void testQueryCatalogItemCopy()
	{
		final Item itemMock = Mockito.mock(Item.class);

		Mockito.doReturn("ennie-minnie").when(context).getCatalogVersionAttribute(nullable(ComposedType.class));
		//,mine,mou
		Mockito.doReturn(Collections.singleton("minnie")).when(context).getUniqueKeyAttributes(nullable(ComposedType.class));

		Mockito.when(
				flexibleSerachMock.search(Mockito.any(SessionContext.class) /* ctxMock */, Mockito.anyString(), Mockito.anyMap(),
						Mockito.any(Class.class))).thenReturn(searchResultMock);

		context.queryCatalogItemCopy(itemMock);


		Mockito.verify(flexibleSerachMock, Mockito.times(1)).search(Mockito.any(SessionContext.class),
				(ArgumentMatchers.argThat(new ArgumentMatcher<String>()
				{
					@Override
					public boolean matches(final String query)
					{
						Assert.assertEquals(
								"SELECT x.PK FROM ({{ SELECT {counterpart.pk} AS PK FROM {mou as counterpart LEFT JOIN ItemSyncTimestamp as ts ON {ts.targetItem} = {counterpart.PK} AND{ts.targetVersion}=?tgtVer AND {ts.syncJob}=0 }WHERE ({ts.PK} IS NULL OR {ts.sourceItem}=?srcItem) AND   {counterpart.ennie-minnie}=?tgtVer  AND {counterpart.minnie} IS NULL }} UNION ALL {{ SELECT {pk} AS PK FROM {ItemSyncTimestamp*} WHERE {sourceItem}=?srcItem AND {targetVersion}=?tgtVer AND {syncJob}=0  }} ) x",
								query);
						return true;
					}
				})), Mockito.anyMap(), Mockito.any(Class.class));
	}


	@Test
	public void testQueryNonCatalogItemCopy()
	{
		final Item itemMock = Mockito.mock(Item.class);

		Mockito.lenient().doReturn("ennie-minnie").when(context).getCatalogVersionAttribute(nullable(ComposedType.class));
		//,mine,mou
		Mockito.doReturn(Collections.singleton("minnie")).when(context)
		       .getNonCatalogItemUniqueAttributes(nullable(ComposedType.class));

		Mockito.doReturn("whou").when(context).getRootUniqueItemType(nullable(ComposedType.class), Mockito.anySet());


		Mockito.when(
				flexibleSerachMock.search(Mockito.any(SessionContext.class) /* ctxMock */, Mockito.anyString(), Mockito.anyMap(),
						Mockito.any(Class.class))).thenReturn(searchResultMock);

		context.queryNonCatalogItemCopy(itemMock);

		Mockito.verify(flexibleSerachMock).search(Mockito.any(SessionContext.class),
				Mockito
						.eq("SELECT {pk} FROM {ItemSyncTimestamp*}WHERE {syncJob}=0 AND {targetVersion}=?tgtVer AND {sourceItem}=?srcItem"),
				Mockito.anyMap(), Mockito.any(Class.class));
		Mockito.verify(flexibleSerachMock).search(Mockito.any(SessionContext.class),
				Mockito.eq(
						"SELECT {counterpart.pk} AS PK FROM {whou AS counterpart LEFT JOIN ItemSyncTimestamp as ts ON {ts.targetItem} = {counterpart.PK}  AND{ts.targetVersion}=?tgtVer AND {ts.syncJob}=0 }WHERE ({ts.PK} IS NULL OR {ts.sourceItem}=?srcItem) AND  {counterpart.minnie} IS NULL "),
				Mockito.anyMap(),
				Mockito.any(Class.class));
	}

	@Test
	public void testGetParentWhenFetchedMultiParent()
	{
		Mockito.when(targetTypeMock.getCode()).thenReturn(DEFAULT_TARGET_TYPE_CODE);
		Mockito.when(flexibleSerachMock.search(Mockito.any(SessionContext.class), Mockito.eq(
				"SELECT DISTINCT {PK} FROM {" + DEFAULT_TARGET_TYPE_CODE + " AS t} WHERE {t:" + DEFAULT_ATTRIBUTE_NAME + (
						DEFAULT_LOCALIZED ?
								":any" :
								"") + "}=?i"), Mockito.anyMap(), Mockito.any(Class.class))).thenReturn(searchResultMock);

		final List<Item> resultItems = createTestResultItemList(2);
		Mockito.when(searchResultMock.getResult()).thenReturn(resultItems);

		final Item itemMock = Mockito.mock(Item.class);

		final GenericCatalogCopyContext.ReverseReferenceParentResolver parentResolver = context.new ReverseReferenceParentResolver(
				dependentMock, attributeDescriptorMock, targetTypeMock, DEFAULT_ATTRIBUTE_NAME, false);

		Assert.assertNull(parentResolver.getParent(itemMock));
	}

	private List<Item> createTestResultItemList(final int size)
	{
		final List<Item> result = new ArrayList<>();

		for (int i = 0; i < size; i++)
		{
			final Item itemMock = Mockito.mock(Item.class);
			Mockito.when(itemMock.toString()).thenReturn("item_" + i);
			result.add(itemMock);
		}

		return result;
	}
}
