/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.config.summaryview.jaxb.Attribute;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.summaryview.label.AttributeLabelResolver;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSummaryViewApprovalStatusRendererTest
{
	@Spy
	private DefaultSummaryViewApprovalStatusRenderer renderer;
	@Mock
	private Component parent;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private Attribute attributeConfiguration;
	@Mock
	private ProductModel data;
	@Mock
	private DataType dataType;
	@Mock
	private LabelService labelService;
	@Mock
	private DataAttribute dataAttribute;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private AttributeLabelResolver attributeLabelResolver;
	@Mock
	private Label label;

	@Before
	public void setup()
	{
		when(data.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		renderer.setAttributeLabelResolver(attributeLabelResolver);
	}

	@Test
	public void shouldRenderAttribute()
	{
		when(dataType.getAttribute(any())).thenReturn(dataAttribute);
		when(dataAttribute.getQualifier()).thenReturn("approvalStatus");
		when(labelService.getObjectLabel(anyObject())).thenReturn("label");
		renderer.setLabelService(labelService);
		renderer.setPermissionFacade(permissionFacade);
		when(Boolean.valueOf(permissionFacade.canReadInstanceProperty(data, ProductModel.APPROVALSTATUS))).thenReturn(Boolean.TRUE);
		when(attributeLabelResolver.createAttributeLabel(eq(attributeConfiguration), eq(dataAttribute), any())).thenReturn(label);

		renderer.render(parent, attributeConfiguration, data, dataType, wim);

		verify(renderer).createApprovalStatusValue(eq(data));
	}

	@Test
	public void shouldGetIconStatusSelector()
	{
		final String result = renderer.getIconStatusSClass(mock(Div.class), attributeConfiguration, data, dataAttribute, dataType, wim);
		final String expected = String.format("yw-summaryview-approval-status-%s", ArticleApprovalStatus.APPROVED.name().toLowerCase());
		assertThat(expected).isEqualTo(result);
	}
}
