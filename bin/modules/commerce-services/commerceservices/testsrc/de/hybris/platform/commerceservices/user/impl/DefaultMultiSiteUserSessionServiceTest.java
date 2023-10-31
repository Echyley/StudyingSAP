/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.NO_SITES_AVAILABLE_DUMMY;
import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.SITE_SESSION_ATTRIBUTE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMultiSiteUserSessionServiceTest
{

	@Mock
	private UserService userServiceMock;
	@Mock
	private SessionService sessionServiceMock;
	@Mock
	private BaseSiteService baseSiteServiceMock;
	@Mock
	private TypeService typeServiceMock;

	@Mock
	private CustomerModel customerModelMock;
	@Mock
	private BaseSiteModel baseSiteModelMock;
	@Mock
	private Session sessionMock;

	private AttributeDescriptorModel attributeDescriptorModel;

	private DefaultMultiSiteUserSessionService multiSiteUserSessionService;

	@Before
	public void setUp()
	{
		multiSiteUserSessionService = new DefaultMultiSiteUserSessionService();

		multiSiteUserSessionService.setUserService(userServiceMock);
		multiSiteUserSessionService.setSessionService(sessionServiceMock);
		multiSiteUserSessionService.setBaseSiteService(baseSiteServiceMock);
		multiSiteUserSessionService.setTypeService(typeServiceMock);

		attributeDescriptorModel = new AttributeDescriptorModel();
	}

	@Test
	public void testCurrentUserSiteIsNotNull()
	{
		//given
		when(userServiceMock.getCurrentUser()).thenReturn(customerModelMock);
		when(typeServiceMock.getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE)).thenReturn(
				attributeDescriptorModel);
		when(customerModelMock.getSite()).thenReturn(baseSiteModelMock);
		when(sessionServiceMock.getCurrentSession()).thenReturn(sessionMock);
		//when
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		//then
		final ArgumentCaptor<Object> siteValueCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(sessionMock).setAttribute(eq(SITE_SESSION_ATTRIBUTE_NAME), siteValueCaptor.capture());

		final Object siteValueObj = siteValueCaptor.getValue();
		assertTrue(siteValueObj instanceof Collection);
		final Collection<BaseSiteModel> siteValue = (Collection<BaseSiteModel>) siteValueObj;

		assertEquals(1, siteValue.size());
		assertTrue(siteValue.contains(baseSiteModelMock));
	}

	@Test
	public void testCustomerSiteIsNotExistingNowAndCurBaseSiteNull()
	{
		//given
		when(userServiceMock.getCurrentUser()).thenReturn(customerModelMock);
		when(typeServiceMock.getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE)).thenThrow(new UnknownIdentifierException("No attribute with qualifier site found."));
		when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(null);
		when(sessionServiceMock.getCurrentSession()).thenReturn(sessionMock);
		//when
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		//then
		Mockito.verify(sessionMock).setAttribute(SITE_SESSION_ATTRIBUTE_NAME, Collections.singleton(NO_SITES_AVAILABLE_DUMMY));
	}

	@Test
	public void testCustomerSiteIsNotExistingAndExistingAfterUpgradeDown()
	{
		//given
		when(userServiceMock.getCurrentUser()).thenReturn(customerModelMock);
		when(typeServiceMock.getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE)).thenThrow(new UnknownIdentifierException("No attribute with qualifier site found.")).thenReturn(attributeDescriptorModel);
		when(customerModelMock.getSite()).thenReturn(baseSiteModelMock);
		when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
		when(sessionServiceMock.getCurrentSession()).thenReturn(sessionMock);
		//when
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		//then
		final ArgumentCaptor<Object> siteValueCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(sessionMock,times(3)).setAttribute(eq(SITE_SESSION_ATTRIBUTE_NAME), siteValueCaptor.capture());

		List resultSitesValueObjList = siteValueCaptor.getAllValues();
		for (Object siteValueObj : resultSitesValueObjList){
			assertTrue(siteValueObj instanceof Collection);
			Collection<BaseSiteModel> siteValue = (Collection<BaseSiteModel>) siteValueObj;
			assertEquals(1, siteValue.size());
			assertTrue(siteValue.contains(baseSiteModelMock));
		}
	}

	@Test
	public void testCurrentUserSiteIsNullAndCurBaseSiteNull()
	{
		//given
		when(userServiceMock.getCurrentUser()).thenReturn(customerModelMock);
		when(typeServiceMock.getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE)).thenReturn(attributeDescriptorModel);
		when(customerModelMock.getSite()).thenReturn(null);
		when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(null);
		when(sessionServiceMock.getCurrentSession()).thenReturn(sessionMock);
		//when
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		//then
		Mockito.verify(sessionMock).setAttribute(SITE_SESSION_ATTRIBUTE_NAME, Collections.singleton(NO_SITES_AVAILABLE_DUMMY));
	}

	@Test
	public void testCurrentUserSiteIsNullAndCurBaseSiteNotNull()
	{
		//given
		when(userServiceMock.getCurrentUser()).thenReturn(customerModelMock);
		when(typeServiceMock.getAttributeDescriptor(CustomerModel._TYPECODE, CustomerModel.SITE)).thenReturn(attributeDescriptorModel);
		when(customerModelMock.getSite()).thenReturn(null);
		when(baseSiteServiceMock.getCurrentBaseSite()).thenReturn(baseSiteModelMock);
		when(sessionServiceMock.getCurrentSession()).thenReturn(sessionMock);
		//when
		multiSiteUserSessionService.setBaseSitesAttributeInSession();
		//then
		final ArgumentCaptor<Object> siteValueCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(sessionMock).setAttribute(eq(SITE_SESSION_ATTRIBUTE_NAME), siteValueCaptor.capture());

		final Object siteValueObj = siteValueCaptor.getValue();
		assertTrue(siteValueObj instanceof Collection);
		final Collection<BaseSiteModel> siteValue = (Collection<BaseSiteModel>) siteValueObj;
		assertEquals(1, siteValue.size());
		assertTrue(siteValue.contains(baseSiteModelMock));
	}
}
