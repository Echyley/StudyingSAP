/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcpqquoteintegration.constants.SapcpqquoteintegrationConstants;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCustomerModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteModel;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteStatusModel;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiOutboundQuoteConversionService;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteCommentMapperService;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteEntryMapperService;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteMapperService;
import com.sap.hybris.sapcpqquoteintegration.outbound.service.SapCpqCpiQuoteStatusMapperService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Default implementation of SapCpqCpiOutboundQuoteConversionService
 */
public class DefaultSapCpqCpiOutboundQuoteConversionService implements SapCpqCpiOutboundQuoteConversionService {


	protected static final Logger LOG = Logger.getLogger(DefaultSapCpqCpiOutboundQuoteConversionService.class);

	private List<SapCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> sapCpqCpiQuoteMappers;
	private List<SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> sapCpqCpiQuoteEntryMappers;
	private List<SapCpqCpiQuoteCommentMapperService<CommentModel, SAPCPQOutboundQuoteCommentModel>> sapCpqCpiQuoteCommentMappers;
	private List<SapCpqCpiQuoteStatusMapperService<QuoteModel, SAPCPQOutboundQuoteStatusModel>> sapCpqCpiQuoteStatusMappers;

	private QuoteService quoteService;
	private ConfigurationService configurationService;
	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;


