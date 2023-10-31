/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.AdditionalInvalidationData;
import de.hybris.platform.cache.AdditionalInvalidationData.Builder;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.cache.InvalidationKey;
import de.hybris.platform.cache.InvalidationListener;
import de.hybris.platform.cache.InvalidationManager;
import de.hybris.platform.cache.InvalidationTarget;
import de.hybris.platform.cache.InvalidationTopic;
import de.hybris.platform.cache.RemoteInvalidationSource;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.persistence.EJBInvalidParameterException;
import de.hybris.platform.persistence.EJBItemNotFoundException;
import de.hybris.platform.persistence.SystemEJB;
import de.hybris.platform.persistence.link.LinkHome;
import de.hybris.platform.persistence.link.LinkRemote;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.util.jeeapi.YCreateException;
import de.hybris.platform.util.jeeapi.YRemoveException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@IntegrationTest
public class ManyToManyRelationCachingIntegrationTest extends ServicelayerBaseTest
{
	private static final String PG_REL_NAME = "PrincipalGroupRelation";
	private static int PG_REL_TYPE_CODE;
	private static PK PG_REL_TYPE_PK;

	private final PropertyConfigSwitcher cacheEnabled = new PropertyConfigSwitcher("relation.cache.enabled");
	private final PropertyConfigSwitcher cachePgRelEnabled = new PropertyConfigSwitcher(
			"relation.cache." + PG_REL_NAME + ".enabled");

	@Resource
	ModelService modelService;
	@Resource
	FlexibleSearchService flexibleSearchService;

	private CapturingInvalidationListener capturingListener = null;

	@BeforeClass
	public static void intiTypeSystemData()
	{
		final ComposedType pgRelType = TypeManager.getInstance().getComposedType(PG_REL_NAME);
		PG_REL_TYPE_CODE = pgRelType.getItemTypeCode();
		PG_REL_TYPE_PK = pgRelType.getPK();
	}

	@Before
	public void setUp()
	{
		capturingListener = new CapturingInvalidationListener(PG_REL_TYPE_CODE);
		final InvalidationTopic topic = InvalidationManager.getInstance().getOrCreateInvalidationTopic(new String[]
				{ Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY });
		topic.addInvalidationListener(capturingListener);
	}

	@After
	public void tearDown()
	{
		final InvalidationTopic topic = InvalidationManager.getInstance().getOrCreateInvalidationTopic(new String[]
				{ Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY });
		topic.removeInvalidationListener(capturingListener);
		capturingListener.clear();
		capturingListener = null;
		cacheEnabled.switchBackToDefault();
		cachePgRelEnabled.switchBackToDefault();
	}

	@Test
	public void testModelServiceScenarioWhenCachingIsEnabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("true");

