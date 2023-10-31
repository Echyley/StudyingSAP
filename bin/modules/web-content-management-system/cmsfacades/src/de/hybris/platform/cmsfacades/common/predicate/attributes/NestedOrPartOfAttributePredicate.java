/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate.attributes;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * Predicate that returns true if the provided {@link AttributeDescriptorModel} represents a 'part-of' or a 'nested' attribute.
 */
public class NestedOrPartOfAttributePredicate implements Predicate<AttributeDescriptorModel>
{
    private Set<Predicate<AttributeDescriptorModel>> nestedAttributePredicates;
    private Set<Predicate<AttributeDescriptorModel>> partOfAttributePredicates;


    @Override
    public boolean test(final AttributeDescriptorModel attributeDescriptor)
    {
        //Fix the issue of CXEC-6195 SmartEdit - cannot edit children of NavigationNode anymore
        //ECP-5889 made children a partOf attribute which leads API return object instead of string
        //So if the partOf relationship is the specific case, still return string to make the API interface unchanged.
        return (attributeDescriptor.getPartOf() && getPartOfAttributePredicates().stream().anyMatch(predicate -> predicate.test(attributeDescriptor))) || getNestedAttributePredicates().stream().anyMatch(predicate -> predicate.test(attributeDescriptor));
    }

    protected Set<Predicate<AttributeDescriptorModel>> getNestedAttributePredicates()
    {
        return nestedAttributePredicates;
    }

    protected Set<Predicate<AttributeDescriptorModel>> getPartOfAttributePredicates()
    {
        return partOfAttributePredicates;
    }

    @Required
    public void setNestedAttributePredicates(Set<Predicate<AttributeDescriptorModel>> nestedAttributePredicates)
    {
        this.nestedAttributePredicates = nestedAttributePredicates;
    }

    public void setPartOfAttributePredicates(Set<Predicate<AttributeDescriptorModel>> partOfAttributePredicates)
    {
        this.partOfAttributePredicates = partOfAttributePredicates;
    }
}
