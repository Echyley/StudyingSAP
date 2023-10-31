/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;

import com.hybris.backoffice.attributechooser.Attribute;


public class BulkEditSelectedAttributesHelperTest
{

	BulkEditSelectedAttributesHelper helper = new BulkEditSelectedAttributesHelper();

	@Test
	public void shouldReturnAttributeWhenItIsAlreadyLeaf()
	{
		// given
		final Attribute attribute = new Attribute("First", "First", false);

		// when
		final Collection<Attribute> leaves = helper.findLeaves(Arrays.asList(attribute));

		// then
		assertThat(leaves).containsExactly(attribute);
	}

	@Test
	public void shouldFindLeavesOnDeeperLevel()
	{
		// given
		final Attribute attributeL1 = new Attribute("L1", "L1", false);
		final Attribute attributeL2A = new Attribute("L2A", "L2A", false);
		final Attribute attributeL2B = new Attribute("L2B", "L2B", false);
		final Attribute attributeL2C = new Attribute("L2C", "L2C", false);
		attributeL1.setSubAttributes(Set.of(attributeL2A, attributeL2B, attributeL2C));
		final Attribute attributeL3A = new Attribute("L3A", "L3A", false);
		final Attribute attributeL3B = new Attribute("L3B", "L3B", false);
		final Attribute attributeL3C = new Attribute("L3C", "L3C", false);
		attributeL2A.setSubAttributes(Set.of(attributeL3C, attributeL3B));
		attributeL2C.setSubAttributes(Set.of(attributeL3A));

		// when
		final Collection<Attribute> leaves = helper.findLeaves(Arrays.asList(attributeL1));

		// then
		assertThat(leaves).containsExactlyInAnyOrder(attributeL2B, attributeL3A, attributeL3B, attributeL3C);
	}

}
