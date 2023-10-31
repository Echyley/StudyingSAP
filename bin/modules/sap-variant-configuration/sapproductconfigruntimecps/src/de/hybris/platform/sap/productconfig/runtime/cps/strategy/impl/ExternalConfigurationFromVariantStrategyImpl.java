/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link ExternalConfigurationFromVariantStrategy}
 */
public class ExternalConfigurationFromVariantStrategyImpl implements ExternalConfigurationFromVariantStrategy
{

	protected static final String DEFAULT_CLASS_TYPE = "300";
	protected static final String INSTANCE_ID_ROOT = "1";
	private static final String CHARACTERISTIC_VALUE_SEPARATOR = "_";
	protected static final String AUTHOR_USER = " ";
	private ClassificationService classificationService;
	private MasterDataCacheAccessService masterDataCacheAccessService;
	private I18NService i18NService;
	private FlexibleSearchService flexibleSearchService;
	private ConfigurationProductUtil configurationProductUtil;
	private String classType;

	/**
	 * @return Class type for root instance
	 */
	protected String getClassType()
	{
		if (classType == null)
		{
			return DEFAULT_CLASS_TYPE;
		}
		return classType;
	}

	/**
	 * @param classType
	 */
	public void setClassType(final String classType)
	{
		this.classType = classType;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	protected MasterDataCacheAccessService getMasterDataCacheAccessService()
	{
		return masterDataCacheAccessService;
	}

	protected ClassificationService getClassificationService()
	{
		return classificationService;
	}

	/**
	 * @param classificationService
	 */
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;

	}

	@Override
	public CPSExternalConfiguration createExternalConfiguration(final String productcode, final String kbId)
	{
		final ProductModel product = getConfigurationProductUtil().getProductForCurrentCatalog(productcode);
		final CPSExternalConfiguration externalConfiguration = new CPSExternalConfiguration();
		final CPSExternalItem rootItem = createExternalRootItem(productcode);
		externalConfiguration.setRootItem(rootItem);
		externalConfiguration.setKbId(kbId);
		externalConfiguration.setConsistent(true);
		externalConfiguration.setComplete(true);
		final List<CPSExternalCharacteristic> characteristics = determineCharacteristics(kbId, product);
		rootItem.setCharacteristics(characteristics);

		return externalConfiguration;
	}

	protected List<CPSExternalCharacteristic> determineCharacteristics(final String kbId, final ProductModel product)
	{
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = new ArrayList<>();
		final List<ProductFeatureModel> productFeatures = product.getFeatures();
		if (CollectionUtils.isNotEmpty(productFeatures))
		{
			for (final ProductFeatureModel productFeature : productFeatures)
			{
				mapProductFeatureToCpsExternalCharacteristic(productFeature, cpsExternalCharacteristicList, kbId);
			}
		}
		return cpsExternalCharacteristicList;
	}

	protected void mapProductFeatureToCpsExternalCharacteristic(final ProductFeatureModel productFeature,
			final List<CPSExternalCharacteristic> cpsExternalCharacteristicList, final String kbId)
	{
		final String characteristicId = retrieveCharacteristicId(productFeature);
		if (!isCharacteristicRelatedToCurrentProduct(characteristicId, kbId))
		{
			return;
		}
		final CPSExternalValue cpsValue = prepareCpsExternalValue(productFeature);

		final CPSExternalCharacteristic cpsCharacteristic = prepareCpsExternalCaracteristic(cpsExternalCharacteristicList,
				characteristicId);
		cpsCharacteristic.getValues().add(cpsValue);
	}

