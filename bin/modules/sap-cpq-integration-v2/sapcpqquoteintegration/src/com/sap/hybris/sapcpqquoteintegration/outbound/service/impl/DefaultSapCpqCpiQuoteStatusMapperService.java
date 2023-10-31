/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteStatusMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * CPQ Quote Status Mapper Service
 *
 */
public class DefaultSapCpqCpiQuoteStatusMapperService implements SapCpqCpiQuoteStatusMapperService<QuoteModel, SAPCPQOutboundQuoteStatusModel>{
     
	 private B2BUnitService<B2BUnitModel, ?> b2bUnitService;



	@Override
	public void map(QuoteModel quote, SAPCPQOutboundQuoteStatusModel scpiQuoteStatus) {
		mapQuoteStatus(quote, scpiQuoteStatus);
		
	}
	

	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}
	private void mapQuoteStatus(QuoteModel quote,SAPCPQOutboundQuoteStatusModel scpiQuoteStatus) {
		scpiQuoteStatus.setQuoteId(quote.getCpqExternalQuoteId());
		if (QuoteState.CANCELLED.equals(quote.getState())) {

			scpiQuoteStatus.setStatus(SapcpqquoteintegrationConstants.REJECT);

			scpiQuoteStatus.setCancellationComment(populateCanellationComments(quote));

		} else if (QuoteState.BUYER_ORDERED.equals(quote.getState())) {

			scpiQuoteStatus.setStatus(SapcpqquoteintegrationConstants.SALESORDER);
			scpiQuoteStatus.setOrderId(quote.getCpqOrderCode());
		}

	}
	
	private List<SAPCPQOutboundQuoteCommentModel> populateCanellationComments(QuoteModel quoteModel) {

		List<SAPCPQOutboundQuoteCommentModel> list = new ArrayList<>();

		for (CommentModel comment : quoteModel.getComments()) {

			if (null == comment.getSource()) {

				SAPCPQOutboundQuoteCommentModel cancelComment = new SAPCPQOutboundQuoteCommentModel();

				if (comment.getAuthor() instanceof B2BCustomerModel) {

					final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
					final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
					cancelComment.setUserCompany(rootB2BUnit.getDisplayName());
					cancelComment.setUserName(comment.getAuthor().getDisplayName());
				}
				cancelComment.setEmail(comment.getAuthor().getUid());
				cancelComment.setQuoteId(quoteModel.getCpqExternalQuoteId());
				cancelComment.setComment(comment.getText());
				cancelComment.setSource(SapcpqquoteintegrationConstants.COMMENT_SOURCE);

				list.add(cancelComment);
			}
		}
		return list;
	}
	
	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService() {
		return b2bUnitService;
	}


	public void setB2bUnitService(B2BUnitService<B2BUnitModel, ?> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}
	
	
}