	@Override
	public SAPCPQOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel) {

		final SAPCPQOutboundQuoteModel scpiQuoteModel = new SAPCPQOutboundQuoteModel();

		// Mapping Quote
		getSapCpqCpiQuoteMappers().forEach(mapper -> mapper.map(quoteModel, scpiQuoteModel));

		// Mapping header comments
		 List<CommentModel> quoteComments = quoteModel.getComments();
		 
		if (quoteComments != null && !quoteComments.isEmpty()) {
			final List<SAPCPQOutboundQuoteCommentModel> scpiQuoteComments = new ArrayList<>();
			quoteComments.forEach(comment -> {
				final SAPCPQOutboundQuoteCommentModel scpiQuoteComment = new SAPCPQOutboundQuoteCommentModel();
				getSapCpqCpiQuoteCommentMappers().forEach(mapper -> mapper.map(comment, scpiQuoteComment));
				scpiQuoteComments.add(scpiQuoteComment);
			});
			scpiQuoteModel.setSapCPQOutboundQuoteComments(scpiQuoteComments);

		}

		// Mapping quote entries
		final List<SAPCPQOutboundQuoteItemModel> scpiQuoteItems = new ArrayList<>();
	
		for(AbstractOrderEntryModel entry : quoteModel.getEntries()) {
			final SAPCPQOutboundQuoteItemModel scpiQuoteItem = new SAPCPQOutboundQuoteItemModel();
			scpiQuoteItem.setItemNumber(entry.getEntryNumber());
			getSapCpqCpiQuoteEntryMappers().forEach(mapper -> mapper.map(entry, scpiQuoteItem));

			// Mapping entry comments
			final List<CommentModel> entryComments = entry.getComments();
			if (entryComments != null && !entryComments.isEmpty()) {
				final List<SAPCPQOutboundQuoteCommentModel> scpiQuoteItemComments = new ArrayList<>();
				entryComments.forEach(entryComment -> {
					final SAPCPQOutboundQuoteCommentModel scpiQuoteComment = new SAPCPQOutboundQuoteCommentModel();
					getSapCpqCpiQuoteCommentMappers().forEach(mapper -> mapper.map(entryComment, scpiQuoteComment));
					scpiQuoteItemComments.add(scpiQuoteComment);
				});
				scpiQuoteItem.setSapCPQOutboundQuoteItemComments(scpiQuoteItemComments);

			}
			scpiQuoteItems.add(scpiQuoteItem);

		}

		scpiQuoteModel.setSapCPQOutboundQuoteItems(scpiQuoteItems);

		/*
		 * Filter Comments in case of Edit Quote Scenario Send the comments which are
		 * not present in CPQ
		 */
		if (null != quoteModel.getCpqExternalQuoteId() && CollectionUtils.isNotEmpty(quoteModel.getComments())) {
			 quoteComments = new ArrayList<>();
			for (CommentModel comment : quoteModel.getComments()) {

				if (null != comment.getCode()
						&& !comment.getCode().startsWith(SapcpqquoteintegrationConstants.COMMENT_PREFIX_CPQ)) {
					quoteComments.add(comment);
				}
			}
		}

		scpiQuoteModel.setSapCPQOutboundQuoteComments(mapComments(quoteComments));

		mapCustomer(quoteModel, scpiQuoteModel);

		return scpiQuoteModel;
	}

	protected List<SAPCPQOutboundQuoteCommentModel> mapComments(List<CommentModel> comments) {

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

	protected CompanyModel getRootB2BUnit(final B2BCustomerModel customerModel) {
		final B2BUnitModel parent = (B2BUnitModel) b2bUnitService.getParent(customerModel);
		return b2bUnitService.getRootUnit(parent);
	}

	private void mapCustomer(QuoteModel quoteModel, SAPCPQOutboundQuoteModel scpiQuoteModel) {

		// Quote Customer Model
		Set<SAPCPQOutboundQuoteCustomerModel> customers = new HashSet<>();
		B2BCustomerModel customerModel = (B2BCustomerModel) quoteModel.getUser();
         
		SAPCPQOutboundQuoteCustomerModel customerContact = new SAPCPQOutboundQuoteCustomerModel();
		SAPCPQOutboundQuoteCustomerModel customerAccount = new SAPCPQOutboundQuoteCustomerModel();
		customerContact.setId(customerModel.getCustomerID());
		customerContact.setCustomerCode(customerModel.getDisplayName());

		/* Bill To Customer */
		customerContact.setRoleType(getConfigurationService().getConfiguration()
				.getString(SapcpqquoteintegrationConstants.CUSTOMER_ROLE_CODE));
		
         /* Business partner */
		customerContact.setBusinessPartnerId(customerModel.getSapBusinessPartnerID());
		customers.add(customerContact);
		
		customerAccount.setBusinessPartnerId(getRootB2BUnit(customerModel).getUid());
		customerAccount.setRoleType(getConfigurationService().getConfiguration()
				.getString(SapcpqquoteintegrationConstants.ACCOUNT_ROLE_CODE));
		customers.add(customerAccount);

		scpiQuoteModel.setSapCPQOutboundQuoteCustomers(customers);
	}

	@Override
	public SAPCPQOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(QuoteModel quote) {

		final SAPCPQOutboundQuoteStatusModel scpiQuoteStatus = new SAPCPQOutboundQuoteStatusModel();

		getSapCpqCpiQuoteStatusMappers().forEach(mapper -> mapper.map(quote, scpiQuoteStatus));

		return scpiQuoteStatus;
	}

	public QuoteService getQuoteService() {
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public List<SapCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> getSapCpqCpiQuoteMappers() {
		return sapCpqCpiQuoteMappers;
	}

	public void setSapCpqCpiQuoteMappers(
			List<SapCpqCpiQuoteMapperService<QuoteModel, SAPCPQOutboundQuoteModel>> sapCpqCpiQuoteMappers) {
		this.sapCpqCpiQuoteMappers = sapCpqCpiQuoteMappers;
	}

	public List<SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> getSapCpqCpiQuoteEntryMappers() {
		return sapCpqCpiQuoteEntryMappers;
	}

	public void setSapCpqCpiQuoteEntryMappers(
			List<SapCpqCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCPQOutboundQuoteItemModel>> sapCpqCpiQuoteEntryMappers) {
		this.sapCpqCpiQuoteEntryMappers = sapCpqCpiQuoteEntryMappers;
	}

	public List<SapCpqCpiQuoteCommentMapperService<CommentModel, SAPCPQOutboundQuoteCommentModel>> getSapCpqCpiQuoteCommentMappers() {
		return sapCpqCpiQuoteCommentMappers;
	}

	public void setSapCpqCpiQuoteCommentMappers(
			List<SapCpqCpiQuoteCommentMapperService<CommentModel, SAPCPQOutboundQuoteCommentModel>> sapCpqCpiQuoteCommentMappers) {
		this.sapCpqCpiQuoteCommentMappers = sapCpqCpiQuoteCommentMappers;
	}

	public List<SapCpqCpiQuoteStatusMapperService<QuoteModel, SAPCPQOutboundQuoteStatusModel>> getSapCpqCpiQuoteStatusMappers() {
		return sapCpqCpiQuoteStatusMappers;
	}

	public void setSapCpqCpiQuoteStatusMappers(
			List<SapCpqCpiQuoteStatusMapperService<QuoteModel, SAPCPQOutboundQuoteStatusModel>> sapCpqCpiQuoteStatusMappers) {
		this.sapCpqCpiQuoteStatusMappers = sapCpqCpiQuoteStatusMappers;
	}

	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService<B2BUnitModel, ?> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

}
