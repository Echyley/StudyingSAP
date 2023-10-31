/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.custom.impl;

import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.locking.ItemLockingService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class LockedItemPermissionAdvisorTest extends AbstractCockpitngUnitTest<LockedItemPermissionAdvisor>
{

	@Spy
	@InjectMocks
	private LockedItemPermissionAdvisor advisor;

	@Mock
	private ItemLockingService itemLockingService;

	@Mock
	private ItemModel unLockedItem;

	@Mock
	private ItemModel lockedItem;

	@Mock
	private ItemModel deletedItem;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		when(modelService.isRemoved(deletedItem)).thenReturn(true);
		when(modelService.isRemoved(argThat(new ArgumentMatcher<ItemModel>()
		{
			@Override
			public boolean matches(ItemModel itemModel) {
				return itemModel != deletedItem;
			}
		}))).thenReturn(false);
		when(itemLockingService.isLocked(lockedItem)).thenReturn(true);
		when(itemLockingService.isLocked(argThat(new ArgumentMatcher<ItemModel>()
		{
			@Override
			public boolean matches(ItemModel itemModel) {
				return itemModel != lockedItem;
			}
		}))).thenReturn(false);
	}

	@Test
	public void advisorFollowsService()
	{
		final boolean deleteLockedResult = advisor.canDelete(lockedItem);
		final boolean modifyLockedResult = advisor.canModify(lockedItem);

		final boolean deleteUnLockedResult = advisor.canDelete(unLockedItem);
		final boolean modifyUnLockedResult = advisor.canModify(unLockedItem);

		assertThat(deleteLockedResult).isFalse();
		assertThat(modifyLockedResult).isFalse();

		assertThat(deleteUnLockedResult).isTrue();
		assertThat(modifyUnLockedResult).isTrue();
	}

	@Test
	public void isApplicableTo()
	{
		final boolean applicableToString = advisor.isApplicableTo("String");
		final boolean applicableToObject = advisor.isApplicableTo(new Object());
		final boolean applicableToItemModel = advisor.isApplicableTo(new ItemModel());
		final boolean applicableToProductModel = advisor.isApplicableTo(new ProductModel());
		final boolean applicableToDeletedObject = advisor.isApplicableTo(deletedItem);

		assertThat(applicableToString).isFalse();
		assertThat(applicableToObject).isFalse();
		assertThat(applicableToDeletedObject).isFalse();

		assertThat(applicableToItemModel).isTrue();
		assertThat(applicableToProductModel).isTrue();
	}

}
