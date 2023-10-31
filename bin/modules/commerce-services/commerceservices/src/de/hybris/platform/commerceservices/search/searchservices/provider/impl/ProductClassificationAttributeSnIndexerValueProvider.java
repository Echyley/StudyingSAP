/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.provider.impl;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.commerceservices.search.searchservices.dao.SnClassAttributeAssignmentModelDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.core.service.SnSessionService;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.util.ParameterUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link SnIndexerValueProvider} for product attributes.
 */
public class ProductClassificationAttributeSnIndexerValueProvider extends
		AbstractProductSnIndexerValueProvider<ProductModel, ProductClassificationAttributeSnIndexerValueProvider.ProductClassificationData>
{
	public static final String ID = "productClassificationAttributeSnIndexerValueProvider";

	protected static final Set<Class<?>> SUPPORTED_QUALIFIER_CLASSES = Set.of(Locale.class);

	public static final String CLASSIFICATION_ATTRIBUTE_PARAM = "classificationAttribute";
	public static final String CLASSIFICATION_ATTRIBUTE_PARAM_DEFAULT_VALUE = null;

	protected static final Pattern PATTERN = Pattern.compile(
			"(?<classificationSystemId>[^/]+)/(?<classificationSystemVersion>[^/]+)/(?<classificationClassCode>[^.]+)\\.(?<classificationAttributeCode>.+)");

	protected static final String CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY = ProductClassificationAttributeSnIndexerValueProvider.class
			.getName() + ".classAttributeAssignments";

	private ClassificationSystemService classificationSystemService;
	private ClassificationService classificationService;
	private SnSessionService snSessionService;
	private SnClassAttributeAssignmentModelDao snClassAttributeAssignmentModelDao;

	@Override
	public Set<Class<?>> getSupportedQualifierClasses() throws SnIndexerException
	{
		return SUPPORTED_QUALIFIER_CLASSES;
	}

	@Override
	protected Object getFieldValue(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper,
			final ProductModel source, final ProductClassificationData data) throws SnIndexerException
	{
		final String classificationAttribute = resolveClassificationAttribute(fieldWrapper);
		final String productSelector = resolveProductSelector(fieldWrapper);
		final Set<ProductModel> products = data.getProducts().get(productSelector);

		if (CollectionUtils.isEmpty(products))
		{
			return null;
		}

		final ClassAttributeAssignmentModel classAttributeAssignment = data.getClassAttributeAssignments()
				.get(classificationAttribute);

		if (fieldWrapper.isLocalized())
		{
			final List<Locale> locales = fieldWrapper.getQualifiers().stream().map(qualifier -> qualifier.getAs(Locale.class))
					.collect(Collectors.toList());
			return collectLocalizedValues(fieldWrapper, products, classAttributeAssignment, data, locales);
		}
		else
		{
			return collectValues(fieldWrapper, products, classAttributeAssignment, data);
		}
	}

	protected Object collectLocalizedValues(final SnIndexerFieldWrapper fieldWrapper, final Collection<ProductModel> products,
			final ClassAttributeAssignmentModel classAttributeAssignment, final ProductClassificationData data,
			final List<Locale> locales)
	{
		final Map<Locale, List<Object>> localizedValues = new HashMap<>();

		for (final Locale locale : locales)
		{
			localizedValues.put(locale, new ArrayList<>());
		}

		for (final ProductModel product : products)
		{
			final FeatureList featureList = data.getFeatures().get(product.getPk());
			if (featureList != null)
			{
				final Feature feature = featureList.getFeatureByAssignment(classAttributeAssignment);
				if (feature != null)
				{
					addLocalizedFeatureValues(localizedValues, feature, locales);
				}
			}
		}

		return cleanLocalizedValues(fieldWrapper, localizedValues);
	}

	protected void addLocalizedFeatureValues(final Map<Locale, List<Object>> localizedValues, final Feature feature,
			final List<Locale> locales)
	{
		if (feature instanceof LocalizedFeature)
		{
			final LocalizedFeature localizedFeature = (LocalizedFeature) feature;

			for (final Locale locale : locales)
			{
				final List<Object> values = localizedValues.get(locale);
				addFeatureValues(values, localizedFeature.getValues(locale));
			}

		}
		else
		{
			for (final Locale locale : locales)
			{
				final List<Object> values = localizedValues.get(locale);
				addFeatureValues(values, feature.getValues());
			}
		}
	}

	protected Object cleanLocalizedValues(final SnIndexerFieldWrapper fieldWrapper,
			final Map<Locale, List<Object>> localizedValues)
	{
		final Map<Locale, Object> target = localizedValues.entrySet().stream() //
				.filter(entry -> CollectionUtils.isNotEmpty(entry.getValue())) //
				.collect(Collectors.toMap(Entry::getKey,
						entry -> fieldWrapper.isMultiValued() ? entry.getValue() : entry.getValue().get(0)));

		if (MapUtils.isEmpty(target))
		{
			return null;
		}

		return target;
	}

	protected Object collectValues(final SnIndexerFieldWrapper fieldWrapper, final Collection<ProductModel> products,
			final ClassAttributeAssignmentModel classAttributeAssignment, final ProductClassificationData data)
	{
		final List<Object> values = new ArrayList<>();

		for (final ProductModel product : products)
		{
			final FeatureList featureList = data.getFeatures().get(product.getPk());
			if (featureList != null)
			{
				final Feature feature = featureList.getFeatureByAssignment(classAttributeAssignment);
				if (feature != null)
				{
					addFeatureValues(values, feature.getValues());
				}
			}
		}

		return cleanValues(fieldWrapper, values);
	}

	protected void addFeatureValues(final List<Object> values, final List<FeatureValue> featureValues)
	{
		if (CollectionUtils.isNotEmpty(featureValues))
		{
			for (final FeatureValue featureValue : featureValues)
			{
				final Object value = featureValue.getValue();
				if (value != null)
				{
					values.add(value);
				}
			}
		}
	}

	protected Object cleanValues(final SnIndexerFieldWrapper fieldWrapper, final List<Object> values)
	{
		Object target;

		if (CollectionUtils.isEmpty(values))
		{
			target = null;
		}
		else if (fieldWrapper.isMultiValued())
		{
			target = values;
		}
		else
		{
			target = values.get(0);
		}

		return target;
	}

	@Override
	protected ProductClassificationData loadData(final SnIndexerContext indexerContext,
			final Collection<SnIndexerFieldWrapper> fieldWrappers, final ProductModel source) throws SnIndexerException
	{
		final Map<String, Set<ProductModel>> products = collectProducts(fieldWrappers, source);
		final Set<ProductModel> mergedProducts = mergeProducts(products);
		final Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = collectClassAttributeAssignments(
				indexerContext, fieldWrappers);
		final Map<PK, FeatureList> features = collectFeatures(mergedProducts, classAttributeAssignments.values());

		final ProductClassificationData data = new ProductClassificationData();
		data.setProducts(products);
		data.setClassAttributeAssignments(classAttributeAssignments);
		data.setFeatures(features);

		return data;
	}

	protected Map<String, ClassAttributeAssignmentModel> collectClassAttributeAssignments(final SnIndexerContext indexerContext,
			final Collection<SnIndexerFieldWrapper> fieldWrappers) throws SnIndexerException
	{
		Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = (Map<String, ClassAttributeAssignmentModel>) indexerContext
				.getAttributes().get(CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY);
		if (classAttributeAssignments == null)
		{
			classAttributeAssignments = doCollectClassAttributeAssignments(fieldWrappers);

			indexerContext.getAttributes().put(CLASSIFICATION_ATTRIBUTE_ASSIGNMENTS_KEY, classAttributeAssignments);
		}

		return classAttributeAssignments;
	}

	protected Map<String, ClassAttributeAssignmentModel> doCollectClassAttributeAssignments(
			final Collection<SnIndexerFieldWrapper> fieldWrappers) throws SnIndexerException
	{
		try
		{
			snSessionService.initializeSession();
			snSessionService.disableSearchRestrictions();

			final Map<String, ClassAttributeAssignmentModel> classAttributeAssignments = new HashMap<>();

			for (final SnIndexerFieldWrapper fieldWrapper : fieldWrappers)
			{
				final String classificationAttribute = resolveClassificationAttribute(fieldWrapper);

				if (StringUtils.isBlank(classificationAttribute))
				{
					throw new SnIndexerException("Required 'classificationAttribute' parameter missing");
				}

				final Matcher matcher = PATTERN.matcher(classificationAttribute);

				if (!matcher.find())
				{
					throw new SnIndexerException(
							"Invalid 'classificationAttribute' parameter value, expected pattern : {classificationSystemId}/{classificationSystemVersion}/{classificationClassCode}.{classificationAttributeCode}");
				}

				final String classificationSystemId = matcher.group("classificationSystemId");
				final String classificationSystemVersion = matcher.group("classificationSystemVersion");
				final String classificationClassCode = matcher.group("classificationClassCode");
				final String classificationAttributeCode = matcher.group("classificationAttributeCode");

				if (StringUtils.isBlank(classificationSystemId) || StringUtils.isBlank(classificationSystemVersion)
						|| StringUtils.isBlank(classificationClassCode) || StringUtils.isBlank(classificationAttributeCode))
				{
					throw new SnIndexerException(
							"Invalid 'classificationAttribute' parameter value, expected pattern : {classificationSystemId}/{classificationSystemVersion}/{classificationClassCode}.{classificationAttributeCode}");
				}

				final ClassificationSystemVersionModel classSystemVersion = classificationSystemService
						.getSystemVersion(classificationSystemId, classificationSystemVersion);
				final ClassificationClassModel classClass = classificationSystemService.getClassForCode(classSystemVersion,
						classificationClassCode);
				final ClassificationAttributeModel classAttribute = classificationSystemService
						.getAttributeForCode(classSystemVersion, classificationAttributeCode);

				final ClassAttributeAssignmentModel classAttributeAssignment = snClassAttributeAssignmentModelDao
						.findClassAttributeAssignmentByClassAndAttribute(classClass, classAttribute).orElseThrow();

				classAttributeAssignments.put(classificationAttribute, classAttributeAssignment);
			}

			return classAttributeAssignments;
		}
		finally
		{
			snSessionService.destroySession();
		}
	}

	protected Map<PK, FeatureList> collectFeatures(final Collection<ProductModel> products,
			final Collection<ClassAttributeAssignmentModel> classAttributeAssignments)
	{
		final Map<PK, FeatureList> features = new HashMap<>();

		for (final ProductModel product : products)
		{
			final FeatureList featureList = classificationService.getFeatures(product, List.copyOf(classAttributeAssignments));
			features.put(product.getPk(), featureList);
		}

		return features;
	}

	protected String resolveClassificationAttribute(final SnIndexerFieldWrapper fieldWrapper)
	{
		return ParameterUtils.getString(fieldWrapper.getValueProviderParameters(), CLASSIFICATION_ATTRIBUTE_PARAM,
				CLASSIFICATION_ATTRIBUTE_PARAM_DEFAULT_VALUE);
	}

	public ClassificationSystemService getClassificationSystemService()
	{
		return classificationSystemService;
	}

	@Required
	public void setClassificationSystemService(final ClassificationSystemService classificationSystemService)
	{
		this.classificationSystemService = classificationSystemService;
	}

	public ClassificationService getClassificationService()
	{
		return classificationService;
	}

	@Required
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;
	}

	public SnSessionService getSnSessionService()
	{
		return snSessionService;
	}

	@Required
	public void setSnSessionService(final SnSessionService snSessionService)
	{
		this.snSessionService = snSessionService;
	}

	public SnClassAttributeAssignmentModelDao getSnClassAttributeAssignmentModelDao()
	{
		return snClassAttributeAssignmentModelDao;
	}

	@Required
	public void setSnClassAttributeAssignmentModelDao(final SnClassAttributeAssignmentModelDao snClassAttributeAssignmentModelDao)
	{
		this.snClassAttributeAssignmentModelDao = snClassAttributeAssignmentModelDao;
	}

	protected static class ProductClassificationData
	{
		private Map<String, Set<ProductModel>> products;
		private Map<String, ClassAttributeAssignmentModel> classAttributeAssignments;
		private Map<PK, FeatureList> features;

		public Map<String, Set<ProductModel>> getProducts()
		{
			return products;
		}

		public void setProducts(final Map<String, Set<ProductModel>> products)
		{
			this.products = products;
		}

		public Map<String, ClassAttributeAssignmentModel> getClassAttributeAssignments()
		{
			return classAttributeAssignments;
		}

		public void setClassAttributeAssignments(final Map<String, ClassAttributeAssignmentModel> classAttributeAssignments)
		{
			this.classAttributeAssignments = classAttributeAssignments;
		}

		public Map<PK, FeatureList> getFeatures()
		{
			return features;
		}

		public void setFeatures(final Map<PK, FeatureList> features)
		{
			this.features = features;
		}
	}
}
