/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.builders;


import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for EditorController
 * Extracted data structure specific methods to this class
 */
public interface DataStructureBuilder
{
	/**
	 * Populates an entry in the attributes map with {@link ListItemAttributeDTO}s.
	 *
	 * @param typeModel   type, for which the attributes should be populated.
	 * @param currAttrMap attributes of the composed type to be populated.
	 * @return a map of all item types in an integration object mapped to the attributes of that type to be included in the
	 * integration object
	 * @deprecated Use {@link #populateAttributesMap(IntegrationMapKeyDTO, IntegrationObjectDefinition)} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	Map<ComposedTypeModel, List<AbstractListItemDTO>> populateAttributesMap(final ComposedTypeModel typeModel,
	                                                                        final Map<ComposedTypeModel, List<AbstractListItemDTO>> currAttrMap);

	/**
	 * Populates an entry in the attributes map with {@link ListItemAttributeDTO}s.
	 *
	 * @param mapKeyDTO   Key under which to populate entries
	 * @param currAttrMap Object holding current attribute mapping
	 * @return The attribute mapping after population
	 */
	IntegrationObjectDefinition populateAttributesMap(final IntegrationMapKeyDTO mapKeyDTO,
	                                                  final IntegrationObjectDefinition currAttrMap);

	/**
	 * Lays an existing definition over the basic derived version (from the type system) by updating any user selected attributes.
	 *
	 * @param existingDefinitions derived existing attributes for the item types in an integration object model
	 * @param currAttrMap         attributes selected by the user in each of the item types included in the integration object
	 * @return updated collection of attributes mapped to the corresponding item type included in the integration object
	 * @deprecated use {@link #loadExistingDefinitions(IntegrationObjectDefinition, IntegrationObjectDefinition)} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	Map<ComposedTypeModel, List<AbstractListItemDTO>> loadExistingDefinitions(
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions,
			final Map<ComposedTypeModel, List<AbstractListItemDTO>> currAttrMap);

	/**
	 * Lays an existing definition over the basic derived version (from the type system) by updating any user selected properties/attributes.
	 *
	 * @param existingDefinitions A map containing any existing definition of the object
	 * @param currAttrMap         Current attribute mapping
	 * @return The attribute mapping after layering with an existing definition
	 */
	IntegrationObjectDefinition loadExistingDefinitions(final IntegrationObjectDefinition existingDefinitions,
	                                                    final IntegrationObjectDefinition currAttrMap);

	/**
	 * Compiles a set of any type instance that has been subtyped (ie. its current type was modified to be an inherited type of the
	 * attribute's base type found in the type system. This implicitly excludes primitive types.
	 *
	 * @param existingDefinitions a map containing the existing definition of the object
	 * @param subtypeDataSet      a set to which new subtype instances will be added
	 * @return the set of subtype data instances
	 * @deprecated use {@link #compileSubtypeDataSet(IntegrationObjectDefinition, Set)} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	Set<SubtypeData> compileSubtypeDataSet(final Map<ComposedTypeModel, List<AbstractListItemDTO>> existingDefinitions,
	                                       final Set<SubtypeData> subtypeDataSet);

	/**
	 * Compiles a set of any type instance that has been subtyped (ie. its current type was modified to be an inherited type of the
	 * attribute's base type found in the type system.
	 *
	 * @param existingDefinitions A map containing the existing definition of the object
	 * @param subtypeDataSet      A set to which new subtype instances will be added
	 * @return The set of subtype data instances
	 */
	Set<SubtypeData> compileSubtypeDataSet(final IntegrationObjectDefinition existingDefinitions,
	                                       final Set<SubtypeData> subtypeDataSet);


	/**
	 * Finds an instance of a subtype in the subtype data set
	 *
	 * @param parentType         Parent key the instance is located in
	 * @param attributeQualifier Qualifier used to identify the attribute
	 * @param attributeType      Type of the attribute used to identify it
	 * @param subtypeDataSet     Set of subtype data instances to search through
	 * @return the subtype instance
	 * @deprecated use {@link #findSubtypeMatch(IntegrationMapKeyDTO, String, ComposedTypeModel, Set)} instead
	 */
	@Deprecated(since = "2205", forRemoval = true)
	ComposedTypeModel findSubtypeMatch(final ComposedTypeModel parentType,
	                                   final String attributeQualifier,
	                                   final ComposedTypeModel attributeType,
	                                   final Set<SubtypeData> subtypeDataSet);

	/**
	 * Finds an instance of a subtype in the subtype data set
	 *
	 * @param parentKey          Parent key the instance is located in
	 * @param attributeQualifier Qualifier used to identify the attribute
	 * @param attributeType      Type of the attribute used to identify it
	 * @param subtypeDataSet     Set of subtype data instances to search through
	 * @return the subtype instance
	 */
	ComposedTypeModel findSubtypeMatch(final IntegrationMapKeyDTO parentKey,
	                                   final String attributeQualifier,
	                                   final ComposedTypeModel attributeType,
	                                   final Set<SubtypeData> subtypeDataSet);
}
