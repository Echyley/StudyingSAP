/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.setup.impl;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractBackofficeSearchSystemSetupConfigTest
{
    private static final String CONFIGUR_KEY = "key";
    private static final String CONFIGUR_VALUE = "value";
    private ConfigStringResolver resolver = mock(ConfigStringResolver.class);
    private AbstractBackofficeSearchSystemSetupConfig config = new AbstractBackofficeSearchSystemSetupConfig(resolver);

    @Test
    public void shouldConfigStringResolverInitialized()
    {
        assertThat(config.getConfigStringResolver()).isNotNull();
    }

    @Test
    public void shouldReturnTargetConfiguredValueAccordingToCofiguredKey()
    {
        when(resolver.resolveConfigStringParameter(CONFIGUR_KEY)).thenReturn(CONFIGUR_VALUE);

        assertThat(config.getConfigurationRoots(CONFIGUR_KEY)).isEqualTo(CONFIGUR_VALUE);
    }
}
