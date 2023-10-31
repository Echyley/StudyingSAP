/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.statistics.collector.commerceservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.basecommerce.model.site.BaseSiteModel
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class SitesStatisticsCollectorSpec extends ServicelayerTransactionalSpockSpecification {
	@Resource
	private ModelService modelService;

	SitesStatisticsCollector collector = new SitesStatisticsCollector();

	@Test
	void "site statistics collector - zero when no sites"() {
		given:
		// no sites

		when:
		def stats = collector.collectStatistics();

		then:
		stats.sites.numIsolatedSites == 0
	}

	@Test
	void "site statistics collector - report only isolated #1"() {
		given:
		createSite("isolated", true)
		createSite("also-isolated", true)
		createSite("foo", false)

		when:
		def stats = collector.collectStatistics()

		then:
		stats.sites.numIsolatedSites == 2
	}

	@Test
	void "site statistics collector - report only isolated #2"() {
		given:
		createSite("not-relevant", false)
		createSite("not-isolated", false)

		when:
		def stats = collector.collectStatistics()

		then:
		stats.sites.numIsolatedSites == 0
	}

	def createSite(String uid, boolean isolated) {
		BaseSiteModel site = modelService.create(BaseSiteModel.class)

		site.uid = uid
		site.dataIsolationEnabled = isolated

		modelService.save(site)
	}
}
