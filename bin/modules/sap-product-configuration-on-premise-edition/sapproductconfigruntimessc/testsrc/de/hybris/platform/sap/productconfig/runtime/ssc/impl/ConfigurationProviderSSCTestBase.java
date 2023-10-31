/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


@ManualTest
public abstract class ConfigurationProviderSSCTestBase
{
	@Mock
	I18NService i18nService;



	@Before
	public void setUp() throws IpcCommandException
	{
		MockitoAnnotations.initMocks(this);
	}



}
