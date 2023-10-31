/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

import de.hybris.platform.subscriptionfacades.data.ExternalObjectReferenceData;
import de.hybris.platform.subscriptionfacades.data.PricingData;

import java.util.List;

public class SubscriptionRatePlanForm {

    private PricingData pricing;
    private List<ExternalObjectReferenceData> externalObjectReferences;

    public PricingData getPricing() {
        return pricing;
    }

    public void setPricing(PricingData pricing) {
        this.pricing = pricing;
    }

    public List<ExternalObjectReferenceData> getExternalObjectReferences() {
        return externalObjectReferences;
    }

    public void setExternalObjectReferences(List<ExternalObjectReferenceData> externalObjectReferences) {
        this.externalObjectReferences = externalObjectReferences;
    }
}
