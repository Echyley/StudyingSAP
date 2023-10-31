/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.NoConfigPrice;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValueMessage;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.security.core.server.csi.XSSEncoder;


/**
 * Transforms an {@link CsticValueModel} into into a list of {@link CharacteristicValue} data as required by the
 * configuration overview page.<br>
 * It is typically called by the {@link ConfigurationOverviewInstancePopulator}.
 */
public class ConfigurationOverviewValuePopulator implements ConfigurablePopulator<CsticValueModel, CharacteristicValue, Map>
{
	static final String HYBRIS_NAMES = "HYBRIS_NAMES";
	static final String CSTIC_MODEL = "CSTIC_MODEL";
	static final String VALUE_POSITION_TYPE = "VALUE_POSITION_TYPE";

	private static final Logger LOG = Logger.getLogger(ConfigurationOverviewValuePopulator.class);
	private ClassificationSystemCPQAttributesProvider nameProvider;
	private ConfigPricingImpl configPricing;
	private ValueFormatTranslator valueFormatTranslator;

	@Override
	public void populate(final CsticValueModel source, final CharacteristicValue target, final Collection<Map> options)
	{
		final HashMap optionsMap = (HashMap) options.iterator().next();
		final CsticModel cstic = (CsticModel) optionsMap.get(CSTIC_MODEL);
		final ClassificationSystemCPQAttributesContainer hybrisNames = (ClassificationSystemCPQAttributesContainer) optionsMap
				.get(HYBRIS_NAMES);
		final ValuePositionTypeEnum valuePositionType = (ValuePositionTypeEnum) optionsMap.get(VALUE_POSITION_TYPE);

		final ClassificationSystemCPQAttributesProvider cpqNameProvider = getNameProvider();
		final boolean isNameProviderDebugEnabled = cpqNameProvider.isDebugEnabled();

		target.setCharacteristic(cpqNameProvider.getDisplayName(cstic, hybrisNames, isNameProviderDebugEnabled));
		target.setCharacteristicId(cstic.getName());

		target.setValue(cpqNameProvider.getOverviewValueName(source, cstic, hybrisNames, isNameProviderDebugEnabled));
		target.setValueId(source.getName());

		final PriceModel priceModel = source.getValuePrice();
		final PriceData priceData = getConfigPricing().getPriceData(priceModel);
		target.setPrice(priceData);
		target.setPriceDescription(getCsticPriceDescription(priceData));
		final PriceData obsoletePriceData = getConfigPricing().getObsoletePriceData(priceModel);
		target.setObsoletePrice(obsoletePriceData);
		target.setObsoletePriceDescription(getCsticPriceDescription(obsoletePriceData));


		if (priceData != null && !priceData.equals(ConfigPricing.NO_PRICE) && obsoletePriceData != null)
		{
			target.setMessages(processMessages(source.getMessages()));
		}
		else
		{
			target.setMessages(new ArrayList<>());
		}
		target.setValuePositionType(valuePositionType);
	}

	protected List<CharacteristicValueMessage> processMessages(final Set<ProductConfigMessage> sourceMessages)
	{
		if (sourceMessages == null)
		{
			return new ArrayList<>();
		}

		return sourceMessages.stream().filter(msg -> ProductConfigMessagePromoType.PROMO_APPLIED.equals(msg.getPromoType())). //
				map(this::processMessage).collect(Collectors.toList());
	}

	protected CharacteristicValueMessage processMessage(final ProductConfigMessage productConfigMessage)
	{
		final CharacteristicValueMessage message = new CharacteristicValueMessage();
		try
		{
			message.setMessage(XSSEncoder.encodeHTML(productConfigMessage.getMessage()));
		}
		catch (final UnsupportedEncodingException ex)
		{
			message.setMessage("");
			LOG.warn("Message with key '" + productConfigMessage.getKey() + "' discarded due to unsupported encoding: "
					+ ex.getMessage(), ex);
		}
		message.setEndDate(getValueFormatTranslator().formatDate(productConfigMessage.getEndDate()));
		return message;
	}


	protected String getCsticPriceDescription(final PriceData price)
	{
		if (price == null || price instanceof NoConfigPrice || BigDecimal.ZERO.compareTo(price.getValue()) == 0)
		{
			return null;
		}

		return price.getFormattedValue();
	}

	/**
	 * @return the hybris characteristic and value name provider
	 */
	protected ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param nameProvider
	 *           hybris characteristic and value name provider
	 */
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

	/**
	 * @return the configPricing
	 */
	public ConfigPricingImpl getConfigPricing()
	{
		return configPricing;
	}

	/**
	 * @param configPricing
	 *           the configPricing to set
	 */
	public void setConfigPricing(final ConfigPricingImpl configPricing)
	{
		this.configPricing = configPricing;
	}

	/**
	 * @return The valueformat translator
	 */
	public ValueFormatTranslator getValueFormatTranslator()
	{
		return valueFormatTranslator;
	}

	/**
	 * Set the value format translator
	 *
	 * @param valueFormatTranslator
	 *           the value format translator
	 */
	@Required
	public void setValueFormatTranslator(final ValueFormatTranslator valueFormatTranslator)
	{
		this.valueFormatTranslator = valueFormatTranslator;
	}
}
