/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.accessors;

import static org.mockito.BDDMockito.*;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.model.impl.DefaultModelValueHandler;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class JaloPropertyNotVisibleInModelAccessorTest
{
	private static final String ITEM_ATTRIBUTE = "abc";
	private static final String PRODUCT_ATTRIBUTE = "def";
	private static final ItemModel itemModel = new ItemModel();

	@Mock
	private ModelService modelService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private PermissionFacade permissionFacade;
	private ProductModel productModel = new ProductModel();
	@InjectMocks
	private final JaloPropertyNotVisibleInModelAccessor jaloPropertyNotVisibleInModelAccessor = new JaloPropertyNotVisibleInModelAccessor();
	@Before
	public void before() throws JaloSecurityException
	{
		productModel = mock(ProductModel.class);
	}

	/**
	 * "abc" -> {EmployeeModel, ItemModel, UserModel}, "def" -> {ProductModel}
	 */
	protected Map<String, Set<Class>> getDefaultSupportedJaloAttributes()
	{
		final Map<String, Set<Class>> map = new HashMap<>();
		final Set<Class> itemSet = new HashSet<>();
		final Set<Class> productSet = new HashSet<>();
		itemSet.add(EmployeeModel.class);
		itemSet.add(ItemModel.class);
		itemSet.add(UserModel.class);

		productSet.add(ProductModel.class);
		map.put(ITEM_ATTRIBUTE, itemSet);
		map.put(PRODUCT_ATTRIBUTE, productSet);
		return map;
	}

	protected Map<String, Set<Class>> getNullJaloAttributes()
	{
		return null;
	}

	protected EvaluationContext getDefaultEvaluationContext()
	{
		final WidgetModel widgetModel = new DefaultWidgetModel(new HashMap<String, Object>(), new DefaultModelValueHandler());
		return new StandardEvaluationContext(widgetModel);
	}


	protected Object getCurrentObject()
	{
		return new Object();
	}

	@Test
	public void testCanReadWhenNoSupportedJaloAtrributes()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		final Object currentObject = getCurrentObject();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getNullJaloAttributes());
		try
		{
			//when
			final boolean canRead = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, currentObject, "aa");
			//then
			Assertions.assertThat(canRead).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenNoSupportedJaloAtrributes()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		final Object currentObject = getCurrentObject();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getNullJaloAttributes());
		try
		{
			//when
			final boolean canWrite = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, currentObject, "aa");
			//then
			Assertions.assertThat(canWrite).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanReadWhenDefaultJaloAttributesAndGrantedPermissions()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		doReturn(Boolean.TRUE).when(permissionFacade).canReadInstanceProperty(anyObject(), anyString());
		try
		{
			final boolean canReadItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelItemAttr).isTrue();
			final boolean canReadProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext,
					productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelItemAttr).isTrue();
			final boolean canReadProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext,
					productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelProductAttr).isTrue();
			final boolean canReadItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelProductAttr).isFalse();
			//
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenDefaultJaloAttributesAndGrantedPermissions() throws TypeNotFoundException
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		doReturn(Boolean.TRUE).when(permissionFacade).canChangeInstanceProperty(anyObject(), anyString());

		final DataType dataType = mock(DataType.class);
		final DataAttribute dataAttributeNotWritable = mock(DataAttribute.class);
		doReturn(Boolean.FALSE).when(dataAttributeNotWritable).isWritable();

		final DataAttribute dataAttributeWritable = mock(DataAttribute.class);
		doReturn(Boolean.TRUE).when(dataAttributeWritable).isWritable();

		when(dataType.getAttribute(any())).thenReturn(dataAttributeNotWritable);
		when(typeFacade.load(any())).thenReturn(dataType);

		try
		{
			boolean canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isFalse();
			boolean canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isFalse();
			boolean canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isFalse();
			boolean canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();

			//
			when(dataType.getAttribute(anyString())).thenReturn(dataAttributeWritable);
			//
			canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isTrue();
			canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isTrue();
			canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, productModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isTrue();
			canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();

		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanReadWhenDefaultJaloAttributesAndRevokedPerimissions()
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		doReturn(Boolean.FALSE).when(permissionFacade).canReadInstanceProperty(anyObject(), anyString());
		try
		{
			final boolean canReadItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelItemAttr).isFalse();
			final boolean canReadProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext,
					productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelItemAttr).isFalse();
			final boolean canReadProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext,
					productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadProductModelProductAttr).isFalse();
			final boolean canReadItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canRead(evaluationContext, itemModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canReadItemModelProductAttr).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}

	@Test
	public void testCanWriteWhenDefaultJaloAttributesAndRevokedPerimissions() throws TypeNotFoundException
	{
		//given
		final EvaluationContext evaluationContext = getDefaultEvaluationContext();
		jaloPropertyNotVisibleInModelAccessor.setSupportedJaloAttributes(getDefaultSupportedJaloAttributes());
		doReturn(Boolean.FALSE).when(permissionFacade).canChangeInstanceProperty(anyObject(), anyString());
		final DataType dataType = mock(DataType.class);
		final DataAttribute dataAttributeWritable = mock(DataAttribute.class);
		doReturn(Boolean.TRUE).when(dataAttributeWritable).isWritable();


		when(dataType.getAttribute(any())).thenReturn(dataAttributeWritable);
		when(typeFacade.load(any())).thenReturn(dataType);

		try
		{
			final boolean canWriteItemModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel,
					ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelItemAttr).isFalse();
			final boolean canWriteProductModelItemAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext,
					productModel, ITEM_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelItemAttr).isFalse();
			final boolean canWriteProductModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext,
					productModel, PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteProductModelProductAttr).isFalse();
			final boolean canWriteItemModelProductAttr = jaloPropertyNotVisibleInModelAccessor.canWrite(evaluationContext, itemModel,
					PRODUCT_ATTRIBUTE);
			Assertions.assertThat(canWriteItemModelProductAttr).isFalse();
		}
		catch (final AccessException ex)
		{
			Assert.fail("Thrown exception: " + ex);
		}
	}
}
