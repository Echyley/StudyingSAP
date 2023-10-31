/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * CPQ Quote Mapper Service
 *
 */
public class DefaultSapCpqCpiQuoteMapperService
		implements SapCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel> {

	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;
	private ConfigurationService configurationService;

	@Override
	public void map(QuoteModel quoteModel, SAPCPQOutboundQuoteModel scpiQuoteModel) {
		mapQuoteToSapCpqCpiOutboundQuote(quoteModel, scpiQuoteModel);

	}

	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}

	protected SAPCPQOutboundQuoteModel mapQuoteToSapCpqCpiOutboundQuote(final QuoteModel quoteModel,
			final SAPCPQOutboundQuoteModel scpiQuoteModel) {

		scpiQuoteModel.setExternalQuoteId(quoteModel.getCode());
		scpiQuoteModel.setName(quoteModel.getName());
		scpiQuoteModel.setDescription(quoteModel.getDescription());
		scpiQuoteModel.setVersion(quoteModel.getVersion().toString());
		scpiQuoteModel.setQuoteId(quoteModel.getCpqExternalQuoteId());
		scpiQuoteModel.setCreationDate(new Date().toString());
		scpiQuoteModel.setDistributionChannel("");
		scpiQuoteModel.setDivison(quoteModel.getCpqDivision());
		scpiQuoteModel.setMarketID("");
		if(null!=quoteModel.getStore().getSAPConfiguration()) {
 			scpiQuoteModel.setMarketCode(quoteModel.getStore().getSAPConfiguration().getSapcommon_salesOrganization());
 		}
		scpiQuoteModel.setPricebookId("");
		scpiQuoteModel.setOrigin(SapcpqquoteintegrationConstants.QUOTE_ORIGIN);

		if (quoteModel.getDescription() != null) {
			scpiQuoteModel.setGlobalComment(quoteModel.getDescription());
		} else {
			scpiQuoteModel.setGlobalComment("");
		}

		List<SAPCPQOutboundQuoteItemModel> listItem = new ArrayList<>();
		int counter = 1;
		for (AbstractOrderEntryModel abstractEntryModel : quoteModel.getEntries()) {

			if (abstractEntryModel instanceof QuoteEntryModel) {

				QuoteEntryModel quoteEntryModel = (QuoteEntryModel) abstractEntryModel;
				SAPCPQOutboundQuoteItemModel model = new SAPCPQOutboundQuoteItemModel();

				model.setExternalItemID(quoteEntryModel.getPk().toString());
				model.setProductSystemId(quoteEntryModel.getProduct().getCode());
				model.setConfigurationId("");
				model.setPartNumber("");
				model.setQuantity(quoteEntryModel.getQuantity().toString());
				model.setItemNumber(counter);

				model.setSapCPQOutboundQuoteItemComments(mapComments(quoteEntryModel.getComments()));
				listItem.add(model);

			}
			counter++;

		}
		scpiQuoteModel.setSapCPQOutboundQuoteItems(listItem);

		return scpiQuoteModel;

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

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
