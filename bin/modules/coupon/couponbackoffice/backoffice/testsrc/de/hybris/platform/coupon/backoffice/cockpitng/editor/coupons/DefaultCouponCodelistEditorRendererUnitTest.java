/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.coupon.backoffice.cockpitng.editor.coupons;

import static com.hybris.cockpitng.editor.commonreferenceeditor.AbstractReferenceEditor.PARENT_OBJECT;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.couponservices.jalo.MultiCodeCoupon;
import de.hybris.platform.couponservices.jalo.SingleCodeCoupon;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.Base;
import com.hybris.cockpitng.editor.commonreferenceeditor.ReferenceEditorLayout;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.spy;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponCodelistEditorRendererUnitTest
{
	private static final String CODE_SEARCH_TEXT = "CODE_SEARCH_TEXT";

	@Mock
	private EditorContext context;
	@Mock
	private Base config;
	@Mock
	private ReferenceEditorLayout editorLayout;

	private DefaultCouponCodeListEditorRenderer listEditorRenderer;


	@Before
	public void setUp()
	{
		listEditorRenderer = new DefaultCouponCodeListEditorRenderer<>();
		listEditorRenderer = spy(listEditorRenderer);
		Mockito.lenient().doReturn(config).when(listEditorRenderer).loadBaseConfiguration(nullable(String.class),
				nullable(WidgetInstanceManager.class));
		listEditorRenderer.createReferenceLayout(context);
	}

	@Test
	public void testUpdateReferencesListBoxModelNoParentObject() throws Exception
	{
		Mockito.lenient().when(context.getParameter(PARENT_OBJECT)).thenReturn(null);
		listEditorRenderer.updateReferencesListBoxModel(CODE_SEARCH_TEXT);
		assertThat(listEditorRenderer.getPageable()).isNull();
	}

	@Test
	public void testUpdateReferencesListBoxModelParentObjectIsOfNonSupportedType() throws Exception
	{
		Mockito.lenient().when(context.getParameter(PARENT_OBJECT)).thenReturn(new SingleCodeCoupon());
		listEditorRenderer.updateReferencesListBoxModel(CODE_SEARCH_TEXT);
		assertThat(listEditorRenderer.getPageable()).isNull();
	}

	@Test
	public void testUpdateReferencesListBoxModelNoGeneratedCodes() throws Exception
	{
		final MultiCodeCoupon multiCodeCoupon = new MultiCodeCoupon();
		Mockito.lenient().when(context.getParameter(PARENT_OBJECT)).thenReturn(multiCodeCoupon);
		listEditorRenderer.updateReferencesListBoxModel(CODE_SEARCH_TEXT);
		assertThat(listEditorRenderer.getPageable()).isNull();
	}

	@Test
	public void testUpdateReferencesListBoxModelOK() throws Exception
	{
		final MultiCodeCouponModel multiCodeCoupon = mock(MultiCodeCouponModel.class);
		Mockito.lenient().when(multiCodeCoupon.getGeneratedCodes()).thenReturn(getMockedGeneratedCodes());
		Mockito.lenient().when(context.getParameter(PARENT_OBJECT)).thenReturn(multiCodeCoupon);
		listEditorRenderer.updateReferencesListBoxModel(CODE_SEARCH_TEXT);
		assertThat(listEditorRenderer.getPageable()).isNotNull();
		assertThat(listEditorRenderer.getPageable().getTotalCount()).isEqualTo(1);
	}

	private Collection<MediaModel> getMockedGeneratedCodes()
	{
		final MediaModel media1 = new MediaModel();
		media1.setCode(CODE_SEARCH_TEXT);
		final MediaModel media2 = new MediaModel();
		media2.setCode("test_code");

		return ImmutableSet.of(media1, media2);
	}
}
