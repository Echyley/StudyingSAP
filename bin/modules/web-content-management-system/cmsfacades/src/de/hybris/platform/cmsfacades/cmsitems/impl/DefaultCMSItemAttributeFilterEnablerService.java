/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;
import de.hybris.platform.cmsfacades.daos.CMSItemTypeAttributeFilterConfigDao;
import de.hybris.platform.cmsfacades.model.CMSItemTypeAttributeFilterConfigModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;


/**
 * Default implementation of {@link CMSItemAttributeFilterEnablerService}
 */
public class DefaultCMSItemAttributeFilterEnablerService implements CMSItemAttributeFilterEnablerService, InitializingBean
{
	public static final String CMS_ITEM_ATTRIBUTE_FILTER_SEPARATOR = ",";
	public static final String CMS_ITEM_ATTRIBUTE_FILTER_REFERENCE_SEPARATOR = ":";
	public static final String CMS_ITEM_ATTRIBUTE_FILTER_ENABLER_MODE_CACHE_KEY = "CMS_ITEM_ATTRIBUTE_ENABLER_MODE";
	public static final String CMS_ITEM_ATTRIBUTE_FILTER_CONFIG_EXPIRATION_TIME_KEY = "cms.item.attribute.filter.config.expiration.time";
	public static final long DEFAULT_EXPIRATION_TIME = 360L;
	public static final int ATTRIBUTE_MIN_LENGTH = 2;

	private CMSItemTypeAttributeFilterConfigDao cmsItemTypeAttributeFilterConfigDao;

	private Supplier<List<TypeConfiguration>> compiledConfigs;

	private TypeService typeService;

	private SessionService sessionService;

	private ConfigurationService configurationService;

	private Supplier<Long> expirationTime = getExpirationTime();

	@Override
	public void afterPropertiesSet() throws Exception
	{
		this.compiledConfigs = initializeCompiledConfigurations(expirationTime.get(), TimeUnit.MINUTES);
	}

	@Override
	public List<String> getAttributes(final String typeCode, final String mode)
	{
		if (Objects.isNull(mode))
		{
			return Collections.emptyList();
		}
		return getAttributesByTypeAndMode(typeCode, mode.toUpperCase());
	}

	@Override
	public List<String> getAttributes(final String typeCode)
	{
		final String cachedMode = getMode();
		return getAttributes(typeCode, cachedMode);
	}

	@Override
	public boolean isAttributeAllowed(final String typeCode, final String attribute)
	{
		final List<String> attributes = getAttributes(typeCode);
		return attributes.isEmpty() || attributes.contains(attribute);
	}

	@Override
	public void setMode(final String mode)
	{
		getSessionService().setAttribute(CMS_ITEM_ATTRIBUTE_FILTER_ENABLER_MODE_CACHE_KEY, mode);
	}

	@Override
	public String getMode()
	{
		return getSessionService().getAttribute(CMS_ITEM_ATTRIBUTE_FILTER_ENABLER_MODE_CACHE_KEY);
	}

	@Override
	public void clearCache() throws InterruptedException
	{
		this.compiledConfigs = initializeCompiledConfigurations(expirationTime.get(), TimeUnit.MINUTES);
	}

	/**
	 * Returns the expiration time for the configuration.
	 * @return
	 */
	protected Supplier<Long> getExpirationTime()
	{
		 return Suppliers.memoizeWithExpiration(
				() -> getConfigurationService().getConfiguration()
						.getLong(CMS_ITEM_ATTRIBUTE_FILTER_CONFIG_EXPIRATION_TIME_KEY, DEFAULT_EXPIRATION_TIME), 1, TimeUnit.MINUTES);
	}

	/**
	 * Compiles the configuration.
	 */
	protected Supplier<List<TypeConfiguration>> initializeCompiledConfigurations(final Long defaultTimeExpiration, final TimeUnit timeUnit)
	{
		return Suppliers.memoizeWithExpiration(
				compileTypeConfigurations(),
				defaultTimeExpiration,
				timeUnit);
	}