		testModelServiceScenarioWithoutTransaction(this::pgRelInvalidation);
	}

	@Test
	public void testModelServiceScenarioWhenCachingForParticularRelationIsEnabledButGlobalCachingIsDisabled()
	{
		cacheEnabled.switchToValue("false");
		cachePgRelEnabled.switchToValue("true");

		testModelServiceScenarioWithoutTransaction((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	@Test
	public void testModelServiceScenarioWhenCachingForParticularRelationIsDisabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("false");

		testModelServiceScenarioWithoutTransaction((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	@Test
	public void testDirectLinkManipulationWithoutTransactionScenarioWhenCachingIsEnabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("true");

		testDirectLinkManipulationWithoutTransactionScenario(this::pgRelInvalidation);
	}

	@Test
	public void testDirectLinkManipulationWithoutTransactionScenarioWhenCachingForParticularRelationIsEnabledButGlobalCachingIsDisabled()
	{
		cacheEnabled.switchToValue("false");
		cachePgRelEnabled.switchToValue("true");

		testDirectLinkManipulationWithoutTransactionScenario((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	@Test
	public void testDirectLinkManipulationWithoutTransactionScenarioWhenCachingForParticularRelationIsDisabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("false");

		testDirectLinkManipulationWithoutTransactionScenario((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	@Test
	public void testDirectLinkManipulationWithinTransactionScenarioWhenCachingIsEnabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("true");

		testDirectLinkManipulationWithinTransactionScenario(this::pgRelInvalidation);
	}

	@Test
	public void testDirectLinkManipulationWithinTransactionScenarioWhenCachingForParticularRelationIsEnabledButGlobalCachingIsDisabled()
	{
		cacheEnabled.switchToValue("false");
		cachePgRelEnabled.switchToValue("true");

		testDirectLinkManipulationWithinTransactionScenario((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	@Test
	public void testDirectLinkManipulationWithinTransactionScenarioWhenCachingForParticularRelationIsDisabled()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("false");

		testDirectLinkManipulationWithinTransactionScenario((linkPk, ignored) -> pgRelInvalidation(linkPk));
	}

	public void testDirectLinkManipulationWithinTransactionScenario(final ServiceLayerInvalidationCreator inv)
	{
		final UserModel u1 = newUser();
		final UserModel u2 = newUser();
		final UserModel u3 = newUser();
		final UserGroupModel g1 = newGroup();
		final UserGroupModel g2 = newGroup();
		final UserGroupModel g3 = newGroup();
		u1.setGroups(Set.of(g1, g2));
		u2.setGroups(Set.of(g1, g2));
		modelService.saveAll();
		capturingListener.clear();

		//remove existing link
		{
			final LinkRemote linkU1G1 = getLinkRemote(requireExistingPgRel(u1, g1));
			doWithinTransaction(() ->
			{
				removeLinkRemote(linkU1G1);
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G1.getPK(), u1, g1));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G1.getPK(), u1, g1));
			capturingListener.clear();
		}


		//modify target on existing link
		{
			final LinkRemote linkU1G2 = getLinkRemote(requireExistingPgRel(u1, g2));
			doWithinTransaction(() ->
			{
				linkU1G2.setTargetPK(g1.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G2.getPK(), u1, g2, u1, g1));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G2.getPK(), u1, g2, u1, g1));
			capturingListener.clear();
		}

		//modify source on existing item
		{
			final LinkRemote linkU2G2 = getLinkRemote(requireExistingPgRel(u2, g2));
			doWithinTransaction(() ->
			{
				linkU2G2.setSourcePK(u1.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2.getPK(), u2, g2, u1, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2.getPK(), u2, g2, u1, g2));
			capturingListener.clear();
		}

		//modify source and target on existing item
		{
			final LinkRemote linkU1G1 = getLinkRemote(requireExistingPgRel(u1, g1));
			doWithinTransaction(() ->
			{
				linkU1G1.setSourcePK(u3.getPk());
				linkU1G1.setTargetPK(g3.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G1.getPK(), u1, g1, u3, g3));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G1.getPK(), u1, g1, u3, g3));
			capturingListener.clear();
		}

		//create new link
		{
			final AtomicReference<PK> linkU2G2Pk = new AtomicReference<>();
			doWithinTransaction(() ->
			{
				final LinkRemote linkU2G2 = createLinkRemote(u2, g2);
				linkU2G2Pk.set(linkU2G2.getPK());
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2.getPK(), u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2Pk.get(), u2, g2));
			capturingListener.clear();
		}

		final PK nullTestLinkPk = getLinkRemote(requireExistingPgRel(u2, g2)).getPK();
		//set target to null
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setTargetPK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}

		//set target to null when it's already a null
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setTargetPK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).isEmpty();

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set target back to non null value
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setTargetPK(g2.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}

		//set source to null
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}

		//set source to null when it's already a null
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).isEmpty();

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set source back to non null value
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(u2.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}

		//set source and target to null
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(null);
				link.setTargetPK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}

		//set source and target to null once again
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(null);
				link.setTargetPK(null);
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).isEmpty();

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set source and target back to non null values
		{
			doWithinTransaction(() ->
			{
				final LinkRemote link = getLinkRemote(nullTestLinkPk);
				link.setSourcePK(u2.getPk());
				link.setTargetPK(g2.getPk());
				Transaction.current().flushDelayedStore();
				final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
				assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));

				final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
				assertThat(outsideTx).isEmpty();
				capturingListener.clear();
			});
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(nullTestLinkPk, u2, g2));
			capturingListener.clear();
		}
	}

	private void testDirectLinkManipulationWithoutTransactionScenario(final ServiceLayerInvalidationCreator inv)
	{
		final UserModel u1 = newUser();
		final UserModel u2 = newUser();
		final UserGroupModel g1 = newGroup();
		final UserGroupModel g2 = newGroup();
		u1.setGroups(Set.of(g1, g2));
		u2.setGroups(Set.of(g1, g2));
		modelService.saveAll();
		capturingListener.clear();

		//remove existing link
		{
			final LinkRemote linkU1G1 = getLinkRemote(requireExistingPgRel(u1, g1));
			removeLinkRemote(linkU1G1);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G1.getPK(), u1, g1));
			capturingListener.clear();
		}

		//modify target on existing link
		{
			final LinkRemote linkU1G2 = getLinkRemote(requireExistingPgRel(u1, g2));
			linkU1G2.setTargetPK(g1.getPk());
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G2.getPK(), u1, g2, u1, g1));
			capturingListener.clear();
		}

		//modify source on existing item
		{
			final LinkRemote linkU2G2 = getLinkRemote(requireExistingPgRel(u2, g2));
			linkU2G2.setSourcePK(u1.getPk());
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2.getPK(), u2, g2, u1, g2));
			capturingListener.clear();
		}

		//create new link
		{
			final LinkRemote linkU2G2 = createLinkRemote(u2, g2);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU2G2.getPK(), u2, g2));
			capturingListener.clear();
		}

		final PK nullTestLinkPk = getLinkRemote(requireExistingPgRel(u2, g2)).getPK();
		//set target to null
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setTargetPK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));
			capturingListener.clear();
		}

		//set target to null when it's already a null
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setTargetPK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set target back to non null value
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setTargetPK(g2.getPk());
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));
			capturingListener.clear();
		}

		//set source to null
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));
			capturingListener.clear();
		}

		//set source to null when it's already a null
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set source back to non null value
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(u2.getPk());
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(link.getPK(), u2, g2));
			capturingListener.clear();
		}

		//set source and target to null
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(null);
			link.setTargetPK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactly(
					inv.createInvalidation(link.getPK(), u2, g2),
					inv.createInvalidation(link.getPK(), null, g2));
			capturingListener.clear();
		}

		//set source and target to null once again
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(null);
			link.setTargetPK(null);
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).isEmpty();
			capturingListener.clear();
		}

		//set source and target back to non null values
		{
			final LinkRemote link = getLinkRemote(nullTestLinkPk);
			link.setSourcePK(u2.getPk());
			link.setTargetPK(g2.getPk());
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).isEmpty();

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactly(
					inv.createInvalidation(link.getPK(), u2, null),
					inv.createInvalidation(link.getPK(), u2, g2));
			capturingListener.clear();
		}
	}

	private void testModelServiceScenarioWithoutTransaction(final ServiceLayerInvalidationCreator inv)
	{
		final UserModel u1 = newUser();
		final UserModel u2 = newUser();
		final UserModel u3 = newUser();
		final UserGroupModel g1 = newGroup();
		final UserGroupModel g2 = newGroup();
		final UserGroupModel g3 = newGroup();
		u1.setGroups(Set.of(g1, g2));
		u2.setGroups(Set.of(g1, g2));
		modelService.saveAll();
		final PK linkU1G1 = requireExistingPgRel(u1, g1);
		final PK linkU1G2 = requireExistingPgRel(u1, g2);
		final PK linkU2G1 = requireExistingPgRel(u2, g1);
		final PK linkU2G2 = requireExistingPgRel(u2, g2);

		//Initial setup was created, assert that all invalidations were sent
		{
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU1G1, u1, g1),
					inv.createInvalidation(linkU1G2, u1, g2),
					inv.createInvalidation(linkU2G1, u2, g1),
					inv.createInvalidation(linkU2G2, u2, g2));

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU1G1, u1, g1),
					inv.createInvalidation(linkU1G2, u1, g2),
					inv.createInvalidation(linkU2G1, u2, g1),
					inv.createInvalidation(linkU2G2, u2, g2));
			capturingListener.clear();
		}

		//Remove u1 from g2
		u1.setGroups(Set.of(g1));
		modelService.saveAll();

		//The linkU1G2 (removed) should be invalidated
		{
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G2, u1, g2));

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU1G2, u1, g2));
			capturingListener.clear();
		}

		//Move u2 from g1 to g3
		u2.setGroups(Set.of(g2, g3));
		modelService.saveAll();
		final PK linkU2G3 = requireExistingPgRel(u2, g3);

		//The linkU2G1 (removed) and linkU2G3 (created) should be invalidated
		{
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU2G1, u2, g1),
					inv.createInvalidation(linkU2G3, u2, g3));

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU2G1, u2, g1),
					inv.createInvalidation(linkU2G3, u2, g3));
			capturingListener.clear();
		}

		//Add u3 to g3
		g3.setMembers(Set.of(u2, u3));
		modelService.saveAll();
		final PK linkU3G3 = requireExistingPgRel(u3, g3);

		//The linkU3G3 (created) should be invalidated
		{
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU3G3, u3, g3));

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(inv.createInvalidation(linkU3G3, u3, g3));
			capturingListener.clear();
		}

		//Remove all users from g3
		g3.setMembers(Set.of());
		modelService.saveAll();

		//The linkU2G3 and linkU3G3 are removed so invalidations for both of them are expected
		{
			final List<Invalidation> insideTx = capturingListener.getCapturedInvalidationsInsideTransaction();
			assertThat(insideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU2G3, u2, g3),
					inv.createInvalidation(linkU3G3, u3, g3));

			final List<Invalidation> outsideTx = capturingListener.getCapturedInvalidationsOutsideTransaction();
			assertThat(outsideTx).containsExactlyInAnyOrder(
					inv.createInvalidation(linkU2G3, u2, g3),
					inv.createInvalidation(linkU3G3, u3, g3));
			capturingListener.clear();
		}

	}

	private void doWithinTransaction(final Runnable r)
	{
		final Transaction tx = Transaction.current();
		assertThat(tx.isRunning()).isFalse();
		try
		{
			tx.execute(new TransactionBody()
			{
				@Override
				public Object execute()
				{
					r.run();
					return null;
				}
			});
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Unexpected exception.", e);
		}
	}

	private Invalidation pgRelInvalidation(final PK linkPk, final PrincipalModel... principals)
	{
		if (principals == null || principals.length == 0)
		{
			return new Invalidation(InvalidationKey.entityArrayKey(linkPk));
		}
		if ((principals.length & 1) == 1)
		{
			throw new IllegalArgumentException("length must be even.");
		}

		final Builder builder = AdditionalInvalidationData.builder().addForeignKey(LinkModel.ITEMTYPE, PG_REL_TYPE_PK);
		for (int i = 0; i < principals.length; i += 2)
		{
			final UserModel u = (UserModel) principals[i];
			final UserGroupModel g = (UserGroupModel) principals[i + 1];
			if (u != null)
			{
				builder.addForeignKey(LinkModel.SOURCE, u.getPk());
			}
			if (g != null)
			{
				builder.addForeignKey(LinkModel.TARGET, g.getPk());
			}
		}
		return new Invalidation(InvalidationKey.entityArrayKey(linkPk, builder.build()));
	}

	private LinkRemote createLinkRemote(final UserModel u, final UserGroupModel g)
	{
		final LinkHome pgRelHome = (LinkHome) Registry.getCurrentTenant()
		                                              .getPersistencePool()
		                                              .getHomeProxy(PG_REL_TYPE_CODE);

		try
		{
			return pgRelHome.create(PG_REL_NAME, u.getPk(), g.getPk(), 0, 0);
		}
		catch (final YCreateException | EJBInvalidParameterException e)
		{
			throw new IllegalStateException("Unexpected exception", e);
		}
	}

	private void removeLinkRemote(final LinkRemote link)
	{
		try
		{
			link.remove();
		}
		catch (final YRemoveException e)
		{
			throw new IllegalStateException("Unexpected exception", e);
		}
	}

	private LinkRemote getLinkRemote(final PK linkPk)
	{
		try
		{
			return (LinkRemote) SystemEJB.getInstance().findRemoteObjectByPK(linkPk);
		}
		catch (final EJBItemNotFoundException | EJBInvalidParameterException e)
		{
			throw new IllegalStateException("Unexpected exception", e);
		}
	}

	private PK requireExistingPgRel(final UserModel u, final UserGroupModel g)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"select {PK} from {PrincipalGroupRelation} where {source}=?s and {target}=?t",
				Map.of("s", u.getPk(), "t", g.getPk()));
		query.setResultClassList(List.of(PK.class));

		final List<PK> pks = flexibleSearchService.<PK>search(query).getResult();
		assertThat(pks).isNotNull().isNotEmpty().hasSize(1);

		final PK pk = pks.get(0);
		assertThat(pk).isNotNull().isNotEqualTo(PK.NULL_PK).isNotEqualTo(PK.BIG_PK);
		assertThat(pk.getTypeCode()).isEqualTo(PG_REL_TYPE_CODE);

		return pk;
	}

	private UserModel newUser()
	{
		final UserModel user = modelService.create(UserModel.class);

		user.setUid(unique());

		return user;
	}

	private UserGroupModel newGroup()
	{
		final UserGroupModel userGroup = modelService.create(UserGroupModel.class);

		userGroup.setUid(unique());

		return userGroup;
	}

	private String unique()
	{
		return UUID.randomUUID().toString();
	}

	@FunctionalInterface
	private interface ServiceLayerInvalidationCreator
	{
		Invalidation createInvalidation(final PK linkPk, final PrincipalModel... principals);
	}

	private static class Invalidation
	{
		private final Object[] key;
		private final int typeCode;

		public Invalidation(final Object[] key)
		{
			this.typeCode = InvalidationKey.requireEntityArrayKey(key).getTypeCode();
			this.key = key;
		}

		public int getTypeCode()
		{
			return typeCode;
		}

		@Override
		public String toString()
		{
			return Arrays.toString(key);
		}

		@Override
		public boolean equals(final Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			final Invalidation that = (Invalidation) o;
			return Arrays.equals(key, that.key);
		}

		@Override
		public int hashCode()
		{
			return Arrays.hashCode(key);
		}
	}

	private static class CapturingInvalidationListener implements InvalidationListener
	{
		private final int typeCodeToCapture;
		private final List<Invalidation> capturedInsideTransaction = new ArrayList<>();
		private final List<Invalidation> capturedOutsideTransaction = new ArrayList<>();

		private CapturingInvalidationListener(final int typeCodeToCapture)
		{
			this.typeCodeToCapture = typeCodeToCapture;
		}

		@Override
		public void keyInvalidated(final Object[] key, final int invalidationType, final InvalidationTarget target,
		                           final RemoteInvalidationSource remoteSrc)
		{
			final Invalidation inv = new Invalidation(key);
			if (inv.getTypeCode() == typeCodeToCapture)
			{
				(target instanceof Cache ? capturedOutsideTransaction : capturedInsideTransaction).add(inv);
			}
		}

		public void clear()
		{
			capturedInsideTransaction.clear();
			capturedOutsideTransaction.clear();
		}

		public List<Invalidation> getCapturedInvalidationsInsideTransaction()
		{
			return List.copyOf(capturedInsideTransaction);
		}

		public List<Invalidation> getCapturedInvalidationsOutsideTransaction()
		{
			return List.copyOf(capturedOutsideTransaction);
		}
	}
}