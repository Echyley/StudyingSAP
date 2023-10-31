/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors;

import static com.google.common.collect.Lists.newLinkedList;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;


/**
 * Abstract class for visiting {@link AbstractCMSComponentModel} models for the cms synchronization service to work
 * properly. In this implementation, it will collect all component's child (collection or not) extending or being
 * {@link CMSItemModel}
 *
 * @param <CMSCOMPONENTTYPE>
 *           the component type that extends {@link AbstractCMSComponentModel}
 */
public abstract class AbstractCMSComponentModelVisitor<CMSCOMPONENTTYPE extends AbstractCMSComponentModel>
		implements ItemVisitor<CMSCOMPONENTTYPE>, InitializingBean
{
	public static final String SYNCHRONIZATION_COMPONENT_ATTRIBUTE_CACHE_EXPIRATION_TIME = "cms.synchronization.generic.component.visitor.attributes.cache.expiration.time.in.minutes";

	private static final Logger LOG = LoggerFactory.getLogger(AbstractCMSComponentModelVisitor.class);
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	private TypeService typeService;
	private ModelService modelService;
	private Map<String, List<String>> ignoreAttributeTypeCodeConfigs;
	private ConfigurationService configurationService;
   private Supplier<Map<String, List<AttributeDescriptorModel>>> allowedAttributesConfigCacheSupplier;

	@Override
	public void afterPropertiesSet()
	{
		final long expirationTime = getConfigurationService().getConfiguration().getLong(SYNCHRONIZATION_COMPONENT_ATTRIBUTE_CACHE_EXPIRATION_TIME, 60);
		this.allowedAttributesConfigCacheSupplier = Suppliers.memoizeWithExpiration(HashMap::new, expirationTime, TimeUnit.MINUTES);
	}

	@Override
	public List<ItemModel> visit(final CMSCOMPONENTTYPE source, final List<ItemModel> path, final Map<String, Object> ctx)
	{
		if (getTypeService() == null)
		{
			// case of not-fixed acceleratorfacades code that is not using the parent bean properly
			return newLinkedList(source.getRestrictions());
		}

		final List<ItemModel> toVisit = new ArrayList<>();
		getAttributesFromCacheOrRetrieve(source).forEach(attributeDescriptorModel -> collectChildItems(source, toVisit, attributeDescriptorModel));
		return toVisit;
	}

	/**
	 * Returns the list of attributes for component type. It retrieves them from the cache if available, or generated if cache is empty.
	 * @param source the source component type
	 * @return the list of attributes.
	 */
	protected List<AttributeDescriptorModel> getAttributesFromCacheOrRetrieve(final CMSCOMPONENTTYPE source)
	{
		final Map<String, List<AttributeDescriptorModel>> config = allowedAttributesConfigCacheSupplier.get();
		final String itemType = source.getItemtype();
		if (!config.containsKey(itemType))
		{
			final ComposedTypeModel composedTypeModel = getTypeService().getComposedTypeForClass(source.getClass());
			final List<AttributeDescriptorModel> allowedAttributes = Stream
					.of(composedTypeModel.getDeclaredattributedescriptors(), composedTypeModel.getInheritedattributedescriptors())
					.flatMap(Collection::stream)
					.filter(attribute -> isClassAssignableFrom(CMSItemModel.class, attribute))
					.filter(attribute -> isAttributeAllowed(source, attribute))
					.collect(toList());
			config.put(itemType, allowedAttributes);
		}
		return config.get(itemType);
	}


	/**
	 * Verifies whether the attribute is allowed for future visiting.
	 * @param componentType the type for which to retrieve the list of ignored attribute types from config.
	 * @param attribute the attribute to verify
	 * @return true if the attribute must be visited earlier, false otherwise.
	 */
	protected boolean isAttributeAllowed(final CMSCOMPONENTTYPE componentType, final AttributeDescriptorModel attribute)
	{
		return getIgnoreAttributeTypeClasses(componentType).stream().noneMatch(clazz -> isClassAssignableFrom(clazz, attribute));
	}

	/**
	 * Returns all type classes that must be ignored during iteration over component attributes.
	 * @param componentType the type for which to retrieve the list of ignored attribute types from config.
	 * @return the list of classes.
	 */
	protected List<Class<?>> getIgnoreAttributeTypeClasses(final CMSCOMPONENTTYPE componentType)
	{
		final List<String> allSuperTypeCodes = getAllSuperTypeCodes(componentType);
		final List<String> allIgnoredAttributeCodes = getAllIgnoredAttributeTypesInConfigHierarchy(allSuperTypeCodes);
		return allIgnoredAttributeCodes.stream() //
				.map(ignoreAttributeTypeCode -> getTypeService().getModelClass(ignoreAttributeTypeCode)) //
				.collect(toList());
	}

	/**
	 * Collects all child items of type {@link CMSItemModel} for the provided source component.
	 *
	 * @param source
	 *           - the component which attributes is inspected to collect child items
	 * @param toVisit
	 *           - the list of items collected which will be used by the synchronization service
	 * @param attributeDescriptorModel
	 *           - the attribute descriptor object containing the information about the attribute of interest
	 */
	protected void collectChildItems(final CMSCOMPONENTTYPE source, final List<ItemModel> toVisit,
			final AttributeDescriptorModel attributeDescriptorModel)
	{
		Optional.ofNullable(getAttributeValue(source, attributeDescriptorModel)).ifPresent(propertyValue -> {
			if (Collection.class.isAssignableFrom(propertyValue.getClass()))
			{
				final Collection<CMSItemModel> collection = (Collection<CMSItemModel>) propertyValue;
				if (!collection.isEmpty())
				{
					toVisit.addAll(collection);
				}
			}
			else
			{
				toVisit.add((CMSItemModel) propertyValue);
			}
		});
	}

	/**
	 * Gets the value by calling the getter for the attribute defined by the provided attribute descriptor.
	 * <p>
	 * When the attribute is not readable (due to configurations in *items.xml), the ModelService will return an
	 * {@link AttributeNotSupportedException} and the attribute will not be added to the list of items to visit by the
	 * synchronization service.
	 *
	 * @param source
	 *           - the source object which the getter method will be called
	 * @param attributeDescriptorModel
	 *           - the attribute descriptor object containing the information about the attribute of interest
	 * @return the result of the getter; can be {@code NULL}
	 */
	protected Object getAttributeValue(final CMSCOMPONENTTYPE source, final AttributeDescriptorModel attributeDescriptorModel)
	{
		try
		{
			return getModelService().getAttributeValue(source, attributeDescriptorModel.getQualifier());
		}
		catch (final AttributeNotSupportedException e)
		{
			// ignore attributes that are not readable
			LOG.debug(e.getMessage(), e);
		}
		return null;
	}

	protected boolean isClassAssignableFrom(final Class clazz, final AttributeDescriptorModel attributeDescriptor)
	{
		return clazz.isAssignableFrom(getAttributeDescriptorModelHelperService().getAttributeClass(attributeDescriptor));
	}

	/**
	 * Returns all type codes that must be ignored during iteration over component attributes.
	 * @param allSuperTypeCodes the list of type codes for which the configuration must be retrieved.
	 * @return the list of type codes that must be ignored.
	 */
	protected List<String> getAllIgnoredAttributeTypesInConfigHierarchy(final List<String> allSuperTypeCodes)
	{
		return allSuperTypeCodes.stream() //
				.map(this::getIgnoreAttributeConfigurationByTypeCode) //
				.filter(Optional::isPresent) //
				.map(Optional::get) //
				.flatMap(Collection::stream) //
				.collect(toList());
	}

	/**
	 * Get the list of all the super typeCodes given a composedType, including the composedType typeCode.
	 *
	 * @param componentType the component type
	 * @return the {@link List} of {@link String} representation of all the super typeCodes given a composedType,
	 *         including the composedType typeCode.
	 */
	protected List<String> getAllSuperTypeCodes(final CMSCOMPONENTTYPE componentType)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(componentType.getItemtype());
		return Stream.concat(Stream.of(composedType), composedType.getAllSuperTypes().stream()).map(ComposedTypeModel::getCode)
				.collect(toList());
	}

	/**
	 * Returns the configuration for the type code.
	 * @param typeCode the type code for which return the configuration.
	 * @return the {@link Optional} configuration that contains a list of type codes that must be ignored.
	 */
	protected Optional<List<String>> getIgnoreAttributeConfigurationByTypeCode(final String typeCode)
	{
		return getIgnoreAttributeTypeCodeConfigs().entrySet().stream() //
				.filter(config -> config.getKey().equals(typeCode)) //
				.map(Map.Entry::getValue) //
				.findFirst();
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			final AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;
	}

	public Map<String, List<String>> getIgnoreAttributeTypeCodeConfigs()
	{
		return ignoreAttributeTypeCodeConfigs;
	}

	@Required
	public void setIgnoreAttributeTypeCodeConfigs(final Map<String, List<String>> ignoreAttributeTypeCodeConfigs)
	{
		this.ignoreAttributeTypeCodeConfigs = ignoreAttributeTypeCodeConfigs;
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
}
