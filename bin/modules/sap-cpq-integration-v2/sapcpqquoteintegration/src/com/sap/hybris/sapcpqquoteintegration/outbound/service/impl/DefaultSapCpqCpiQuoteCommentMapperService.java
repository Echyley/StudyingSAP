/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteCommentMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;

/**
 * CPQ Api Quote Comment Mapper Service Implementation
 *
 */
public class DefaultSapCpqCpiQuoteCommentMapperService
		implements SapCpqCpiQuoteCommentMapperService<CommentModel, SAPCPQOutboundQuoteCommentModel> {

	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;

	@Override
	public void map(CommentModel comment, SAPCPQOutboundQuoteCommentModel scpiQuoteComment) {
		mapComments(comment, scpiQuoteComment);

	}

	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}

	protected void mapComments(CommentModel comment, SAPCPQOutboundQuoteCommentModel quoteOutboundComment) {

		if (comment.getAuthor() instanceof B2BCustomerModel) {
			final B2BCustomerModel customer = (B2BCustomerModel) comment.getAuthor();
			final CompanyModel rootB2BUnit = getRootB2BUnit(customer);
			quoteOutboundComment.setUserCompany(rootB2BUnit.getDisplayName());
			quoteOutboundComment.setUserName(comment.getAuthor().getDisplayName());
		}
		quoteOutboundComment.setEmail(comment.getAuthor().getUid());
		quoteOutboundComment.setComment(comment.getText());
		quoteOutboundComment.setSource(SapcpqquoteintegrationConstants.COMMENT_SOURCE);

	}

	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, ?> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

}
