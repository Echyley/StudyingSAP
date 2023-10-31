/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingc4cintegration.data.RelatedTransaction;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Populator implementation for populating RelatedTransaction of
 * ServiceRequestData from MemoActivity
 *
 * @param <S> source as MemoActivity
 * @param <T> target as ServiceRequestData
 */
public class Y2C4CRelatedTransactionPopulator<S extends MemoActivity, T extends ServiceRequestData>
	implements Populator<S, T> {

    @Override
    public void populate(S source, T target) throws ConversionException {

	RelatedTransaction relatedTransaction = new RelatedTransaction();
	
	relatedTransaction.setRoleCode(Customerticketingc4cintegrationConstants.MEMO_ACTIVITY_ROLE_CODE);
	relatedTransaction.setTypeCode(Customerticketingc4cintegrationConstants.MEMO_ACTIVITY_TYPE_CODE);
	relatedTransaction.setID(source.getID());
	
	if (StringUtils.isNotBlank(source.getTicketObjectID())) {
	    relatedTransaction.setParentObjectID(source.getTicketObjectID());
	}
	
	if (target.getRelatedTransactions() != null) {
	    target.getRelatedTransactions().add(relatedTransaction);
	} else {
	    target.setRelatedTransactions((Collections.singletonList(relatedTransaction)));
	}

	
    }

}
