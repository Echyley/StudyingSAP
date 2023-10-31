/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationaddon;

import de.hybris.platform.acceleratorcms.services.impl.DefaultCMSDynamicAttributeService;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.personalizationaddon.data.CxViewValueCoder;
import de.hybris.platform.personalizationcms.strategy.CmsCxAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * CxCMS service extending the {@link DefaultCMSDynamicAttributeService}.
 */
public class PersonalizationDynamicAttributeService extends DefaultCMSDynamicAttributeService {
    private static final String PUBLIC_CUSTOMIZATION = "data-personalization-customization-code";
    private static final String PUBLIC_VARIATION = "data-personalization-variation-code";

    private CxViewValueCoder cxViewValueCoder;

    @Override
    public Map<String, String> getDynamicComponentAttributes(final AbstractCMSComponentModel component,
                                                             final ContentSlotModel contentSlot)

    {
        final Map<String, String> result = new ConcurrentHashMap<>();
        if (component instanceof CmsCxAware) {
            final CmsCxAware cxAwareComponent = (CmsCxAware) component;

            putIfNotEmpty(result, PUBLIC_CUSTOMIZATION, cxAwareComponent::getCxCustomizationCode);
            putIfNotEmpty(result, PUBLIC_VARIATION, cxAwareComponent::getCxVariationCode);
        }
        return result;
    }

    private void putIfNotEmpty(final Map<String, String> result, final String key, final Supplier<String> supplier) {
        String value = supplier.get();
        if (value != null) {
            if (cxViewValueCoder != null) {
                value = cxViewValueCoder.encode(value);
            }
            result.put(key, value);
        }
    }

    @Override
    public String getFallbackElement(final CMSItemModel cmsItemModel) {
        if (cmsItemModel instanceof CmsCxAware && ((CmsCxAware) cmsItemModel).getCxActionCode() != null) {
            return "div";
        } else {
            return null;
        }
    }

    public void setCxViewValueCoder(final CxViewValueCoder cxViewValueCoder) {
        this.cxViewValueCoder = cxViewValueCoder;
    }
}
