/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate.attributes;

import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

/**
 * Predicate that returns true if the provided {@link AttributeDescriptorModel} is partOf relation except "CMSNavigationNodeChildren"
 */
public class IsNavigationNodeRelationPredicate implements Predicate<AttributeDescriptorModel>
{
    @Override
    public boolean test(AttributeDescriptorModel attributeDescriptorModel)
    {
        return attributeContainsSkipRelation(attributeDescriptorModel);
    }

    protected boolean attributeContainsSkipRelation(AttributeDescriptorModel attributeDescriptorModel)
    {
        if (attributeDescriptorModel.getItemtype().contains(RelationDescriptorModel._TYPECODE)){
            final RelationDescriptorModel rd = (RelationDescriptorModel) attributeDescriptorModel;
            if ("CMSNavigationNodeChildren".equals(rd.getRelationName())){
                return false;
            }
        }
        return true;
    }
}
