/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiproductexchange.inbound.events;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationClassesResolverStrategy;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.lang.String;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SapCpiProductFeaturePersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiProductFeaturePersistenceHook.class);

	private static final String IMPEX_FALLBACK_KEY = "impex.nonexistend.clsattrvalue.fallback.enabled";
	private static final String QUALIFIER_SPLITTER = "/";
	private static final String DEFAULT_DATE_TIME_PATTERN = "yyyyMMdd";
	private static final int CLASSIFICATION_SYSTEM = 0;
	private static final int CLASSIFICATION_SYSTEM_VERSION = 1;
	private static final int FEATURE_ID = 2;
	private static final int RANGE_VALUES = 2;
	private static final int QUALIFIER_SIZE = 3;

	private ClassificationClassesResolverStrategy classResolverStrategy;
	private ModelService modelService;
	private ClassificationSystemService classificationSystemService;
	private String collectionDelimiter;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof ProductModel)
		{
			LOG.info("The persistence hook sapCpiProductFeaturePersistenceHook is called!");
			final ProductModel productModel = (ProductModel) item;
			final List<ProductFeatureModel> existingFeatures = productModel.getFeatures().stream().filter(feature->feature.getPk() != null).collect(Collectors.toList());
			final List<ProductFeatureModel> unSavedFeatures =  productModel.getFeatures().stream().filter(feature->feature.getPk() == null).collect(Collectors.toList());

			final List<ProductFeatureModel> cleanedFeatures = new ArrayList<>();
			cleanedFeatures.addAll(existingFeatures);

			setProductFeatureProduct(unSavedFeatures, productModel);

			final Map<PK, Integer> multipleAttributeValuePosition = new HashMap<>();
			existingFeatures.forEach(feature -> multipleAttributeValuePosition
					.put(feature.getClassificationAttributeAssignment().getPk(),
							multipleAttributeValuePosition.getOrDefault(feature.getClassificationAttributeAssignment().getPk(),
									feature.getValuePosition()) + 1));

			final List<ProductFeatureModel> newFeatures = new ArrayList<>();
			final Set<PK> singleAttributeValueSet = new HashSet<>();
			final Set<ClassificationClassModel> classificationClasses = getClassificationClassModel(productModel);

			for (final ProductFeatureModel unSavedFeature : unSavedFeatures)
			{
				final List<String> qualifierInfo = processFeatureQualifier(unSavedFeature);
				final ClassAttributeAssignmentModel assignmentModel =
						retrieveClassAttributeAssignmentModel(classificationClasses, qualifierInfo);

				if (isDuplicatedSingleAssignmentValue(assignmentModel, singleAttributeValueSet))
				{
					continue;
				}
				else if (Boolean.TRUE.equals(assignmentModel.getMultiValued()))
				{
					multipleAttributeValuePosition.put(assignmentModel.getPk(), multipleAttributeValuePosition
							.getOrDefault(assignmentModel.getPk(), 0) + 1);
				}

				final List<Object> values = translateAwareValue(assignmentModel, unSavedFeature.getValue(),
						qualifierInfo.get(2));
				final ClassificationClassModel classificationClass = assignmentModel.getClassificationClass();

				unSavedFeature.setValue(values.get(0));
				unSavedFeature.setUnit(assignmentModel.getUnit());
				unSavedFeature.setClassificationAttributeAssignment(assignmentModel);
				unSavedFeature.setQualifier(buildQualifier(classificationClass, qualifierInfo));
				unSavedFeature.setValuePosition(multipleAttributeValuePosition.get(assignmentModel.getPk()));

				final ProductFeatureModel matchedFeature = findMatchedProductFeatureValue(existingFeatures, unSavedFeature);

				if (matchedFeature != null)
				{
					existingFeatures.remove(matchedFeature);
				}
				else
				{
					newFeatures.add(unSavedFeature);
				}

				//If it is a range attribute that has two numeric values
				if (values.size() == RANGE_VALUES)
				{
					final ProductFeatureModel newProductFeature = cloneProductFeature(unSavedFeature);
					newProductFeature.setValue(values.get(1));
					newProductFeature.setUnit(assignmentModel.getUnit());
					//If the attribute has multiple values
					multipleAttributeValuePosition.put(assignmentModel.getPk(), multipleAttributeValuePosition
							.get(assignmentModel.getPk()) + 1);
					newProductFeature.setValuePosition(multipleAttributeValuePosition.get(assignmentModel.getPk()));
					newFeatures.add(newProductFeature);
				}
			}

			if( !existingFeatures.isEmpty() || !newFeatures.isEmpty())
			{
				cleanFeatures(cleanedFeatures);				
				storeFeatures(newFeatures);
				unSavedFeatures.clear();
			}
			return Optional.empty();
		}

		return Optional.of(item);
	}

	private boolean isDuplicatedSingleAssignmentValue(final ClassAttributeAssignmentModel assignment,
	                                                  final Set<PK> singleAttributeValueSet)
	{
		return assignment == null || !(assignment.getMultiValued() || singleAttributeValueSet.add(assignment.getPk()));
	}

	private ProductFeatureModel findMatchedProductFeatureValue(final List<ProductFeatureModel> existedProductFeature,
	                                                           final ProductFeatureModel comparedFeature)
	{
		return existedProductFeature.stream().filter(
				feature -> isSameFeatureAttributes(feature,comparedFeature) && isSameFeatureValue(feature,comparedFeature) &&
						isSameFeatureAuthor(feature,comparedFeature)).findFirst().orElse(null);
	}

	private boolean isSameFeatureAttributes(final ProductFeatureModel feature, final ProductFeatureModel comparedFeature) {
		return feature.getClassificationAttributeAssignment().getPk().equals(comparedFeature.getClassificationAttributeAssignment().getPk());
	}

	private boolean isSameFeatureValue(final ProductFeatureModel feature, final ProductFeatureModel comparedFeature) {
		return feature.getValue().equals(comparedFeature.getValue()) || (feature
				.getValue() instanceof ClassificationAttributeValueModel && ((ClassificationAttributeValueModel) feature
				.getValue()).getPk().equals(
				((ClassificationAttributeValueModel) comparedFeature.getValue()).getPk()));
	}

	private boolean isSameFeatureAuthor(final ProductFeatureModel feature, final ProductFeatureModel comparedFeature) {
		return Objects.equals(feature.getAuthor(), comparedFeature.getAuthor());
	}


	private void cleanFeatures(final List<ProductFeatureModel> features)
	{
		if (CollectionUtils.isNotEmpty(features))
		{
			getModelService().removeAll(features);
		}
	}

	private void storeFeatures(final List<ProductFeatureModel> features)
	{
		if (CollectionUtils.isNotEmpty(features))
		{
			features.forEach(feature -> getModelService().save(feature));
		}
	}

	private List<String> processFeatureQualifier(final ProductFeatureModel productFeature)
	{
		return Arrays.asList(productFeature.getQualifier().split(QUALIFIER_SPLITTER));
	}

	private List<Object> translateAwareValue(final ClassAttributeAssignmentModel assignment, final Object value,
	                                         final String qualifier)
	{
		final List<Object> values = new ArrayList<>();

		for (final String singleStr : splitFeatureValues(assignment, value, qualifier))
		{
			if (Optional.ofNullable(singleStr).isPresent())
			{
				try
				{
					values.add(getSingleProductFeatureValue(assignment, singleStr, qualifier));
				}
				catch (final JaloInvalidParameterException e)
				{
					if (Config.getBoolean(IMPEX_FALLBACK_KEY, false))
					{
						LOG.debug("Fallback ENABLED");
						LOG.warn(String.format("Value %s is not of type %s will use type string as fallback (%s)", value,
								assignment.getAttributeType().getCode(), e.getMessage()), e);
					}
					else
					{
						LOG.debug("Fallback DISABLED. Marking line as unresolved. Will try to import value in another pass", e);
					}
				}
			}
		}

		return values;
	}

	private void setProductFeatureProduct(final List<ProductFeatureModel> productFeatures, final ProductModel product)
	{
		if (productFeatures != null)
		{
			productFeatures.forEach(feature -> feature.setProduct(product));
		}
	}

	private Set<ClassificationClassModel> getClassificationClassModel(final ProductModel product)
	{
		return classResolverStrategy.resolve(product);
	}

	private ClassAttributeAssignmentModel retrieveClassAttributeAssignmentModel(
			final Set<ClassificationClassModel> classificationClasses, final List<String> qualifierInfo)
	{

		if (classificationClasses.isEmpty() || qualifierInfo == null || qualifierInfo.size() < QUALIFIER_SIZE)
		{
			return null;
		}

		return findAssignmentWithCode(classResolverStrategy.getAllClassAttributeAssignments(classificationClasses),
				qualifierInfo);
	}

	private ClassAttributeAssignmentModel findAssignmentWithCode(final Collection<ClassAttributeAssignmentModel> assignments,
	                                                             final List<String> qualifierInfo)
	{
		if (Optional.ofNullable(assignments).isPresent())
		{
			for (final ClassAttributeAssignmentModel assignment : assignments)
			{
				if (assignment != null && assignment.getClassificationAttribute()
				                                    .getCode()
				                                    .equals(qualifierInfo.get(FEATURE_ID)) &&
						assignment.getSystemVersion().getCatalog().getId().equals(qualifierInfo.get(CLASSIFICATION_SYSTEM)))
				{
					return assignment;
				}
			}
		}
		return null;
	}

	private ClassificationAttributeValueModel findAttributeValue(final String code,
	                                                             final ClassificationSystemVersionModel classificationSystemVersion)
	{
		final ClassificationAttributeValueModel attributeValue = classificationSystemService.getAttributeValueForCode(
				classificationSystemVersion, code);
		validateParameterNotNull(attributeValue, "No such attribute value: null");
		return attributeValue;
	}

	private Object getSingleProductFeatureValue(final ClassAttributeAssignmentModel assignment, final Object featureValue,
	                                            final String qualifier)
	{
		if (featureValue instanceof String)
		{
			switch (assignment.getAttributeType())
			{
				case BOOLEAN:
					return Boolean.valueOf(formatValue(featureValue, qualifier));
				case ENUM:
					return findAttributeValue((String) featureValue, assignment.getSystemVersion());
				case NUMBER:
					return Double.valueOf(convertNumeric(featureValue, qualifier));
				case STRING:
					return formatValue(featureValue, qualifier);
				case DATE:
					return getDateValue(featureValue, qualifier);
				case REFERENCE:
					break;
			}
		}
		return featureValue;
	}

	private Date getDateValue(final Object featureValue, final String qualifier)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN);
		try
		{
			return df.parse(convertNumeric(featureValue, qualifier));
		}
		catch (final ParseException e)
		{
			throw new IllegalArgumentException("Invalid date: " + formatValue(featureValue, qualifier));
		}
	}

	private String formatValue(final Object value, final String qualifier)
	{
		return value != null ? ((String) value).replace(qualifier.toUpperCase() + "_", "") : null;
	}

	private String buildQualifier(final ClassificationClassModel superCategory, final List<String> qualifierInfo)
	{
		return qualifierInfo.get(CLASSIFICATION_SYSTEM) +
				QUALIFIER_SPLITTER +
				qualifierInfo.get(CLASSIFICATION_SYSTEM_VERSION) +
				QUALIFIER_SPLITTER +
				superCategory.getCode() +
				"." +
				qualifierInfo.get(FEATURE_ID).toLowerCase();
	}

	private String convertNumeric(final Object value, final String qualifier)
	{
		final String numericValue = formatValue(value, qualifier);
		final BigDecimal number = new BigDecimal(numericValue);
		return number.stripTrailingZeros().toPlainString();
	}

	private ProductFeatureModel cloneProductFeature(final ProductFeatureModel productFeature)
	{
		return modelService.clone(productFeature);
	}

	private List<String> splitFeatureValues(final ClassAttributeAssignmentModel assignment, final Object valueCollection,
	                                        final String qualifier)
	{
		if (valueCollection == null)
		{
			return Collections.emptyList();
		}

		final String delimiter = (collectionDelimiter == null || collectionDelimiter.isEmpty()) ? String.valueOf(
				ImpExConstants.Syntax.DEFAULT_COLLECTION_VALUE_DELIMITER) : collectionDelimiter;

		final String[] values = ((String) valueCollection).split(Pattern.quote(delimiter));
		final List<String> returnValues = new ArrayList<>();

		returnValues.add(values[0]);
		if (Boolean.TRUE.equals(assignment.getRange()) && values.length == RANGE_VALUES)
		{
			if (new BigDecimal(formatValue(values[0], qualifier)).compareTo(new BigDecimal(values[1])) > 0)
			{
				returnValues.add(convertMaxNumeric(assignment.getFormatDefinition()));
			}
			else
			{
				returnValues.add(values[1]);
			}
		}
		return returnValues;
	}

	private String convertMaxNumeric(final String format)
	{
		return format == null ? "9999" :
				format.split(";")[0]
						.replace(",", "")
						.replace('#', '9')
						.replace('0', '9');
	}

	protected ClassificationClassesResolverStrategy getClassResolverStrategy()
	{
		return classResolverStrategy;
	}

	public void setClassResolverStrategy(final ClassificationClassesResolverStrategy classResolverStrategy)
	{
		this.classResolverStrategy = classResolverStrategy;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ClassificationSystemService getClassificationSystemService()
	{
		return classificationSystemService;
	}

	public void setClassificationSystemService(final ClassificationSystemService classificationSystemService)
	{
		this.classificationSystemService = classificationSystemService;
	}

	protected String getCollectionDelimiter()
	{
		return collectionDelimiter;
	}

	public void setCollectionDelimiter(final String collectionDelimiter)
	{
		this.collectionDelimiter = collectionDelimiter;
	}
}
