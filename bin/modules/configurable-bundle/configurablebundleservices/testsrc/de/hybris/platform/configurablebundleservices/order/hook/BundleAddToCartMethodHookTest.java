/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.order.hook;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultCartBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@UnitTest
public class BundleAddToCartMethodHookTest
{
	@Mock
	protected DefaultCartBundleComponentEditableChecker editableChecker;
	@Mock
	protected L10NService l10NService;
	@Mock
	protected ModelService modelService;
	@Mock
	protected CartService cartService;
	@Mock
	protected BundleTemplateService bundleTemplateService;
	@Mock
	protected BundleRuleService bundleRuleService;
	@Mock
	protected EntryGroupService entryGroupService;
	@Mock
	protected BundleCartValidator bundleCartValidator;
	@Mock
	protected BundleCartHookHelper bundleCartHookHelper;
	@Mock
	protected DefaultCartBundleComponentEditableChecker bundleComponentEditableChecker;

	@InjectMocks
	protected BundleAddToCartMethodHook hook;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	MockitoSession mockito;

	@Before
	public void setUp() throws Exception
	{
		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.startMocking();
	}

	@After
	public void tearDown()
	{

		hook.setAutoPickEnabled(false);
		//It is necessary to finish the session so that Mockito
		// can detect incorrect stubbing and validate Mockito usage
		//'finishMocking()' is intended to be used in your test framework's 'tear down' method.
		mockito.finishMocking();
	}

	@Test
	public void shouldSkipNotBundledCalls() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setProduct(new ProductModel());
		parameter.getProduct().setSoldIndividually(Boolean.TRUE);
		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldDenyAddingOfNonIndividualProducts() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setProduct(new ProductModel());
		parameter.getProduct().setSoldIndividually(Boolean.FALSE);
		thrown.expect(CommerceCartModificationException.class);
		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldCheckTheComponentExists() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(new CartModel());
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setProduct(new ProductModel());
		parameter.getProduct().setSoldIndividually(Boolean.TRUE);

		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("A");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		given(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).willReturn(group);
		given(bundleTemplateService.getBundleTemplateForCode("A")).willThrow(new ModelNotFoundException(""));

