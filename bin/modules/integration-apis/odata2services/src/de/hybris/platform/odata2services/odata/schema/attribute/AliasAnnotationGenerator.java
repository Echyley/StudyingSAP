/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.attribute;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ALIAS_ANNOTATION_ATTR_NAME;

import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

public class AliasAnnotationGenerator
{
	private IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator;

	/**
	 * Generates {@link AnnotationAttribute} for the given {@link IntegrationObjectItemModel} using injected
	 * 	  {@link IntegrationKeyMetadataGenerator}.
	 *
	 * @param itemModel item model to generate annotation attribute for.
	 * @return the annotation attribute object generated from the given item model. Returns {@code null} if
	 * IntegrationKeyMetadataGenerator is {@code null} or when given metadata generator generates empty or {@code null}
	 * metadata string. See {@link #setIntegrationKeyMetadataGenerator(IntegrationKeyMetadataGenerator)}.
	 * @deprecated use {@link #generate(TypeDescriptor)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public AnnotationAttribute generate(final IntegrationObjectItemModel itemModel)
	{
		return integrationKeyMetadataGenerator != null
				? getAnnotationAttribute(integrationKeyMetadataGenerator.generateKeyMetadata(itemModel))
				: null;
	}

	/**
	 * Generates {@link AnnotationAttribute} for the given {@link TypeDescriptor} using injected
	 * {@link IntegrationKeyMetadataGenerator}.
	 *
	 * @param descriptor type descriptor for the item to generate annotation attribute for.
	 * @return the annotation attribute object generated from the given type descriptor. Returns {@code null} if
	 * IntegrationKeyMetadataGenerator is {@code null} or when given metadata generator generates empty or {@code null}
	 * metadata string. See {@link #setIntegrationKeyMetadataGenerator(IntegrationKeyMetadataGenerator)}.
	 */
	@Nullable
	public AnnotationAttribute generate(final TypeDescriptor descriptor)
	{
		if (integrationKeyMetadataGenerator != null)
		{
			final String integrationKeyAlias = integrationKeyMetadataGenerator.generateKeyMetadata(descriptor);
			return getAnnotationAttribute(integrationKeyAlias);
		}
		return null;
	}

	private AnnotationAttribute getAnnotationAttribute(final String integrationKeyAlias)
	{
		return StringUtils.isEmpty(integrationKeyAlias)
				? null
				: new AnnotationAttribute().setName(ALIAS_ANNOTATION_ATTR_NAME)
				                           .setText(integrationKeyAlias);
	}

	/**
	 * Sets the given nullable IntegrationKeyMetadataGenerator. If the generator is not injected or is {@code null},
	 * the {@link #generate(TypeDescriptor)} cannot generate a valid
	 * alias and will return {@code null}.
	 *
	 * @param integrationKeyMetadataGenerator generator used by generate methods.
	 */
	public void setIntegrationKeyMetadataGenerator(
			@Nullable final IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator)
	{
		this.integrationKeyMetadataGenerator = integrationKeyMetadataGenerator;
	}
}
