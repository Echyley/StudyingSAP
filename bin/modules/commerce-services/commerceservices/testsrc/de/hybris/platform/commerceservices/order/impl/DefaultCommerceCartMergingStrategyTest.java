/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommerceCartMergingStrategyTest
{
	private final DefaultCommerceCartMergingStrategy mergingStrategy = new DefaultCommerceCartMergingStrategy();

	private static final String ONLY_LOGGED_USER_CAN_MERGE_CARTS = "Only logged user can merge carts";

	@Mock
	private UserService userService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ModelService modelService;
	@Mock
	private EntryMergeStrategy entryMergeStrategy;
	@Mock(lenient = true)
	private EntryGroupService entryGroupService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock(lenient = true)
	private CartModel toCart;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CartModel fromCart;

	@Before
	public void setUp() throws CommerceCartModificationException
	{
		mergingStrategy.setBaseSiteService(baseSiteService);
		mergingStrategy.setEntryMergeStrategy(entryMergeStrategy);
		mergingStrategy.setModelService(modelService);
		mergingStrategy.setUserService(userService);
		mergingStrategy.setCommerceCartService(commerceCartService);
		mergingStrategy.setEntryGroupService(entryGroupService);

		final UserModel userModel = new UserModel();
		given(userService.getCurrentUser()).willReturn(userModel);
		given(Boolean.valueOf(userService.isAnonymousUser(userModel))).willReturn(Boolean.FALSE);

		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		when(baseSiteModel.getName()).thenReturn("site1");
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);

		fromCart = new CartModel();
		fromCart.setGuid("guidFromCart");
		fromCart.setEntries(Collections.emptyList());
		fromCart.setEntryGroups(Collections.emptyList());
		fromCart.setSite(baseSiteModel);

		when(toCart.getSite()).thenReturn(baseSiteModel);
		when(toCart.getGuid()).thenReturn("guidToCart");

		when(commerceCartService.updateQuantityForCartEntry(any(CommerceCartParameter.class))).then(invocationOnMock -> {
			final CommerceCartParameter parameter = (CommerceCartParameter) invocationOnMock.getArguments()[0];
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setQuantity(parameter.getQuantity());
			final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
			entry.setOrder(parameter.getCart());
			entry.setEntryNumber(Integer.valueOf((int) parameter.getEntryNumber()));
			entry.setQuantity(Long.valueOf(parameter.getQuantity()));
			modification.setEntry(entry);
			return modification;
		});
		when(modelService.clone(any(AbstractOrderEntryModel.class), (Class<? extends AbstractOrderEntryModel>) any(Class.class)))
				.thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
	}

	@Test
	public void shouldNotMergeForAnonymousUser() throws CommerceCartMergingException
	{
		given(Boolean.valueOf(userService.isAnonymousUser(any()))).willReturn(Boolean.TRUE);
		final List<CommerceCartModification> list = new ArrayList<>();
		thrown.expect(AccessDeniedException.class);
		thrown.expectMessage(ONLY_LOGGED_USER_CAN_MERGE_CARTS);
		mergingStrategy.mergeCarts(fromCart, toCart, list);
	}

	@Test
	public void shouldNotMergeForNullUser() throws CommerceCartMergingException
	{
		given(userService.getCurrentUser()).willReturn(null);
		final List<CommerceCartModification> list = new ArrayList<>();
		thrown.expect(AccessDeniedException.class);
		thrown.expectMessage(ONLY_LOGGED_USER_CAN_MERGE_CARTS);
		mergingStrategy.mergeCarts(fromCart, toCart, list);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckTheSourceCartIsNotNull() throws CommerceCartMergingException
	{
		mergingStrategy.mergeCarts(null, toCart, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckTheTargetCartIsNotNull() throws CommerceCartMergingException
	{
		mergingStrategy.mergeCarts(fromCart, null, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckTheModificationListIsNotNull() throws CommerceCartMergingException
	{
		mergingStrategy.mergeCarts(fromCart, toCart, null);
	}

	@Test
	public void shouldToCartSiteEqualWithCurrentSite() throws CommerceCartMergingException
	{
		final BaseSiteModel site = mock(BaseSiteModel.class);
		when(site.getName()).thenReturn("another site");
		when(toCart.getSite()).thenReturn(site);
		when(toCart.getCode()).thenReturn("target");
		thrown.expect(CommerceCartMergingException.class);
		thrown.expectMessage("Current site site1 is not equal to cart target site another site");
		mergingStrategy.mergeCarts(fromCart, toCart, Collections.emptyList());
	}

	@Test
	public void shouldFromCartSiteEqualWithCurrentSite() throws CommerceCartMergingException
	{
		final BaseSiteModel site = mock(BaseSiteModel.class);
		when(site.getName()).thenReturn("a wrong site");
		final CartModel cart = spy(fromCart);
		when(cart.getSite()).thenReturn(site);
		when(cart.getCode()).thenReturn("source");
		thrown.expect(CommerceCartMergingException.class);
		thrown.expectMessage("Current site site1 is not equal to cart source site a wrong site");
		mergingStrategy.mergeCarts(cart, toCart, Collections.emptyList());
	}

	@Test
	public void shouldCheckThatTheCartsAreDistinct() throws CommerceCartMergingException
	{
		thrown.expect(CommerceCartMergingException.class);
		thrown.expectMessage("Cannot merge cart to itself");
		mergingStrategy.mergeCarts(toCart, toCart, Collections.emptyList());
	}

	@Test
	public void shouldSaveTheToCart() throws CommerceCartMergingException
	{
		when(toCart.getEntries()).thenReturn(Collections.emptyList());
		mergingStrategy.mergeCarts(fromCart, toCart, Collections.emptyList());
		verify(modelService).save(toCart);
	}

	@Test
	public void shouldRemoveTheFromCart() throws CommerceCartMergingException
	{
		when(toCart.getEntries()).thenReturn(Collections.emptyList());
		mergingStrategy.mergeCarts(fromCart, toCart, Collections.emptyList());
		verify(modelService).remove(fromCart);
	}

	@Test
	public void shouldInvokeUpdateQuantityForCartEntryIfThereIsEntryToMerge()
			throws CommerceCartMergingException, CommerceCartModificationException
	{
		final AbstractOrderEntryModel fromCartEntry = new AbstractOrderEntryModel();
		fromCartEntry.setEntryNumber(Integer.valueOf(1));
		fromCartEntry.setQuantity(Long.valueOf(1L));
		fromCart.setEntries(Collections.singletonList(fromCartEntry));
		final AbstractOrderEntryModel toCartEntry = new AbstractOrderEntryModel();
		toCartEntry.setEntryNumber(Integer.valueOf(0));
		toCartEntry.setQuantity(Long.valueOf(1L));
		when(entryMergeStrategy.getEntryToMerge(toCart.getEntries(), fromCartEntry)).thenReturn(toCartEntry);
		final List<CommerceCartModification> modifications = new ArrayList<>();
		mergingStrategy.mergeCarts(fromCart, toCart, modifications);

		assertEquals(1, modifications.size());
		assertTrue(fromCart.getEntries().isEmpty());
		verify(commerceCartService).updateQuantityForCartEntry(any(CommerceCartParameter.class));
	}

	@Test
	public void shouldMergeEntriesIfThereIsEntryToMerge() throws CommerceCartMergingException, CommerceCartModificationException
	{
		final AbstractOrderEntryModel fromCartEntry = new AbstractOrderEntryModel();
		fromCartEntry.setEntryNumber(Integer.valueOf(1));
		fromCartEntry.setQuantity(Long.valueOf(1L));
		final AbstractOrderEntryModel toCartEntry = new AbstractOrderEntryModel();
		toCartEntry.setEntryNumber(Integer.valueOf(2));
		toCartEntry.setQuantity(Long.valueOf(3L));
		fromCart.setEntries(Collections.singletonList(fromCartEntry));
		when(entryMergeStrategy.getEntryToMerge(any(), any())).thenReturn(toCartEntry);
		final List<CommerceCartModification> modifications = new ArrayList<>();
		mergingStrategy.mergeCarts(fromCart, toCart, modifications);

		assertEquals(1, modifications.size());
		assertEquals(toCartEntry.getEntryNumber(), modifications.get(0).getEntry().getEntryNumber());
		assertEquals(4, modifications.get(0).getQuantity());
	}

	@Test
	public void shouldAddClonedEntryToCartAndResolveEntryNumberConflictsIfThereIsNoEntryToMerge()
			throws CommerceCartMergingException
	{
		final AbstractOrderEntryModel fromCartEntry = new AbstractOrderEntryModel();
		fromCartEntry.setEntryNumber(Integer.valueOf(1));
		fromCartEntry.setQuantity(Long.valueOf(1L));
		final AbstractOrderEntryModel toCartEntry = new AbstractOrderEntryModel();
		toCartEntry.setEntryNumber(Integer.valueOf(1));
		toCartEntry.setQuantity(Long.valueOf(1L));
		fromCart.setEntries(Collections.singletonList(fromCartEntry));
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		cart.setEntries(Collections.singletonList(toCartEntry));
		when(entryMergeStrategy.getEntryToMerge(any(), any())).thenReturn(null);
		final List<CommerceCartModification> modifications = new ArrayList<>();
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertEquals(2, cart.getEntries().size());
		assertNotEquals(cart.getEntries().get(0).getEntryNumber(), cart.getEntries().get(1).getEntryNumber());
	}

	@Test
	public void shouldCopyEntryGroupsWhenToCartEntryGroupsIsEmpty() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		final List<CommerceCartModification> modifications = new ArrayList<>();
		fromCart.setEntryGroups(Collections.singletonList(new EntryGroup()));
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertThat(cart.getEntryGroups(), iterableWithSize(1));
	}

	@Test
	public void shouldCopyEntryGroupsWhenToCartEntryGroupsIsNotEmpty() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		final EntryGroup toCartEntryGroup = new EntryGroup();
		cart.setEntryGroups(Collections.singletonList(toCartEntryGroup));
		final List<CommerceCartModification> modifications = new ArrayList<>();
		final EntryGroup fromCartEntryGroup = new EntryGroup();
		fromCart.setEntryGroups(Collections.singletonList(fromCartEntryGroup));
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertThat(cart.getEntryGroups(), iterableWithSize(2));
	}

	@Test
	public void shouldNotCopyEntryGroupsWhenFromCartEntryGroupsIsEmpty() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		final EntryGroup toCartEntryGroup = new EntryGroup();
		cart.setEntryGroups(Collections.singletonList(toCartEntryGroup));
		final List<CommerceCartModification> modifications = new ArrayList<>();
		fromCart.setEntryGroups(Collections.EMPTY_LIST);
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertThat(cart.getEntryGroups(), iterableWithSize(1));
	}

	@Test
	public void shouldResolveEntryGroupNumberConflicts() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		final List<CommerceCartModification> modifications = new ArrayList<>();
		final EntryGroup fromCartEntryGroup = new EntryGroup();
		fromCartEntryGroup.setGroupNumber(Integer.valueOf(1));
		fromCart.setEntryGroups(Collections.singletonList(fromCartEntryGroup));
		final EntryGroup toCartEntryGroup = new EntryGroup();
		toCartEntryGroup.setGroupNumber(Integer.valueOf(1));
		cart.setEntryGroups(Collections.singletonList(toCartEntryGroup));
		when(Integer.valueOf(entryGroupService.findMaxGroupNumber(any()))).thenReturn(Integer.valueOf(1));
		when(entryGroupService.getNestedGroups(fromCartEntryGroup)).thenReturn(Collections.singletonList(fromCartEntryGroup));
		when(entryGroupService.getNestedGroups(toCartEntryGroup)).thenReturn(Collections.singletonList(toCartEntryGroup));
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertThat(cart.getEntryGroups(), containsInAnyOrder(fromCartEntryGroup, toCartEntryGroup));
		assertNotEquals(cart.getEntryGroups().get(0).getGroupNumber(), cart.getEntryGroups().get(1).getGroupNumber());
	}

	@Test
	public void shouldReindexEntriesAlongWithGroups() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		cart.setSite(fromCart.getSite());
		final List<CommerceCartModification> modifications = new ArrayList<>();
		final EntryGroup fromCartEntryGroup = new EntryGroup();
		fromCartEntryGroup.setGroupNumber(Integer.valueOf(1));
		fromCart.setEntryGroups(Collections.singletonList(fromCartEntryGroup));
		final EntryGroup toCartEntryGroup = new EntryGroup();
		toCartEntryGroup.setGroupNumber(Integer.valueOf(1));
		cart.setEntryGroups(Collections.singletonList(toCartEntryGroup));
		final AbstractOrderEntryModel fromCartEntry = new AbstractOrderEntryModel();
		fromCartEntry.setEntryNumber(Integer.valueOf(100));
		fromCartEntry.setQuantity(Long.valueOf(1L));
		fromCartEntry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		fromCart.setEntries(Collections.singletonList(fromCartEntry));
		final AbstractOrderEntryModel toCartEntry = new AbstractOrderEntryModel();
		toCartEntry.setEntryNumber(Integer.valueOf(101));
		toCartEntry.setQuantity(Long.valueOf(1L));
		toCartEntry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		cart.setEntries(Collections.singletonList(toCartEntry));
		when(Integer.valueOf(entryGroupService.findMaxGroupNumber(any()))).thenReturn(Integer.valueOf(1));
		when(entryGroupService.getNestedGroups(fromCartEntryGroup)).thenReturn(Collections.singletonList(fromCartEntryGroup));
		when(entryGroupService.getNestedGroups(toCartEntryGroup)).thenReturn(Collections.singletonList(toCartEntryGroup));
		mergingStrategy.mergeCarts(fromCart, cart, modifications);

		assertThat(cart.getEntryGroups(), iterableWithSize(2));
		assertNotEquals(cart.getEntryGroups().get(0).getGroupNumber(), cart.getEntryGroups().get(1).getGroupNumber());
		assertThat(toCartEntry.getEntryGroupNumbers(), contains(Integer.valueOf(1)));
		assertThat(fromCartEntry.getEntryGroupNumbers(), not(contains(Integer.valueOf(1))));
	}

	@Test
	public void testValidationBeforeMergeResultWithFreeGift()
	{
		final CartModel fromCartLocal = new CartModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final AbstractOrderEntryModel normalEntry = new AbstractOrderEntryModel();
		normalEntry.setGiveAway(false);
		orderEntries.add(normalEntry);
		final AbstractOrderEntryModel normalEntry1 = new AbstractOrderEntryModel();
		orderEntries.add(normalEntry1);

		final AbstractOrderEntryModel freeGiftEntry = new AbstractOrderEntryModel();
		freeGiftEntry.setGiveAway(true);
		orderEntries.add(freeGiftEntry);

		fromCartLocal.setEntries(orderEntries);
		mergingStrategy.validationBeforeMergeResult(fromCartLocal);

		assertEquals(2, fromCartLocal.getEntries().size());
		assertFalse(fromCartLocal.getEntries().get(0).getGiveAway());
		assertFalse(fromCartLocal.getEntries().get(1).getGiveAway());

	}

	@Test
	public void testValidationBeforeMergeResultWithoutFreeGift()
	{
		final CartModel fromCartLocal = new CartModel();
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
		final AbstractOrderEntryModel normalEntry = new AbstractOrderEntryModel();
		normalEntry.setGiveAway(false);
		orderEntries.add(normalEntry);

		fromCartLocal.setEntries(orderEntries);
		mergingStrategy.validationBeforeMergeResult(fromCartLocal);

		assertEquals(1, fromCartLocal.getEntries().size());
		assertFalse(fromCartLocal.getEntries().get(0).getGiveAway());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidationBeforeMergeResultEmptyFromCart() throws CommerceCartMergingException
	{
		final CartModel fromCartLocal = new CartModel();
		fromCartLocal.setEntries(null);
		mergingStrategy.validationBeforeMergeResult(fromCartLocal);
	}

	@Test
	public void shouldFilterOutGiveAwayEntriesInFromCart() throws CommerceCartMergingException
	{
		// add one regular and one giveaway entry
		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class, withSettings().lenient());
		when(baseSiteModel.getName()).thenReturn("site1");
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSiteModel);

		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		fromCart.setGuid("guidFromCartLocal");
		toCart.setGuid("guidToCartLocal");
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
	
		final AbstractOrderEntryModel regularFromCartEntry = new AbstractOrderEntryModel();
		regularFromCartEntry.setEntryNumber(Integer.valueOf(1));
		regularFromCartEntry.setQuantity(Long.valueOf(1L));

		final AbstractOrderEntryModel giveawayFromCartEntry = new AbstractOrderEntryModel();
		giveawayFromCartEntry.setEntryNumber(Integer.valueOf(2));
		giveawayFromCartEntry.setQuantity(Long.valueOf(1L));
		giveawayFromCartEntry.setGiveAway(Boolean.TRUE);

		final AbstractOrderEntryModel toCartEntry = new AbstractOrderEntryModel();
		toCartEntry.setEntryNumber(Integer.valueOf(1));
		toCartEntry.setQuantity(Long.valueOf(3L));
		fromCart.setEntries(Arrays.asList(regularFromCartEntry, giveawayFromCartEntry));
		when(entryMergeStrategy.getEntryToMerge(any(), any())).thenReturn(toCartEntry);
		final List<CommerceCartModification> modifications = new ArrayList<>();
		mergingStrategy.mergeCarts(fromCart, toCart, modifications);

		assertEquals(1, modifications.size());
		assertEquals(toCartEntry.getEntryNumber(), modifications.get(0).getEntry().getEntryNumber());
		assertFalse(modifications.get(0).getEntry().getGiveAway());
		assertEquals(4, modifications.get(0).getQuantity());
	}
}
