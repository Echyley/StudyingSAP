/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.order.hook;


import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BundleUpdateCartEntryHookTest
{
	@InjectMocks
	private final BundleUpdateCartEntryHook hook = new BundleUpdateCartEntryHook();
	@Mock
	private BundleCartValidator bundleCartValidator;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private ModelService modelService;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private BundleCartHookHelper bundleCartHookHelper;
	@Mock
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSkipNonBundleBefore()
	{
		final CommerceCartParameter parameter = createParameter();

		getHook().beforeUpdateCartEntry(parameter);

		verify(bundleTemplateService, never()).getBundleTemplateForCode(any(String.class));
	}

	@Test
	public void shouldFailIfComponentDoesNotExist()
	{
		final CommerceCartParameter parameter = createParameter();
		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleTemplateForCode(any())).willThrow(new ModelNotFoundException(""));
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bundle template TEST was not found");

		getHook().beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldLimitQuantityAccordingToNSelectionCriteria()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		final PickExactlyNBundleSelectionCriteriaModel selectionCriteria = new PickExactlyNBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);

		getHook().beforeUpdateCartEntry(parameter);

		assertEquals(10, parameter.getQuantity());
		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, parameter.getModificationStatus());
	}

	@Test
	public void shouldLimitQuantityAccordingToNSelectionCriteriaWithOtherProducts()
	{
		final CommerceCartParameter parameter = createParameterWithOtherProduct(2);
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		given(bundleTemplateService.getBundleEntryGroup(any(), any())).willReturn(group);

		final BundleTemplateModel component = new BundleTemplateModel();
		final PickExactlyNBundleSelectionCriteriaModel selectionCriteria = new PickExactlyNBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);

		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);
		getHook().beforeUpdateCartEntry(parameter);
		// result should be max - other products( 10 - 2 = 8)
		assertEquals(8, parameter.getQuantity());
		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, parameter.getModificationStatus());
	}

	@Test
	public void shouldLimitQuantityAccordingToNSelectionCriteriaWithOtherProductsHasBigQuantity()
	{
		final CommerceCartParameter parameter = createParameterWithOtherProduct(999);
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		given(bundleTemplateService.getBundleEntryGroup(any(), any())).willReturn(group);

		final BundleTemplateModel component = new BundleTemplateModel();
		final PickExactlyNBundleSelectionCriteriaModel selectionCriteria = new PickExactlyNBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);

		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);
		getHook().beforeUpdateCartEntry(parameter);
		// result should be positive even the result is negative( 10 - 999)
		assertEquals(1, parameter.getQuantity());
		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, parameter.getModificationStatus());
	}
	@Test
	public void shouldLimitQuantityAccordingToNtoMSelectionCriteria()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		final PickNToMBundleSelectionCriteriaModel selectionCriteria = new PickNToMBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(0));
		selectionCriteria.setM(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);

		getHook().beforeUpdateCartEntry(parameter);

		assertEquals(10, parameter.getQuantity());
		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, parameter.getModificationStatus());
	}

	@Test
	public void shouldSkipNonBundleAfter()
	{
		final CommerceCartParameter parameter = createParameter();

		getHook().afterUpdateCartEntry(parameter, null);

		verify(entryGroupService, never()).getRoot(any(), any());
	}

	@Test
	public void shouldReValidateTheWholeBundle()
	{
		final CommerceCartParameter parameter = createParameter();
		prepareForRevalidateBundle(parameter);

		getHook().afterUpdateCartEntry(parameter, null);

		verify(bundleCartValidator).updateErroneousGroups(any(), any());
		verify(bundleCartHookHelper).invalidateBundleEntries(any(), any());
	}

	@Test
	public void shouldUpdateModificationStatusWhenPreviousValueIsNull()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setModificationStatus(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED);
		final CommerceCartModification cartModification = new CommerceCartModification();
		prepareForRevalidateBundle(parameter);

		getHook().afterUpdateCartEntry(parameter, cartModification);

		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, cartModification.getStatusCode());
	}

	@Test
	public void shouldUpdateModificationStatusWhenPreviousValueIsSuccess()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setModificationStatus(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED);
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		prepareForRevalidateBundle(parameter);

		getHook().afterUpdateCartEntry(parameter, cartModification);

		assertEquals(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED, cartModification.getStatusCode());
	}

	@Test
	public void shouldNotUpdateModificationStatusWhenPreviousValueIsLowStock()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setModificationStatus(CommerceCartModificationStatus.MAX_BUNDLE_SELECTION_CRITERIA_LIMIT_EXCEEDED);
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		prepareForRevalidateBundle(parameter);

		getHook().afterUpdateCartEntry(parameter, cartModification);

		assertEquals(CommerceCartModificationStatus.LOW_STOCK, cartModification.getStatusCode());
	}

	@Test
	public void shouldNotUpdateModificationStatusWhenNoModificationStatusInParameter()
	{
		final CommerceCartParameter parameter = createParameter();
		final CommerceCartModification cartModification = new CommerceCartModification();
		cartModification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		prepareForRevalidateBundle(parameter);

		getHook().afterUpdateCartEntry(parameter, cartModification);

		assertEquals(CommerceCartModificationStatus.LOW_STOCK, cartModification.getStatusCode());
	}

	protected void prepareForRevalidateBundle(final CommerceCartParameter parameter)
	{
		parameter.getCart().setCalculated(Boolean.TRUE);
		parameter.getCart().getEntries().get(0).setCalculated(Boolean.TRUE);
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		final EntryGroup root = new EntryGroup();
		root.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(entryGroupService.getRoot(any(), any())).willReturn(root);
		given(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).willReturn(group);
		given(entryGroupService.getGroup(parameter.getCart(), Integer.valueOf(1))).willReturn(group);
		given(entryGroupService.getNestedGroups(root)).willReturn(Arrays.asList(root, group));
		given(commerceCartCalculationStrategy.calculateCart(any(CommerceCartParameter.class))).willReturn(Boolean.TRUE);
	}

	protected CommerceCartParameter createParameter()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(new CartModel());
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(1);
		entry.setProduct(new ProductModel());
		entry.getProduct().setCode("PRODUCT");
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(1)));
		entry.setOrder(parameter.getCart());
		parameter.getCart().setEntries(Collections.singletonList(entry));
		parameter.setEntryNumber(1);
		parameter.setQuantity(1L);
		return parameter;
	}

	protected CommerceCartParameter createParameterWithOtherProduct( long quantity) {
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(new CartModel());

		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		entry1.setEntryNumber(1);
		entry1.setProduct(new ProductModel());
		entry1.getProduct().setCode("PRODUCT");
		entry1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(2)));
		entry1.setOrder(parameter.getCart());

		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setEntryNumber(2);
		entry2.setProduct(new ProductModel());
		entry2.setQuantity(quantity);
		entry2.getProduct().setCode("PRODUCT-2");
		entry2.setEntryGroupNumbers(entry1.getEntryGroupNumbers());
		entry2.setOrder(parameter.getCart());

		parameter.getCart().setEntries(Arrays.asList( entry1, entry2));
		parameter.setEntryNumber(1);
		parameter.setQuantity(1L);
		return parameter;
	}

	protected BundleUpdateCartEntryHook getHook()
	{
		return hook;
	}
}