		thrown.expect(CommerceCartModificationException.class);

		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldCheckTheComponentBelongToTheRightTemplate() throws CommerceCartModificationException
	{
		final ProductModel product = new ProductModel();
		product.setCode("test");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.singletonList(entry));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		final BundleTemplateModel component2 = new BundleTemplateModel();
		component2.setId("B");
		parameter.setBundleTemplate(component1);
		parameter.setCart(cart);
		parameter.setProduct(product);
		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("B");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(cart, parameter.getEntryGroupNumbers())).thenReturn(group);
		Mockito.lenient().when(bundleTemplateService.getRootBundleTemplate(component1)).thenReturn(new BundleTemplateModel());
		Mockito.lenient().when(bundleTemplateService.getRootBundleTemplate(component2)).thenReturn(component2);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateName(component1)).thenReturn(component1.getId());
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateName(component2)).thenReturn(component2.getId());
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The given bundle A is not equal to the component B stored in entry group #1");
		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldAssignBundleTemplate() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.emptyList());

		final ProductModel product = new ProductModel();
		product.setCode("test");

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		component1.setProducts(Collections.singletonList(product));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setCart(cart);
		parameter.setProduct(product);

		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("A");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		given(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).willReturn(group);
		given(bundleTemplateService.getBundleTemplateForCode("A")).willReturn(component1);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(cart, component1, 1)).willReturn(true);

		hook.beforeAddToCart(parameter);

		assertEquals(component1, parameter.getBundleTemplate());
	}

	@Test
	public void shouldNotCheckRequiredComponentNullEntryGroup() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.emptyList());

		final ProductModel product = new ProductModel();
		product.setCode("test");

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		component1.setProducts(Collections.singletonList(product));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setBundleTemplate(component1);

		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).thenReturn(null);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("A")).thenReturn(component1);

		hook.beforeAddToCart(parameter);

		verifyNoMoreInteractions(bundleComponentEditableChecker);
		assertEquals(component1, parameter.getBundleTemplate());
	}

	@Test
	public void shouldNotAllowInvalidComponentNullEntryGroup() throws CommerceCartModificationException
	{
		thrown.expect(CommerceCartModificationException.class);

		final CartModel cart = new CartModel();
		cart.setEntries(Collections.emptyList());

		final ProductModel product = new ProductModel();
		product.setCode("test");

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		final BundleTemplateModel requiredComponent = new BundleTemplateModel();
		component1.setProducts(Collections.singletonList(product));
		component1.setRequiredBundleTemplates(Collections.singletonList(requiredComponent));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setBundleTemplate(component1);

		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).thenReturn(null);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("A")).thenReturn(component1);

		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldNotAllowInvalidRequiredComponents() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.emptyList());

		final ProductModel product = new ProductModel();
		product.setCode("test");

		final BundleTemplateModel component2 = new BundleTemplateModel();
		component2.setId("B");

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		component1.setProducts(Collections.singletonList(product));
		component1.setRequiredBundleTemplates(List.of(component2));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setCart(cart);
		parameter.setProduct(product);

		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("A");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		given(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).willReturn(group);
		given(bundleTemplateService.getBundleTemplateForCode("A")).willReturn(component1);
		given(bundleTemplateService.getBundleTemplateName(component2)).willReturn("Component");
		given(bundleComponentEditableChecker.isRequiredDependencyMet(cart, component1, 1)).willReturn(false);

		Assert.assertThrows(CommerceCartModificationException.class, () -> hook.beforeAddToCart(parameter));
		assertEquals(component1, parameter.getBundleTemplate());
	}

	@Test
	public void shouldCheckThatTheProductIsAllowed() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.emptyList());

		final ProductModel product = new ProductModel();
		product.setCode("test");

		final BundleTemplateModel component1 = new BundleTemplateModel();
		component1.setId("A");
		component1.setProducts(Collections.emptyList());

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setBundleTemplate(component1);

		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("A");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		Mockito.lenient().when(entryGroupService.getGroup(any(), any())).thenReturn(group);
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode("A")).thenReturn(component1);

		thrown.expect(CommerceCartModificationException.class);

		hook.beforeAddToCart(parameter);
	}

	@Test
	public void shouldCreateBundleTreeInCart() throws CommerceCartModificationException
	{
		// WHEN a new bundle is started
		final ProductModel product = new ProductModel();
		product.setCode("test");

		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setProduct(product);
		entry.setQuantity(Long.valueOf(1L));

		final CartModel cart = new CartModel();
		cart.setEntries(Collections.singletonList(entry));

		final BundleTemplateModel component1 = spy(new BundleTemplateModel());
		component1.setId("A");
		component1.setProducts(Collections.singletonList(product));

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setBundleTemplate(component1);

		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(entry);
		modification.setQuantityAdded(1L);

		entry.setOrder(cart);

		Mockito.lenient().when(bundleTemplateService.getRootBundleTemplate(component1)).thenReturn(component1);
		Mockito.lenient().when(Integer.valueOf(entryGroupService.findMaxGroupNumber(any()))).thenReturn(Integer.valueOf(0));

		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("A");
		group.setGroupNumber(Integer.valueOf(1));
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		Mockito.lenient().when(bundleTemplateService.getBundleEntryGroup(any(), any())).thenReturn(group);
		given(entryGroupService.getLeaves(any())).willAnswer(invocation -> Collections.singletonList(invocation.getArguments()[0]));
		Mockito.lenient().when(bundleTemplateService.createBundleTree(any(), any())).thenReturn(group);
		doAnswer((invocationOnMock) -> new HashSet<>(Collections.singletonList(Integer.valueOf(1)))
		).when(bundleCartHookHelper).union(any(), any());

		hook.afterAddToCart(parameter, modification);

		// THEN the entry should be attached to the right group
		assertThat(entry.getEntryGroupNumbers(), contains(group.getGroupNumber()));
		// AND the cart should be invalidated
		assertFalse(cart.getCalculated().booleanValue());
		assertFalse(entry.getCalculated().booleanValue());
		verify(bundleCartValidator).updateErroneousGroups(any(), any());
		verify(entryGroupService, times(1)).forceOrderSaving(cart);
		// AND the modification should be updated
		assertEquals(parameter.getEntryGroupNumbers(), modification.getEntryGroupNumbers());
	}

	@Test
	public void shouldUpdateLeafComponentsErroneousStatus() throws Exception
	{
		final CartModel cart = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setOrder(cart);
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setCart(cart);
		commerceCartParameter.setBundleTemplate(bundleTemplate);
		final Set<Integer> entryGroupNumbers = new HashSet<>(Collections.singletonList(1));
		commerceCartParameter.setEntryGroupNumbers(entryGroupNumbers);
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(2);
		final EntryGroup rootGroup = new EntryGroup();
		final EntryGroup leafGroup1 = new EntryGroup();
		final EntryGroup leafGroup2 = new EntryGroup();
		final List<EntryGroup> leafGroups = Arrays.asList(leafGroup1, leafGroup2);

		when(bundleTemplateService.getBundleEntryGroup(eq(cart), eq(entryGroupNumbers)))
				.thenReturn(entryGroup);
		when(entryGroupService.getRoot(eq(cart), eq(2))).thenReturn(rootGroup);
		when(entryGroupService.getLeaves(eq(rootGroup))).thenReturn(leafGroups);
		when(bundleCartValidator.updateErroneousGroups(eq(leafGroups), eq(cart))).thenReturn(true);

		hook.addToBundle(commerceCartParameter, entry);

		verify(bundleCartValidator).updateErroneousGroups(leafGroups, cart);
	}

	@Test
	public void shouldDenyAddingAutoPickProducts() throws CommerceCartModificationException
	{
		hook.setAutoPickEnabled(true);
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final ProductModel product = new ProductModel();
		parameter.setProduct(product);
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setProducts(List.of(product));

		parameter.setBundleTemplate(bundleTemplate);
		parameter.setCart(new CartModel());
		parameter.setEntryGroupNumbers(Set.of(1));
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setExternalReferenceId("1");
		given(bundleTemplateService.getBundleEntryGroup(any(), any())).willReturn(entryGroup);
		given(bundleTemplateService.getBundleTemplateForCode(any())).willReturn(bundleTemplate);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), any(), any())).willReturn(true);
		given(bundleTemplateService.isAutoPickComponent(any())).willReturn(true);

		thrown.expect(CommerceCartModificationException.class);
		hook.beforeAddToCart(parameter);
	}

}
