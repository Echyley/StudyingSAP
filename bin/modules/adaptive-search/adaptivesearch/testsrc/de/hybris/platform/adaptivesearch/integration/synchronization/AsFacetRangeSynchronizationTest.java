/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsFacetRangeModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsFacetRangeSynchronizationTest extends AbstractAsSynchronizationTest
{
	private static final String CATALOG_ID = "hwcatalog";
	private static final String VERSION_STAGED = "Staged";
	private static final String VERSION_ONLINE = "Online";

	private static final String FACET_CONFIGURATION_UID = "facet";

	private static final String UID1 = "966cac76-0b8d-4e5b-944f-20fc7b0c778c";

	private static final String ID1 = "id1";
	private static final String ID2 = "id2";

	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";

	private static final String VALUE1 = "1";
	private static final String VALUE2 = "2";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AbstractAsFacetConfigurationModel facetConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", StandardCharsets.UTF_8.name());
		importCsv("/adaptivesearch/test/integration/asSimpleSearchProfile.impex", StandardCharsets.UTF_8.name());
		importCsv("/adaptivesearch/test/integration/asSimpleSearchConfiguration.impex", StandardCharsets.UTF_8.name());
		importCsv("/adaptivesearch/test/integration/asFacets.impex", StandardCharsets.UTF_8.name());

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsFacetConfigurationModel> facetConfigurationOptional = asConfigurationService
				.getConfigurationForUid(AbstractAsFacetConfigurationModel.class, stagedCatalogVersion, FACET_CONFIGURATION_UID);
		facetConfiguration = facetConfigurationOptional.orElseThrow();
	}

	@Test
	public void facetRangeNotFoundBeforeSynchronization()
	{
		// given
		final AsFacetRangeModel facetRange = asConfigurationService.createConfiguration(AsFacetRangeModel.class);
		facetRange.setCatalogVersion(stagedCatalogVersion);
		facetRange.setUid(UID1);
		facetRange.setFacetConfiguration(facetConfiguration);
		facetRange.setId(ID1);
		facetRange.setName(NAME1);
		facetRange.setFrom(VALUE1);
		facetRange.setTo(VALUE2);

		// when
		asConfigurationService.saveConfiguration(facetRange);

		final Optional<AsFacetRangeModel> synchronizedFacetRangeOptional = asConfigurationService
				.getConfigurationForUid(AsFacetRangeModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedFacetRangeOptional.isPresent());
	}

	@Test
	public void synchronizeNewFacetRange()
	{
		// given
		final AsFacetRangeModel facetRange = asConfigurationService.createConfiguration(AsFacetRangeModel.class);
		facetRange.setCatalogVersion(stagedCatalogVersion);
		facetRange.setUid(UID1);
		facetRange.setFacetConfiguration(facetConfiguration);
		facetRange.setId(ID1);
		facetRange.setName(NAME1);
		facetRange.setFrom(VALUE1);
		facetRange.setTo(VALUE2);

		// when
		asConfigurationService.saveConfiguration(facetRange);
		modelService.refresh(facetRange);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsFacetRangeModel> synchronizedFacetRangeOptional = asConfigurationService
				.getConfigurationForUid(AsFacetRangeModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedFacetRangeOptional.isPresent());

		final AsFacetRangeModel synchronizedFacetRange = synchronizedFacetRangeOptional.orElseThrow();
		assertFalse(synchronizedFacetRange.isCorrupted());
		assertSynchronized(facetRange, synchronizedFacetRange, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedFacetRange()
	{
		// given
		final AsFacetRangeModel facetRange = asConfigurationService.createConfiguration(AsFacetRangeModel.class);
		facetRange.setCatalogVersion(stagedCatalogVersion);
		facetRange.setUid(UID1);
		facetRange.setFacetConfiguration(facetConfiguration);
		facetRange.setId(ID1);
		facetRange.setName(NAME1);
		facetRange.setFrom(VALUE1);
		facetRange.setTo(VALUE2);

		// when
		asConfigurationService.saveConfiguration(facetRange);
		modelService.refresh(facetRange);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		facetRange.setId(ID2);
		facetRange.setName(NAME2);

		asConfigurationService.saveConfiguration(facetRange);
		modelService.refresh(facetRange);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsFacetRangeModel> synchronizedFacetRangeOptional = asConfigurationService
				.getConfigurationForUid(AsFacetRangeModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedFacetRangeOptional.isPresent());

		final AsFacetRangeModel synchronizedFacetRange = synchronizedFacetRangeOptional.orElseThrow();
		assertFalse(synchronizedFacetRange.isCorrupted());
		assertSynchronized(facetRange, synchronizedFacetRange, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedFacetRange()
	{
		// given
		final AsFacetRangeModel facetRange = asConfigurationService.createConfiguration(AsFacetRangeModel.class);
		facetRange.setCatalogVersion(stagedCatalogVersion);
		facetRange.setUid(UID1);
		facetRange.setFacetConfiguration(facetConfiguration);
		facetRange.setId(ID1);
		facetRange.setName(NAME1);
		facetRange.setFrom(VALUE1);
		facetRange.setTo(VALUE2);

		// when
		asConfigurationService.saveConfiguration(facetRange);
		modelService.refresh(facetRange);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(facetRange);

		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsFacetRangeModel> synchronizedFacetRangeOptional = asConfigurationService
				.getConfigurationForUid(AsFacetRangeModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedFacetRangeOptional.isPresent());
	}
}
