/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.cmsfacades.common.predicate.attributes.IsNavigationNodeRelationPredicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class IsNavigationNodeRelationPredicateTest
{
    @Mock
    private RelationDescriptorModel relationDescriptorModel;

    @InjectMocks
    private IsNavigationNodeRelationPredicate predicate;

    @Before
    public void setup()
    {
        doReturn(RelationDescriptorModel._TYPECODE).when(relationDescriptorModel).getItemtype();
    }

    @Test
    public void predicateShouldFailWhenIsNavigationChildrenRelation()
    {
        // Arrange
        doReturn("CMSNavigationNodeChildren").when(relationDescriptorModel).getRelationName();
        // Act
        boolean result = predicate.test(relationDescriptorModel);

        // Assert
        assertFalse(result);
    }

    @Test
    public void predicateShouldPassWhenIsNotNavigationChildrenRelation()
    {
        // Arrange
        doReturn("OtherRelation").when(relationDescriptorModel).getRelationName();
        // Act
        boolean result = predicate.test(relationDescriptorModel);

        // Assert
        assertTrue(result);
    }

}
