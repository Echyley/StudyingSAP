/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Special strategy for json-fields naming
 * OData have special pascal format for all objects, but it ignores some special cases, like 'd' and 'results',
 *  also if we expect 2or move capitals letters from the beginning(like ID)
 */
public class ODataCaseStrategy extends PropertyNamingStrategy.UpperCamelCaseStrategy
{
    @Override
    public String translate(String input)
    {
        if ("d".equals(input) || "results".equals(input))
        {
            return input;
        }
        else if ("ID".equalsIgnoreCase(input))
        {
            return "ID";
        }
        else
        {
            return super.translate(input);
        }
    }
}
