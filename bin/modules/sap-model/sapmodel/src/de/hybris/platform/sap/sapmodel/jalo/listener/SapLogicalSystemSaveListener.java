/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.jalo.listener;

import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import de.hybris.platform.util.localization.Localization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

public class SapLogicalSystemSaveListener implements
		AfterSaveListener {

	private static final Logger LOGGER = Logger.getLogger(SapLogicalSystemSaveListener.class);

	private FlexibleSearchService flexibleSearchService;

	@Override
	public void afterSave(Collection<AfterSaveEvent> events) {

		if (events.stream()
				.filter(event -> event.getPk().getTypeCode() == SapmodelConstants.SAP_LOGICAL_SYSTEM_TYPE_CODE)
				.findFirst().isPresent()) {

			final List<SAPLogicalSystemModel> sapLogicalSystems = readSapLogicalSystems();

			if (sapLogicalSystems.stream().allMatch(entry -> entry.isDefaultLogicalSystem() == false)) {

				LOGGER.error(Localization
						.getLocalizedString("validation.Saplogicalsystem.DefaultSystemMissing"));
			}

			if (sapLogicalSystems.stream().filter(entry -> entry.isDefaultLogicalSystem() == true).count() > 1L) {

				LOGGER.error(Localization
						.getLocalizedString("validation.Saplogicalsystem.MoreThanOneDefaultSystem"));
			}

		}

	}

	protected List<SAPLogicalSystemModel> readSapLogicalSystems() {

		SearchResult<SAPLogicalSystemModel> querySapLogicalSystems = getFlexibleSearchService()
				.<SAPLogicalSystemModel> search(
						"SELECT {PK} FROM {SAPLogicalSystem} WHERE {sapGlobalConfiguration} IS NOT NULL");

		return querySapLogicalSystems.getResult();

	}

	protected FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

}
