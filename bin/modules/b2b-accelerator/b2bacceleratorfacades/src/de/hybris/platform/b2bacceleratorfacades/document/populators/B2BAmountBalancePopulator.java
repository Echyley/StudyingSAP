/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.document.populators;


import de.hybris.platform.b2bacceleratorservices.company.B2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.company.PastDueBalanceDateRangeService;
import de.hybris.platform.b2bacceleratorservices.document.B2BDocumentDueDateRangePredicate;
import de.hybris.platform.b2bacceleratorservices.document.B2BDocumentPastDuePredicate;
import de.hybris.platform.b2bacceleratorservices.document.NumberOfDayRange;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BAmountBalanceData;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BNumberOfDayRangeData;
import de.hybris.platform.b2bacceleratorfacades.formatters.AmountFormatter;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class B2BAmountBalancePopulator implements Populator<B2BUnitModel, B2BAmountBalanceData>
{

	private B2BDocumentService b2bDocumentService;
	private PastDueBalanceDateRangeService pastDueBalanceDateRangeService;

	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;
	private I18NService i18NService;

	private AmountFormatter amountFormatter;

	@Override
	public void populate(B2BUnitModel source, B2BAmountBalanceData target)
			throws ConversionException {
		final Map<B2BNumberOfDayRangeData, String> rangeMap = new LinkedHashMap<>();
		final List<NumberOfDayRange> ranges = pastDueBalanceDateRangeService.getNumberOfDayRange();

		final SearchResult<B2BDocumentModel> documentsResult = b2bDocumentService.getOpenDocuments(source);
		final List<B2BDocumentModel> documents = documentsResult.getResult();

		for (final NumberOfDayRange dateRange : ranges)
		{
			final B2BNumberOfDayRangeData range = new B2BNumberOfDayRangeData();
			range.setMinBoundery(dateRange.getMinBoundary());
			range.setMaxBoundery(dateRange.getMaxBoundary());
			rangeMap.put(range,
					totalAmount(CollectionUtils.select(documents, new B2BDocumentDueDateRangePredicate(dateRange))));
		}
		target.setDueBalance(rangeMap);
		target.setOpenBalance(totalAmount(documents));
		target.setPastDueBalance(totalAmount(CollectionUtils.select(documents, new B2BDocumentPastDuePredicate())));
		target.setCurrentBalance(totalAmount(CollectionUtils.selectRejected(documents, new B2BDocumentPastDuePredicate())));
	}

	protected String totalAmount(final Collection<B2BDocumentModel> documents)
	{
		CurrencyModel currency = getCommerceCommonI18NService().getCurrentCurrency();
		final LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
		Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(currentLanguage);

		if (locale == null)
		{
			// Fallback to session locale
			locale = getI18NService().getCurrentLocale();
		}

		if (documents != null && !documents.isEmpty())
		{
			currency = documents.iterator().next().getCurrency();

			return amountFormatter.formatAmount(sumOfOpenAmount(documents), currency, locale);
		}
		return amountFormatter.formatAmount(BigDecimal.ZERO, currency, locale);
	}

	protected BigDecimal sumOfOpenAmount(final Collection<B2BDocumentModel> documents)
	{
		BigDecimal total = BigDecimal.ZERO;
		for (final B2BDocumentModel document : documents)
		{
			if (document.getDocumentType().getIncludeInOpenBalance().booleanValue() && document.getOpenAmount() != null)
			{
				total = total.add(document.getOpenAmount());
			}
		}
		return total;
	}

	public B2BDocumentService getB2bDocumentService()
	{
		return b2bDocumentService;
	}

	public void setB2bDocumentService(final B2BDocumentService b2bDocumentService)
	{
		this.b2bDocumentService = b2bDocumentService;
	}

	public PastDueBalanceDateRangeService getPastDueBalanceDateRangeService()
	{
		return pastDueBalanceDateRangeService;
	}

	public void setPastDueBalanceDateRangeService(final PastDueBalanceDateRangeService pastDueBalanceDateRangeService)
	{
		this.pastDueBalanceDateRangeService = pastDueBalanceDateRangeService;
	}

	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public I18NService getI18NService()
	{
		return i18NService;
	}

	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	public AmountFormatter getAmountFormatter()
	{
		return amountFormatter;
	}

	public void setAmountFormatter(final AmountFormatter amountFormatter)
	{
		this.amountFormatter = amountFormatter;
	}

}
