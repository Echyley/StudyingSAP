/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.azure.dtu.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.HybrisJdbcTemplate;

import javax.sql.DataSource;

public class HybrisJdbcTemplateWithMasterDataSource extends HybrisJdbcTemplate
{
    @Override
    public DataSource getDataSource()
    {
        return Registry.getCurrentTenantNoFallback().getMasterDataSource();
    }
}
