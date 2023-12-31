/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.company.populators;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import de.hybris.platform.b2bacceleratorservices.constants.B2BAccountSummaryConstants;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentData;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentTypeData;
import de.hybris.platform.b2bacceleratorfacades.document.data.MediaData;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentPayableOrUsable;
import de.hybris.platform.b2bacceleratorfacades.formatters.AmountFormatter;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

public class B2BDocumentPopulator implements Populator<B2BDocumentModel, B2BDocumentData>
{
	private Converter<CurrencyModel, CurrencyData>	currencyConverter;
	private Converter<MediaModel, MediaData>			mediaConverter;
	private AmountFormatter								amountFormatter;
	private CommerceCommonI18NService					commerceCommonI18NService;
	private CommonI18NService								commonI18NService;
	private I18NService										i18NService;

	@Override
	public void populate( final B2BDocumentModel source, final B2BDocumentData target ) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final Locale locale = getLocale();
		final B2BDocumentTypeModel documentTypeModel = source.getDocumentType();

		target.setDocumentNumber(source.getDocumentNumber());
		target.setAmount(source.getAmount());
		target.setFormattedAmount(amountFormatter.formatAmount(source.getAmount(), source.getCurrency(), locale));
		target.setOpenAmount(source.getOpenAmount());
		target.setFormattedOpenAmount(amountFormatter.formatAmount(source.getOpenAmount(), source.getCurrency(), locale));
		target.setDate(source.getDate());
		target.setDueDate(source.getDueDate());
		target.setDocumentType(getB2BDocumentType(documentTypeModel));
		target.setSelectable(isSelectable(documentTypeModel, source.getOpenAmount()));
		target.setCurrency(currencyConverter.convert(source.getCurrency()));
		target.setStatus(source.getStatus().getCode());
		target.setDocumentMedia(getDocumentMedia(source));
		target.setMediaId(getDocumentMediaCode(source));
	}

	protected Locale getLocale()
	{
		final LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
		Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(currentLanguage);
		if (locale == null)
		{
			locale = getI18NService().getCurrentLocale();
		}
		return locale;
	}

	protected B2BDocumentTypeData getB2BDocumentType(final B2BDocumentTypeModel documentTypeModel)
	{
		final B2BDocumentTypeData documentType = new B2BDocumentTypeData();

		documentType.setCode(documentTypeModel.getCode());
		documentType.setName(documentTypeModel.getName());
		documentType.setDisplayInAllList(documentTypeModel.getDisplayInAllList());
		documentType.setIncludeInOpenBalance(documentTypeModel.getIncludeInOpenBalance());
		if (documentTypeModel.getPayableOrUsable() != null) {
			documentType.setPayableOrUsable(documentTypeModel.getPayableOrUsable().getCode());
		}

		return documentType;
	}

	protected boolean isSelectable(final B2BDocumentTypeModel documentTypeModel, BigDecimal openAmount)
	{
		boolean isSelectable = false;
		if (documentTypeModel.getPayableOrUsable() != null && BigDecimal.ZERO.compareTo(openAmount) < 0)
		{
			// select both types
			if( Registry.getCurrentTenant().getConfig().getBoolean( B2BAccountSummaryConstants.PAYORUSEINTERFACENAME, false ) )
			{
				isSelectable = true;
			}
			else
			{
				isSelectable = documentTypeModel.getPayableOrUsable() == DocumentPayableOrUsable.PAY;
			}
		}
		return isSelectable;
	}

	protected MediaData getDocumentMedia(final B2BDocumentModel source)
	{
		MediaData documentMedia = null;
		if (source.getDocumentMedia() != null)
		{
			documentMedia = mediaConverter.convert(source.getDocumentMedia());
		}
		return documentMedia;
	}

	protected String getDocumentMediaCode(final B2BDocumentModel source)
	{
		String documentMediaCode = null;
		if (source.getDocumentMedia() != null)
		{
			documentMediaCode = source.getDocumentMedia().getCode();
		}
		return documentMediaCode;
	}

	@Required
	public void setCurrencyConverter( final Converter<CurrencyModel, CurrencyData> currencyConverter )
	{
		this.currencyConverter = currencyConverter;
	}

	@Required
	public void setMediaConverter( final Converter<MediaModel, MediaData> mediaConverter )
	{
		this.mediaConverter = mediaConverter;
	}

	@Required
	public void setAmountFormatter( final AmountFormatter amountFormatter )
	{
		this.amountFormatter = amountFormatter;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService( final CommerceCommonI18NService commerceCommonI18NService )
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService( final I18NService i18NService )
	{
		this.i18NService = i18NService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService( final CommonI18NService commonI18NService )
	{
		this.commonI18NService = commonI18NService;
	}
}
