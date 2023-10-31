/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteEntryMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * CPQ Quote Entry Mapper service implementation
 *
 */
public class DefaultSapCpqCpiQuoteEntryMapperService  implements SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>{

	 private B2BUnitService<B2BUnitModel, ?> b2bUnitService;

	@Override
	public void map(AbstractOrderEntryModel quoteEntry, SAPCPQOutboundQuoteItemModel sapCpiQuoteItem) {
		mapQuoteEntries(quoteEntry,sapCpiQuoteItem);
		
	}
	
	private void mapQuoteEntries(final AbstractOrderEntryModel quoteEntryModel, final SAPCPQOutboundQuoteItemModel sapCpiQuoteItem) {
	
		sapCpiQuoteItem.setExternalConfigurationId("");
		sapCpiQuoteItem.setProductSystemId(quoteEntryModel.getProduct().getCode());
		sapCpiQuoteItem.setConfigurationId("");
		sapCpiQuoteItem.setPartNumber("");
		sapCpiQuoteItem.setQuantity(quoteEntryModel.getQuantity().toString());
		sapCpiQuoteItem.setSapCPQOutboundQuoteItemComments(mapComments(quoteEntryModel.getComments()));
	}
	
	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}
	
	
	private List<SAPCPQOutboundQuoteCommentModel> mapComments(List<CommentModel> comments) {

		List<SAPCPQOutboundQuoteCommentModel> quoteOutboundComments = new ArrayList<>();

		for (CommentModel comment : comments) {

			SAPCPQOutboundQuoteCommentModel quoteOutboundComment = new SAPCPQOutboundQuoteCommentModel();

			if (comment.getAuthor() instanceof B2BCustomerModel) {
				final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
				final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
				quoteOutboundComment.setUserCompany(rootB2BUnit.getDisplayName());
				quoteOutboundComment.setUserName(comment.getAuthor().getDisplayName());
			}
			quoteOutboundComment.setEmail(comment.getAuthor().getUid());
			quoteOutboundComment.setComment(comment.getText());
			quoteOutboundComment.setSource(SapcpqquoteintegrationConstants.COMMENT_SOURCE);
			quoteOutboundComments.add(quoteOutboundComment);

		}

		return quoteOutboundComments;

	}
	
	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, ?> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}
}
