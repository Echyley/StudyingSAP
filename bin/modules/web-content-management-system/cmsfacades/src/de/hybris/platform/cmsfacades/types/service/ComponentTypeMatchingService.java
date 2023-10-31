/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import java.util.Set;

/**
 * Service that matches the <code>ComponentTypeData</code> from a given componentTypeData for different criteria.
 */
public interface ComponentTypeMatchingService {

    /**
     * This method is used to map ComponentType name to name with current langIsoCode.
     * Note: If the langIsoCode is empty, the default langIsoCode is en.
     *
     * @param componentTypeData
     * 			The object that contains the information about the type to check.
     * @param langIsoCode
     * 			The langIsoCode applied when searching. Is applied to name.
     * @return ComponentTypeData with langIsoCode name, otherwise ComponentTypeData with empty name.
     */
    ComponentTypeData mapNameForComponentType(final ComponentTypeData componentTypeData, String langIsoCode);

    /**
     * This method is used to determine if a given ComponentTypeData is valid based on the given criteria. It checks that the code of the type is
     * included in the given typeRestrictions and that the mask matches either the name or the code.
     *
     * Note: If the mask is empty, this method will skip the name and code check.
     *
     * @param componentTypeData
     * 			The object that contains the information about the type to check.
     * @param typeRestrictionsForPage
     * 			The set of type restrictions that are valid for the page.
     * @param mask
     * 			The mask applied when searching. Is applied to name and code
     * @return <tt>TRUE</tt> if the type is valid for the page, <tt>FALSE</tt> otherwise.
     */
    boolean isTypeMatchingCriteria(final ComponentTypeData componentTypeData, final Set<String> typeRestrictionsForPage,
                                   final String mask);

    /**
     * Utility function. Checks if the given value is null. If it is, the function returns an empty string. Otherwise, it
     * returns the given value.
     *
     * @param value the value to check
     * @return An empty string if the provided value is null, otherwise the original string.
     */
    String getValueOrDefault(final String value);
}
