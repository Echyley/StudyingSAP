/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.integration

import de.hybris.platform.searchservices.admin.dao.SnFieldDao
import de.hybris.platform.searchservices.admin.dao.SnIndexTypeDao
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.admin.service.SnCommonConfigurationService
import de.hybris.platform.searchservices.admin.service.SnIndexTypeService
import de.hybris.platform.searchservices.constants.SearchservicesConstants
import de.hybris.platform.searchservices.core.SnException
import de.hybris.platform.searchservices.core.service.SnContextFactory
import de.hybris.platform.searchservices.index.service.SnIndexService
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse
import de.hybris.platform.searchservices.indexer.service.SnIndexerService
import de.hybris.platform.searchservices.indexer.service.impl.TypeSnIndexerItemSource
import de.hybris.platform.searchservices.model.SnFieldModel
import de.hybris.platform.searchservices.model.SnIndexTypeModel
import de.hybris.platform.searchservices.support.CustomSpockRunner
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.util.Config

import java.nio.charset.StandardCharsets

import javax.annotation.Resource

import org.apache.commons.lang3.StringUtils
import org.junit.Assume
import org.junit.runner.RunWith

import spock.lang.Shared

@RunWith(CustomSpockRunner)
abstract class AbstractSnIntegrationSpec extends ServicelayerSpockSpecification {

	protected static final String SEARCH_PROVIDER_KEY = "searchservices.test.integration.searchProvider"

	protected static final String LANGUAGE_EN_ISOCODE = "en"
	protected static final String LANGUAGE_DE_ISOCODE = "de"

	protected static final Locale LOCALE_EN = new Locale(LANGUAGE_EN_ISOCODE)
	protected static final Locale LOCALE_DE = new Locale(LANGUAGE_DE_ISOCODE)

	protected static final String CURRENCY_EUR_ISOCODE = "EUR"
	protected static final String CURRENCY_USD_ISOCODE = "USD"

	protected static final String CATALOG_ID = "hwcatalog"
	protected static final String CATALOG_VERSION_STAGED = "Staged"
	protected static final String CATALOG_VERSION_ONLINE = "Online"

	protected static final String INDEX_CONFIGURATION_ID = "testIndexConfiguration"
	protected static final String INDEX_CONFIGURATION_1_ID = "testIndexConfiguration1"
	protected static final String INDEX_CONFIGURATION_2_ID = "testIndexConfiguration2"

	protected static final String INDEX_TYPE_ID = "testIndexType"
	protected static final String INDEX_TYPE_1_ID = "testIndexType1"
	protected static final String INDEX_TYPE_2_ID = "testIndexType2"

	@Shared
	Closure cleanupClosure

	@Shared
	boolean initialized = false

	@Resource
	CommonI18NService commonI18NService

	@Resource
	SnCommonConfigurationService snCommonConfigurationService

	@Resource
	SnIndexTypeService snIndexTypeService

	@Resource
	SnIndexService snIndexService

	@Resource
	SnIndexerService snIndexerService

	@Resource
	SnIndexTypeDao snIndexTypeDao

	@Resource
	SnFieldDao snFieldDao

	@Resource
	ModelService modelService

	@Resource
	SnContextFactory snContextFactory

	def setup() {
		String searchProvider = Config.getString(SEARCH_PROVIDER_KEY, null)
		Assume.assumeTrue(StringUtils.isNotBlank(searchProvider))

		initializeSession()

		if (!initialized) {
			cleanupClosure = this.&cleanupSpecWithSpring
			setupSpecWithSpring()
			initialized = true
		}
	}

	def setupSpecWithSpring() {
	}

	def cleanupSpec() {
		cleanupClosure?.run()
	}

	def cleanupSpecWithSpring() {
	}

	def initializeSession() {
		getOrCreateLanguage(LANGUAGE_EN_ISOCODE)
		setCurrentLanguage(LANGUAGE_EN_ISOCODE)

		getOrCreateCurrency(CURRENCY_EUR_ISOCODE)
		setCurrentCurrency(CURRENCY_EUR_ISOCODE)
	}

	def setCurrentLanguage(String isocode) {
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(isocode))
	}

	def setCurrentCurrency(String isocode) {
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency(isocode))
	}

	def applyLanguages(List<String> languageIds) {
		if (languageIds) {
			String languageId = languageIds.get(0)
			if (languageId) {
				setCurrentLanguage(languageId);
			}
		}
	}

	def applyQualifiers(Map<String, String> qualifiers) {
		if (qualifiers) {
			String currencyId = qualifiers.get(SearchservicesConstants.CURRENCY_QUALIFIER_TYPE);
			if (currencyId) {
				setCurrentCurrency(currencyId);
			}
		}
	}

	def createTestData() {
		String searchProvider = Config.getString(SEARCH_PROVIDER_KEY, null)
		importData("/impex/test/searchprovider-${searchProvider}.impex", StandardCharsets.UTF_8.name())
	}

	def deleteTestData() {
		List<SnIndexType> indexTypes = snIndexTypeService.getAllIndexTypes()
		for (SnIndexType indexType : indexTypes) {
			if (StringUtils.isNotBlank(indexType.getIndexConfigurationId())) {
				try {
					// TBD it should be possible to get the list of indexes for a given search provider
					String indexId = snIndexService.getDefaultIndexId(indexType.getId())
					snIndexService.deleteIndexForId(indexType.getId(), indexId)
				}
				catch (final SnException e) {
					// empty
				}
			}
		}
	}

	def executeFullIndexerOperation(String indexTypeId) {
		final SnIndexerItemSource itemSource = new TypeSnIndexerItemSource()
		final SnIndexerRequest indexerRequest = snIndexerService.createFullIndexerRequest(indexTypeId, itemSource)
		final SnIndexerResponse indexerResponse = snIndexerService.index(indexerRequest)
	}

	void resetFields(fieldModifier) {
		SnIndexTypeModel indexType = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_ID).orElseThrow()
		List<SnFieldModel> fields = snFieldDao.findFieldsByIndexType(indexType)

		for (SnFieldModel field : fields) {
			fieldModifier(field)
			modelService.save(field)
		}
	}

	def patchField(String id, def patch) {
		SnIndexTypeModel indexType = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_ID).orElseThrow()
		SnFieldModel field = snFieldDao.findFieldByIndexTypeAndId(indexType, id).orElseThrow()

		for (def entry : patch.entrySet()) {
			field[entry.key] = entry.value
		}

		modelService.save(field)
	}

	def exportConfiguration() {
		snCommonConfigurationService.exportConfiguration(INDEX_CONFIGURATION_ID)
	}

	List<SnFieldModel> getFields() {
		SnIndexTypeModel indexType = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_ID).orElseThrow()
		return snFieldDao.findFieldsByIndexType(indexType)
	}
}
