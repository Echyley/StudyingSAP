/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigDataMergeProcessor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * CPQ UI Validator.<br>
 * Will validate the user input received via HTTP. Ensures that the partial configuration send via HHTP is merged with
 * the complete configuration state, so that all processing can access the whole configuration.<br>
 * Validation itself will be delegated to Checker classes, such as the @ NumericChecker}.
 *
 * @see ConfigDataMergeProcessor
 */
public class ProductConfigurationValidator implements Validator
{
	private ConfigDataMergeProcessor mergeProcessor;

	private List<CsticValueValidator> csticValidators;

	private ConfigurationFacade configurationFacade;

	@Override
	public boolean supports(final Class<?> classObj)
	{
		return ConfigurationData.class.equals(classObj);
	}

	@Override
	public void validate(final Object configurationObj, final Errors errorObj)
	{
		final ConfigurationData configuration = (ConfigurationData) configurationObj;

		// In case of session failover the previously cached ConfigModel and UiStatus are lost.
		// In this case merge/completeInput is not possible.
		// Update method in the related controller class checks this as well and navigate than to desired home/error page.

		final String configId = configuration.getConfigId();
		if (!getConfigurationFacade().isConfigurationAvailable(configId))
		{
			return;
		}

		mergeProcessor.completeInput(configuration);

		final List<UiGroupData> groups = configuration.getGroups();

		validateSubGroups(groups, errorObj, "groups");
	}

	protected void validateGroup(final UiGroupData group, final Errors errorObj)
	{
		final List<CsticData> cstics = group.getCstics();
		validateCstics(cstics, errorObj);

		final List<UiGroupData> subGroups = group.getSubGroups();
		validateSubGroups(subGroups, errorObj, "subGroups");
	}

	protected void validateSubGroups(final List<UiGroupData> subGroups, final Errors errorObj, final String groupListName)
	{
		if (subGroups == null)
		{
			return;
		}
		for (int ii = 0; ii < subGroups.size(); ii++)
		{
			final UiGroupData subGroup = subGroups.get(ii);
			final String prefix = groupListName + "[" + ii + "]";
			errorObj.pushNestedPath(prefix);
			validateGroup(subGroup, errorObj);
			errorObj.popNestedPath();
		}
	}

	protected void validateCstics(final List<CsticData> cstics, final Errors errorObj)
	{
		if (cstics == null)
		{
			return;
		}
		for (int ii = 0; ii < cstics.size(); ii++)
		{
			errorObj.pushNestedPath("cstics[" + ii + "]");
			final CsticData csticData = cstics.get(ii);
			validateCstic(errorObj, csticData);
			errorObj.popNestedPath();
		}
	}

	protected void validateCstic(final Errors errorObj, final CsticData csticData)
	{
		for (final CsticValueValidator validator : getCsticValidators())
		{
			if (validator.appliesTo(csticData))
			{
				validateWithModification(errorObj, csticData, validator);
			}
		}
	}

	protected void validateWithModification(final Errors errorObj, final CsticData csticData, final CsticValueValidator validator)
	{
		String value = csticData.getValue();
		if (!StringUtils.isEmpty(value) && validator.appliesToValues())
		{
			value = validator.validate(csticData, errorObj, value);
			csticData.setValue(value);
		}

		String additionalValue = csticData.getAdditionalValue();
		if (!StringUtils.isEmpty(additionalValue) && validator.appliesToAdditionalValues())
		{
			additionalValue = validator.validate(csticData, errorObj, additionalValue);
			csticData.setAdditionalValue(additionalValue);
		}

		String formattedValue = csticData.getFormattedValue();
		if (!StringUtils.isEmpty(formattedValue) && validator.appliesToFormattedValues())
		{
			formattedValue = validator.validate(csticData, errorObj, formattedValue);
			csticData.setFormattedValue(formattedValue);
		}
	}


	/**
	 * @param mergeProcessor injects the merge processor, that will merge the partial configuration submitted from the UI with the
	 *                       complete configuration from the underlying layers
	 */
	public void setMergeProcessor(final ConfigDataMergeProcessor mergeProcessor)
	{
		this.mergeProcessor = mergeProcessor;
	}

	protected ConfigurationFacade getConfigurationFacade()
	{
		return configurationFacade;
	}

	@Required
	public void setConfigurationFacade(final ConfigurationFacade configurationFacade)
	{
		this.configurationFacade = configurationFacade;
	}

	protected List<CsticValueValidator> getCsticValidators()
	{
		return Optional.ofNullable(csticValidators).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	/**
	 * @param csticValidators list of cstic validators to be called for validation
	 */
	public void setCsticValidators(final List<CsticValueValidator> csticValidators)
	{
		this.csticValidators = Optional.ofNullable(csticValidators).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

}
