/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpireturnsexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.inbound.DataHubInboundOrderHelper;

public class SapCpiOmmReturnOrderConfirmationPersistenceHook implements PrePersistHook {
    private DataHubInboundOrderHelper sapDataHubInboundReturnOrderHelper;

    @Override
    public Optional<ItemModel> execute(final ItemModel item) {
        if (item instanceof ReturnRequestModel) {
            final ReturnRequestModel returnRequest = (ReturnRequestModel) item;
            getSapDataHubInboundReturnOrderHelper().processOrderConfirmationFromDataHub(returnRequest.getCode());
            return Optional.empty();
        }

        return Optional.of(item);
    }

    /**
     * @return the sapDataHubInboundReturnOrderHelper
     */
    public DataHubInboundOrderHelper getSapDataHubInboundReturnOrderHelper() {
        return sapDataHubInboundReturnOrderHelper;
    }

    /**
     * @param sapDataHubInboundReturnOrderHelper
     *            the sapDataHubInboundReturnOrderHelper to set
     */
    @Required
    public void setSapDataHubInboundReturnOrderHelper(
            final DataHubInboundOrderHelper sapDataHubInboundReturnOrderHelper) {
        this.sapDataHubInboundReturnOrderHelper = sapDataHubInboundReturnOrderHelper;
    }

}
