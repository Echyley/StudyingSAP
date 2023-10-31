/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.cxml.ItemID;
import org.cxml.ItemOut;
import org.cxml.SupplierPartID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemOutConverterTest
{
	private static final String SUPPLIER_PART_ID = "12345";
	private static final String TEST_QUANTITY = "10.0";

	@InjectMocks
	private DefaultItemOutConverter defaultItemOutConverter;

	@Mock
	private CartService cartService;
	@Mock
	private ProductService productService;
	@Mock
	private ProductModel productModel;
	@Mock
	private UnitModel unitModel;
	@Mock
	private CartModel cartModel;

	private ItemOut source;

	@Before
	public void setUp()
	{
		source = new ItemOut();
		source.setQuantity(TEST_QUANTITY);

		final ItemID itemID = new ItemID();
		final SupplierPartID supplierPartID = new SupplierPartID();

		supplierPartID.setvalue(SUPPLIER_PART_ID);
		itemID.setSupplierPartID(supplierPartID);

		source.setItemID(itemID);

		when(productService.getProductForCode(SUPPLIER_PART_ID)).thenReturn(productModel);
		when(productModel.getUnit()).thenReturn(unitModel);
		when(cartService.getSessionCart()).thenReturn(cartModel);
	}

	@Test
	public void testConversion()
	{
		defaultItemOutConverter.convert(source);

		verify(productService).getProductForCode(SUPPLIER_PART_ID);
		verify(cartService).addNewEntry(cartModel, productModel, 10, unitModel);
	}

	@Test
	public void testProductNotFound()
	{
		when(productService.getProductForCode(SUPPLIER_PART_ID)).thenThrow(new UnknownIdentifierException("mock product not found exception."));
		final PunchOutException punchOutException = assertThrows(PunchOutException.class, () -> defaultItemOutConverter.convert(source));
		assertThat(punchOutException).hasMessage("Product with code " + source.getItemID().getSupplierPartID().getvalue() + " is not found.")
				.hasFieldOrPropertyWithValue("errorCode", Integer.toString(HttpStatus.BAD_REQUEST.value()));
	}
}
