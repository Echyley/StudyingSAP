/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.odata.schema.navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.schema.association.AssociationGenerator;
import de.hybris.platform.odata2services.odata.schema.navigation.NavigationPropertySchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils;
/**
 * C4cNavigationPropertySchemaElementGenerator to skip CPQ/C4C validation
 */
public class C4cNavigationPropertySchemaElementGenerator extends NavigationPropertySchemaElementGenerator {
	@Override
	public Optional<NavigationProperty> generate(final TypeAttributeDescriptor attribute) {
		final Optional<AssociationGenerator> associationGeneratorOptional = getAssociationGeneratorRegistry()
				.getAssociationGenerator(attribute);
		final String typeCode = attribute.getTypeDescriptor().getTypeCode();
		ArrayList<String> itemType = new ArrayList<>(Arrays.asList("SAPC4CCpiOutboundItem", "SAPC4CCpiOutboundQuote","C4CSalesOrderNotification","SAPC4CComment","SAPCPQOutboundQuote","SAPCPQOutboundQuoteItem","SAPCPQOutboundQuoteCustomer","SAPCPQOutboundQuoteComment","SAPCPQOutboundQuoteStatus" ));
		if(itemType.contains(typeCode)) {
			return associationGeneratorOptional
					.map(associationGenerator -> generateInternal(associationGenerator, attribute));
		} else {
			return super.generate(attribute);
		}

	}

	private NavigationProperty generateInternal(final AssociationGenerator associationGenerator,
			final TypeAttributeDescriptor attribute) {
		return attribute != null ? createNavigationProperty(associationGenerator, attribute) : null;
	}

	private NavigationProperty createNavigationProperty(final AssociationGenerator associationGenerator,
			final TypeAttributeDescriptor attribute) {
		final Association association = associationGenerator.generate(attribute);
		return new NavigationProperty().setName(attribute.getAttributeName())
				.setRelationship(new FullQualifiedName(SchemaUtils.NAMESPACE, association.getName()))
				.setFromRole(association.getEnd1().getRole()).setToRole(association.getEnd2().getRole())
				.setAnnotationAttributes(getPropertyAnnotationListGenerator().generate(attribute));
	}

}
