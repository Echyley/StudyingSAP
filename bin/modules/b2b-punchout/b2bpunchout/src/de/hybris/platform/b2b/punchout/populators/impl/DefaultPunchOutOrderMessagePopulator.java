/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.ItemIn;
import org.cxml.Money;
import org.cxml.PunchOutOrderMessage;
import org.cxml.PunchOutOrderMessageHeader;
import org.cxml.SupplierPartAuxiliaryID;
import org.cxml.Total;


/**
 * Populate Converts a Cart into a CMXL PunchOutOrderMessage.
 */
public class DefaultPunchOutOrderMessagePopulator implements Populator<CartModel, PunchOutOrderMessage> {

    private Converter<AbstractOrderEntryModel, ItemIn> defaultPunchOutOrderEntryPopulator;

    @Override
    public void populate(final CartModel cartModel, final PunchOutOrderMessage punchOutOrder) throws ConversionException {
        punchOutOrder.setPunchOutOrderMessageHeader(new PunchOutOrderMessageHeader());
        punchOutOrder.getPunchOutOrderMessageHeader().setTotal(new Total());
        punchOutOrder.getPunchOutOrderMessageHeader().getTotal().setMoney(new Money());
        punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().setCurrency(cartModel.getCurrency().getIsocode());
        punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney().setvalue(String.valueOf(cartModel.getTotalPrice()));

        for (final AbstractOrderEntryModel orderEntry : cartModel.getEntries()) {
            final ItemIn itemIn = getPunchOutOrderEntryConverter().convert(orderEntry);
            itemIn.getItemID().setSupplierPartAuxiliaryID(new SupplierPartAuxiliaryID());
            itemIn.getItemID().getSupplierPartAuxiliaryID().getContent().add(cartModel.getCode());
            punchOutOrder.getItemIn().add(itemIn);
        }
    }

    protected Converter<AbstractOrderEntryModel, ItemIn> getPunchOutOrderEntryConverter() {
        return defaultPunchOutOrderEntryPopulator;
    }

    public void setPunchOutOrderEntryConverter(final Converter<AbstractOrderEntryModel, ItemIn> punchOutOrderEntryPopulator) {
        this.defaultPunchOutOrderEntryPopulator = punchOutOrderEntryPopulator;
    }
}