	protected String retrieveCharacteristicId(final ProductFeatureModel productFeature)
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = productFeature.getClassificationAttributeAssignment();
		Preconditions.checkNotNull(classAttributeAssignment, "No classAttributeAssignment found");
		final ClassificationAttributeModel classificationAttribute = classAttributeAssignment.getClassificationAttribute();
		Preconditions.checkNotNull(classificationAttribute, "No classificationAttribute found");
		return classificationAttribute.getCode();
	}

	protected boolean isCharacteristicRelatedToCurrentProduct(final String characteristicId, final String kbId)
	{
		final String language = getI18NService().getCurrentLocale().toLanguageTag();
		final CPSMasterDataKnowledgeBaseContainer kbContainer = getMasterDataCacheAccessService().getKbContainer(kbId, language);
		final Map<String, CPSMasterDataCharacteristicContainer> characteristics = kbContainer.getCharacteristics();
		return characteristics.containsKey(characteristicId);
	}

	protected CPSExternalValue prepareCpsExternalValue(final ProductFeatureModel productFeature)
	{
		final CPSExternalValue cpsValue = new CPSExternalValue();

		final String characteristicId = retrieveCharacteristicId(productFeature);

		final Object productFeatureValue = productFeature.getValue();
		if (!(productFeatureValue instanceof ClassificationAttributeValueModel) && !(productFeatureValue instanceof Double)
				&& !(productFeatureValue instanceof String))
		{
			//We expect only enum types for cstics with domains (that lead to ClassificationAttributeValueModel), Strings or Double
			throw new IllegalStateException(
					"Feature value is of wrong type (" + productFeatureValue.getClass().toString() + "): " + productFeatureValue);
		}

		if (productFeatureValue instanceof ClassificationAttributeValueModel)
		{
			final ClassificationAttributeValueModel valueModel = (ClassificationAttributeValueModel) productFeatureValue;
			final String valueCode = valueModel.getCode();
			if (!valueCode.contains(characteristicId + CHARACTERISTIC_VALUE_SEPARATOR))
			{
				throw new IllegalStateException(
						"We expect that classification value contains characteristic ID which was not the case for: " + valueCode + "/ "
								+ characteristicId);
			}
			final String valueId = valueCode.substring(characteristicId.length() + 1, valueCode.length());
			cpsValue.setValue(valueId);
		}
		else
		{
			cpsValue.setValue(productFeatureValue.toString());
		}

		final String author = productFeature.getAuthor();
		cpsValue.setAuthor(author != null ? author : AUTHOR_USER);

		return cpsValue;
	}

	protected CPSExternalCharacteristic prepareCpsExternalCaracteristic(
			final List<CPSExternalCharacteristic> cpsExternalCharacteristicList, final String characteristicId)
	{
		for (final CPSExternalCharacteristic cpsCharacteristic : cpsExternalCharacteristicList)
		{
			if (cpsCharacteristic.getId().equals(characteristicId))
			{
				return cpsCharacteristic;
			}
		}

		final CPSExternalCharacteristic cpsCharacteristic = new CPSExternalCharacteristic();
		cpsExternalCharacteristicList.add(cpsCharacteristic);
		cpsCharacteristic.setId(characteristicId);
		final List<CPSExternalValue> values = new ArrayList<>();
		cpsCharacteristic.setValues(values);

		return cpsCharacteristic;
	}

	protected CPSExternalItem createExternalRootItem(final String productcode)
	{
		final CPSExternalItem rootItem = new CPSExternalItem();
		rootItem.setId(INSTANCE_ID_ROOT);
		rootItem.setComplete(true);
		rootItem.setConsistent(true);
		final CPSExternalObjectKey objectKey = new CPSExternalObjectKey();
		objectKey.setType(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		objectKey.setClassType(getClassType());
		final ProductModel baseProduct = determineBaseProduct(productcode);
		objectKey.setId(baseProduct.getCode());
		rootItem.setObjectKey(objectKey);
		rootItem.setObjectKeyAuthor(AUTHOR_USER);
		final CPSQuantity quantity = new CPSQuantity();
		quantity.setValue(Double.valueOf(1));
		quantity.setUnit(baseProduct.getUnit().getCode());
		rootItem.setQuantity(quantity);
		return rootItem;
	}

	protected ProductModel determineBaseProduct(final String productcode)
	{
		final ProductModel productModel = getConfigurationProductUtil().getProductForCurrentCatalog(productcode);
		if (productModel instanceof VariantProductModel)
		{
			final VariantProductModel variantModel = (VariantProductModel) productModel;
			return variantModel.getBaseProduct();
		}
		throw new IllegalStateException("Product is no variant: " + productcode);
	}

	@Required
	public void setMasterDataCacheAccessService(final MasterDataCacheAccessService masterDataCacheAccessService)
	{
		this.masterDataCacheAccessService = masterDataCacheAccessService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		this.i18NService = i18nService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;

	}

	protected String findAuthor(final String classificationvalueidentifier, final String productpk)
	{
		final SearchResult<ProductFeatureModel> searchResult = flexibleSearchService
				.search("select {pk} from {productfeature} where {stringvalue}='" + classificationvalueidentifier + "'"
						+ "and {product}='" + productpk + "'");

		final List<ProductFeatureModel> result = searchResult.getResult();
		if (result.size() != 1)
		{
			throw new IllegalStateException("No unique result found in productFeature");
		}
		final String author = result.get(0).getAuthor();
		return author != null ? author : AUTHOR_USER;

	}

	protected ConfigurationProductUtil getConfigurationProductUtil()
	{
		return configurationProductUtil;
	}

	@Required
	public void setConfigurationProductUtil(final ConfigurationProductUtil configurationProductUtil)
	{
		this.configurationProductUtil = configurationProductUtil;
	}

}
