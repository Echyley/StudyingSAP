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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.renderers.DefaultSummaryViewOnlineStatusRenderer.OnlineStatus;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

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
public class DefaultSummaryViewOnlineStatusRendererTest
{
	@Spy
	private DefaultSummaryViewOnlineStatusRenderer onlineStatusRenderer;
	@Mock
	private AttributeLabelResolver attributeLabelResolver;
	@Mock
	private Component parent;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private Attribute attributeConfiguration;
	@Mock
	private DataType dataType;
	@Mock
	private LabelService labelService;
	@Mock
	private DataAttribute dataAttribute;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private Label label;
	@Mock
	private ProductModel product;
	@Mock
	private TimeService timeService;
	@Mock
	private I18NService i18NService;

	private static final String ATTRIBUTE_QUALIFIER = "attributeQualifier";

	@Before
	public void setUp()
	{
		onlineStatusRenderer.setAttributeLabelResolver(attributeLabelResolver);
		onlineStatusRenderer.setLabelService(labelService);
		onlineStatusRenderer.setPermissionFacade(permissionFacade);
	}

	@Test
	public void shouldRenderAttribute()
	{
		// given
		when(dataType.getAttribute(any())).thenReturn(dataAttribute);
		when(dataAttribute.getQualifier()).thenReturn(ATTRIBUTE_QUALIFIER);
		when(Boolean.valueOf(permissionFacade.canReadInstanceProperty(product, ProductModel.ONLINEDATE))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionFacade.canReadInstanceProperty(product, ProductModel.OFFLINEDATE))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(permissionFacade.canReadInstanceProperty(product, ATTRIBUTE_QUALIFIER))).thenReturn(Boolean.TRUE);
		onlineStatusRenderer.setLabelService(labelService);
		onlineStatusRenderer.setPermissionFacade(permissionFacade);
		when(attributeLabelResolver.createAttributeLabel(eq(attributeConfiguration), eq(dataAttribute), any()))
				.thenReturn(label);

		// when
		onlineStatusRenderer.render(parent, attributeConfiguration, product, dataType, wim);

		// then
		verify(onlineStatusRenderer).createOnlineStatusLabel(any(), any(), eq(product), any(), any(), eq(wim));
	}

	@Test
	public void shouldHandleProductModelAndSubtypes()
	{
		final VariantProductModel variantData = new VariantProductModel();
		final CatalogModel catalogData = new CatalogModel();

		assertThat(onlineStatusRenderer.canHandle(product, dataType)).isTrue();
		assertThat(onlineStatusRenderer.canHandle(variantData, dataType)).isTrue();
		assertThat(onlineStatusRenderer.canHandle(catalogData, dataType)).isFalse();
	}

	@Test
	public void shouldGetIconStatusSelector()
	{
		// given
		final String expected = String.format("yw-summaryview-online-status-%s", OnlineStatus.ONLINE.name().toLowerCase());
		setProductAvailabilityTime(getNow().minusDays(1), getNow().plusDays(1));

		// when
		final String result = onlineStatusRenderer.getIconStatusSClass(mock(Div.class), attributeConfiguration, product,
				dataAttribute, dataType, wim);

		// then
		assertThat(expected).isEqualTo(result);
	}

	@Test
	public void shouldMarkProductStatusAsOnlineWhenItIsAvailableNowForTwoHours()
	{
		// given
		setProductAvailabilityTime(getNow().minusHours(1), getNow().plusHours(1));

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.ONLINE);
	}

	@Test
	public void shouldMarkProductStatusAsOnlineWhenItIsAvailableNowForTwoDays()
	{
		// given
		setProductAvailabilityTime(getNow().minusDays(1), getNow().plusDays(1));

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.ONLINE);
	}

	@Test
	public void shouldMarkProductStatusAsOfflineWhenProductIsNotAvailableYet()
	{
		// given
		setProductAvailabilityTime(getNow().plusDays(1), getNow().plusDays(2));

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.OFFLINE);
	}

	@Test
	public void shouldMarkProductStatusAsOfflineWhenProductIsAlreadyNotAvailable()
	{
		// given
		setProductAvailabilityTime(getNow().minusDays(2), getNow().minusDays(1));

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.OFFLINE);
	}


	@Test
	public void shouldReturnOfflineStatusWhenOnlineAndOfflineDatesAreNull()
	{
		// given
		when(product.getOnlineDate()).thenReturn(null);
		when(product.getOfflineDate()).thenReturn(null);

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.OFFLINE);
	}

	@Test
	public void shouldReturnOnlineStatusWhenOfflineDateIsNull()
	{
		// given
		final LocalDateTime onlineFrom = getNow().minusDays(1);
		final Date onlineFromDate = convertLocalDateTimeToDate(onlineFrom);
		when(product.getOnlineDate()).thenReturn(onlineFromDate);
		when(product.getOfflineDate()).thenReturn(null);

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.ONLINE);
	}

	@Test
	public void shouldReturnOfflineStatusWhenOfflineDateIsNull()
	{
		// given
		final LocalDateTime onlineFrom = getNow().plusDays(1);
		final Date onlineFromDate = convertLocalDateTimeToDate(onlineFrom);
		when(product.getOnlineDate()).thenReturn(onlineFromDate);
		when(product.getOfflineDate()).thenReturn(null);

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.OFFLINE);
	}

	@Test
	public void shouldReturnOnlineStatusWhenOnlineDateIsNull()
	{
		// given
		final LocalDateTime onlineTo = getNow().plusDays(1);
		final Date onlineToDate = convertLocalDateTimeToDate(onlineTo);
		when(product.getOnlineDate()).thenReturn(null);
		when(product.getOfflineDate()).thenReturn(onlineToDate);

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.ONLINE);
	}

	@Test
	public void shouldReturnOfflineStatusWhenOnlineDateIsNull()
	{
		// given
		final LocalDateTime onlineTo = getNow().minusDays(1);
		final Date onlineToDate = convertLocalDateTimeToDate(onlineTo);
		when(product.getOnlineDate()).thenReturn(null);
		when(product.getOfflineDate()).thenReturn(onlineToDate);

		// when
		final OnlineStatus status = onlineStatusRenderer.getOnlineStatus(product);

		// then
		assertThat(status).isEqualTo(OnlineStatus.OFFLINE);
	}

	private Date convertLocalDateTimeToDate(final LocalDateTime localDateTime)
	{
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private LocalDateTime getNow()
	{
		return LocalDateTime.now();
	}

	private void setProductAvailabilityTime(final LocalDateTime from, final LocalDateTime to)
	{
		final Date fromDate = convertLocalDateTimeToDate(from);
		final Date toDate = convertLocalDateTimeToDate(to);
		when(product.getOnlineDate()).thenReturn(fromDate);
		when(product.getOfflineDate()).thenReturn(toDate);
	}
}