	/**
	 * Returns attributes that must be returned by CMS ITEM API for provided type and mode.
	 * If the mode does not exist the parent type will be analyzed and if it contains necessary mode it will be used.
	 * @param typeCode the type
	 * @param mode the mode
	 * @return the list of attributes
	 */
	protected List<String> getAttributesByTypeAndMode(final String typeCode, final String mode)
	{
		final List<TypeConfiguration> typeConfigurations = compiledConfigs.get();
		return typeConfigurations.stream()
				.filter(config -> config.typeCode.equalsIgnoreCase(typeCode))
				.flatMap(config -> config.attributesByMode.stream())
				.filter(config -> config.mode.equals(mode))
				.flatMap(config -> config.attributes.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Build the attribute configuration for CMS ITEM API
	 * @return the supplier that provides the list of configurations.
	 */
	protected Supplier<List<TypeConfiguration>> compileTypeConfigurations()
	{
			return () -> {
				final List<TypeConfiguration> definedConfigs = readDefinedConfigurations();
				return compileTypeConfigurationsForAllCMSTypes(definedConfigs);
			};
	}

	/**
	 * Builds configurations for all subtypes of {@link CMSItemModel}.
	 * @param definedConfigs user based configurations
	 * @return the list of {@link TypeConfiguration} configurations for all defined platform types.
	 */
	protected List<TypeConfiguration> compileTypeConfigurationsForAllCMSTypes(final List<TypeConfiguration> definedConfigs)
	{
		final ComposedTypeModel cmsItemType = getTypeService().getComposedTypeForCode(CMSItemModel._TYPECODE);

		final ArrayList<ComposedTypeModel> allTypes = new ArrayList<>(cmsItemType.getAllSubTypes());
		allTypes.add(cmsItemType);
		return allTypes.stream().map(type -> {
			TypeConfiguration typeConfiguration = new TypeConfiguration();
			typeConfiguration.typeCode = type.getCode();
			Optional<TypeConfiguration> configOpt = getRelatedPrebuiltConfigByTypeCode(type.getCode(), definedConfigs);
			configOpt.ifPresent(config -> typeConfiguration.attributesByMode = config.attributesByMode);
			return Objects.isNull(typeConfiguration.attributesByMode) ? null : typeConfiguration;
		})
		.filter(Objects::nonNull)
		.collect(toList());
	}

	/**
	 * Reads configurations defined by user.
	 * @return the list of {@link TypeConfiguration} configurations defined by users
	 */
	protected List<TypeConfiguration> readDefinedConfigurations()
	{
		final Map<String, List<CMSItemTypeAttributeFilterConfigModel>> groupedByTypeConfigurations = getConfigurationsMap();

		return groupedByTypeConfigurations.entrySet().stream()
				.map(configEntry -> {
					TypeConfiguration typeConfiguration = new TypeConfiguration();
					typeConfiguration.typeCode = configEntry.getKey();
					typeConfiguration.attributesByMode = configEntry.getValue().stream()
							.map(config -> {
								TypeModeConfiguration typeModeConfiguration = new TypeModeConfiguration();
								typeModeConfiguration.mode = config.getMode();
								typeModeConfiguration.attributes = compileAttributes(config.getAttributes(), config.getTypeCode());
								return typeModeConfiguration;
							})
							.collect(toList());
					return typeConfiguration;
				})
				.collect(toList());
	}



	/**
	 * Returns the config for the type code.
	 * How?
	 * 1. It retrieves all supertypes for the type.
	 * 2. For all supertypes+currenttype it retrieves the configuration added by user:
	 * 	0 = "ContentPage"  			null
	 * 	1 = "AbstractPage" 			BASIC;CMSItem:BASIC,slots
	 * 	2 = "CMSItem"      			BASIC;itemtype,uid,catalogVersion,uuid
	 * 					   				SELECT;BASIC,name,label
	 * 	3 = "GenericItem"  			null
	 * 	4 = "LocalizableItem"		null
	 * 	5 = "ExtensibleItem" 		null
	 * 	6 = "Item"						null
	 * 3. Then it converts the list to map using the mode as a key. The child mode always overrides the parent (see (config1, config2) -> config1))
	 * 4. The new TypeConfiguration is build based on built structure.
	 *
	 * @return the config for the type code. If the code for current code does not exist the method tries to find a config
	 * 	 for the parent and so on until the config is found. If not found will return empty {@link Optional}.
	 */
	protected Optional<TypeConfiguration> getRelatedPrebuiltConfigByTypeCode(final String typeCode, final List<TypeConfiguration> prebuildConfigs)
	{
		final ComposedTypeModel composedTypeForCode = getTypeService().getComposedTypeForCode(typeCode);
		final ArrayList<ComposedTypeModel> relatedTypes = new ArrayList<>(composedTypeForCode.getAllSuperTypes());
		relatedTypes.add(0, composedTypeForCode);

		final Map<String, List<String>> hierarchicalModes = relatedTypes.stream()
				.map(relatedType -> getPrebuiltConfigByTypeCode(relatedType.getCode(), prebuildConfigs))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.flatMap(config -> config.attributesByMode.stream())
				.collect(toMap(config -> config.mode, config -> config.attributes, (config1, config2) -> config1));

		final TypeConfiguration hierarchicalTypeConfiguration = new TypeConfiguration();
		hierarchicalTypeConfiguration.typeCode = typeCode;
		hierarchicalTypeConfiguration.attributesByMode = hierarchicalModes.entrySet().stream()
				.map(entry -> {
					TypeModeConfiguration typeModeConfiguration = new TypeModeConfiguration();
					typeModeConfiguration.mode = entry.getKey();
					typeModeConfiguration.attributes = entry.getValue();
					return typeModeConfiguration;
				})
				.collect(toList());

		if (hierarchicalTypeConfiguration.attributesByMode.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(hierarchicalTypeConfiguration);
		}

	}

	/**
	 *  Returns the config (that defined by user) for the type code.
	 * @param typeCode the type code of the item
	 * @param prebuildConfigs the user defined configs
	 * @return the optional {@link TypeConfiguration}
	 */
	protected  Optional<TypeConfiguration> getPrebuiltConfigByTypeCode(final String typeCode, final List<TypeConfiguration> prebuildConfigs)
	{
		return prebuildConfigs.stream().filter(config -> config.typeCode.equals(typeCode)).findFirst();
	}


	/**
	 * Compiles the list of attributes for provided type.
	 * @param attributes the list of comma separated attributes.
	 * @param typeCode the type
	 * @return the list of compiled attributes.
	 */
	protected List<String> compileAttributes(final String attributes, final String typeCode)
	{
		return Lists.newArrayList(attributes.split(CMS_ITEM_ATTRIBUTE_FILTER_SEPARATOR)).stream()
				.map(String::strip)
				.map(attr -> compileAttribute(attr, typeCode))
				.flatMap(List::stream)
				.collect(toList());
	}

	/**
	 * Compiles one attribute.
	 * @param attribute the attribute to compile.
	 * @param typeCode the type code which contains the attribute
	 * @return the list that contains either one attribute if it's simple (e.g. "uid"), or many attributes if it represents a reference (e.g. "AbstractPage:BASIC")
	 */
	protected List<String> compileAttribute(final String attribute, final String typeCode)
	{
		if (Objects.isNull(attribute))
		{
			return Collections.emptyList();
		}

		if (isReferenceAttribute(attribute))
		{
			final String[] attributeSplit = attribute.split(CMS_ITEM_ATTRIBUTE_FILTER_REFERENCE_SEPARATOR);
			if (attributeSplit.length > ATTRIBUTE_MIN_LENGTH)
			{
				throw new IllegalArgumentException("The reference attribute can contain only 2 parts: <type>:<mode>. The attribute " + attributeSplit + " has errors.");
			}
			final String value = attributeSplit[0];
			final String mode = attributeSplit[1];

			if (isType(value))
			{
				return compileAttributeByTypeAndMode(value, mode);
			}
			else
			{
				throw new IllegalArgumentException("The value must be a type or mode of current (" + typeCode + ") type");
			}
		}
		else if (isMode(typeCode, attribute))
		{
			return compileAttributeByTypeAndMode(typeCode, attribute);
		}
		else
		{
			return Collections.singletonList(attribute);
		}
	}

	/**
	 * Compiles attributes for provided type and mode.
	 * @param typeCode the type for which the attributes must be compiled.
	 * @param mode the mode for which the attributes must be compiled.
	 * @return the list of attributes.
	 */
	protected List<String> compileAttributeByTypeAndMode(final String typeCode, final String mode)
	{
		final List<CMSItemTypeAttributeFilterConfigModel> configsByType = getConfigurationsByType(typeCode);
		return configsByType.stream()
				.filter(conf -> conf.getMode().equals(mode))
				.map(conf -> compileAttributes(conf.getAttributes(), conf.getTypeCode()))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	/**
	 * Verifies whether the attribute is a reference attribute.
	 * If the attribute contains {@link DefaultCMSItemAttributeFilterEnablerService#CMS_ITEM_ATTRIBUTE_FILTER_REFERENCE_SEPARATOR} it means it's a reference attribute.
	 * For example: AbstractPage:BASIC
	 * @param attribute the attribute to verify.
	 * @return true if the attribute is a reference attribute, false otherwise.
	 */
	protected boolean isReferenceAttribute(final String attribute)
	{
		return Objects.nonNull(attribute) && attribute.contains(CMS_ITEM_ATTRIBUTE_FILTER_REFERENCE_SEPARATOR);
	}

	/**
	 * Verifies whether the value represents a type.
	 * @param value the value to verify.
	 * @return true if the value represents the type, false otherwise.
	 */
	protected boolean isType(final String value)
	{
		try
		{
			getTypeService().getTypeForCode(value);
			return true;
		}
		catch (final UnknownIdentifierException e)
		{
			return false;
		}
	}

	/**
	 * Verifies whether the value represents a mode for a type.
	 * @param typeCode the type for which value is validated.
	 * @param value the value to verify
	 * @return true if the value represents a mode for a type, false otherwise.
	 */
	protected boolean isMode(final String typeCode, final String value)
	{
		final List<CMSItemTypeAttributeFilterConfigModel> configsByType = getConfigurationsByType(typeCode);
		return configsByType.stream().anyMatch(config -> config.getMode().equalsIgnoreCase(value));
	}

	/**
	 * Returns all configurations by its type.
	 * @param typeCode the type for which configurations will be returned.
	 * @return the list of configurations.
	 */
	protected List<CMSItemTypeAttributeFilterConfigModel> getConfigurationsByType(final String typeCode)
	{
		final Map<String, List<CMSItemTypeAttributeFilterConfigModel>> groupedByTypeConfigurations = getConfigurationsMap();
		return groupedByTypeConfigurations.get(typeCode);
	}

	/**
	 * Returns a map of configurations for all types where the entry key represents the type
	 * and the entry value contains the list of configurations defined for a given type.
	 *
	 * @return the grouped configurations.
	 */
	protected Map<String, List<CMSItemTypeAttributeFilterConfigModel>> getConfigurationsMap()
	{
		final List<CMSItemTypeAttributeFilterConfigModel> allAttributeConfigs = getCmsItemTypeAttributeFilterConfigDao().getAllAttributeConfigurations();

		return allAttributeConfigs.stream()
				.collect(Collectors.groupingBy(CMSItemTypeAttributeFilterConfigModel::getTypeCode));
	}

	public CMSItemTypeAttributeFilterConfigDao getCmsItemTypeAttributeFilterConfigDao()
	{
		return cmsItemTypeAttributeFilterConfigDao;
	}

	@Required
	public void setCmsItemTypeAttributeFilterConfigDao(
			final CMSItemTypeAttributeFilterConfigDao cmsItemTypeAttributeFilterConfigDao)
	{
		this.cmsItemTypeAttributeFilterConfigDao = cmsItemTypeAttributeFilterConfigDao;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	private static class TypeConfiguration
	{
		String typeCode;
		List<TypeModeConfiguration> attributesByMode;
	}

	private static class TypeModeConfiguration
	{
		String mode;
		List<String> attributes;
	}
}

