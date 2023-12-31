/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegration.inbound.helper.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteHelper;
import com.sap.hybris.sapquoteintegration.service.SapQuoteService;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Base64;
import de.hybris.platform.util.DiscountValue;

public class DefaultInboundQuoteHelper implements InboundQuoteHelper {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultInboundQuoteHelper.class);
	private QuoteService quoteService;
	private UserService userService;
	private CommentService commentService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private SapQuoteService sapQuoteService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.hybris.sapquoteintegration.inbound.InboundQuoteHelper#
	 * processInboundQuote(de.hybris.platform.core.model.order.QuoteModel)
	 */
	@Override
	public QuoteModel processInboundQuote(QuoteModel inboundQuote) {
		if (inboundQuote.getCode().equals(inboundQuote.getExternalQuoteId())) {
			// process new quote
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating new quote");
			inboundQuote.setState(QuoteState.BUYER_OFFER);
			inboundQuote.setDate(new Date());
			inboundQuote.setStore(baseStoreService.getBaseStoreForUid(inboundQuote.getStoreUid()));
			inboundQuote.setSite(baseSiteService.getBaseSiteForUID(inboundQuote.getStoreUid()));
			inboundQuote.setGuid(UUID.randomUUID().toString());
			inboundQuote.setVersion(1);

		} else {
			// create a Vendor Quote of Existing Quote
			LOG.info("DefaultInboundQuoteHelper#processInboundQuote Creating Vendor Quote of Existing quote");
			replicateCurrentQuote(inboundQuote);
			inboundQuote.setSubtotal(inboundQuote.getTotalPrice() + inboundQuote.getTotalDiscounts());
		}
		
		if(null != inboundQuote.getStore() && inboundQuote.getStore().isNet()) {
			inboundQuote.setNet(true);
		}
		inboundQuote.setExpirationTime(inboundQuote.getQuoteExpirationDate());
		processComments(inboundQuote);
		processDiscount(inboundQuote);
		saveProposalDocument(inboundQuote);
		LOG.info("Exiting DefaultInboundQuoteHelper#processInboundQuote");
		return inboundQuote;

	}

	/**
	 * @param inboundQuote
	 */
	private void replicateCurrentQuote(QuoteModel inboundQuote) {
		QuoteModel currentQuote = quoteService.getCurrentQuoteForCode(inboundQuote.getCode());
		if (inboundQuote.getExternalQuoteId() == null
				|| inboundQuote.getExternalQuoteId().isEmpty()) {
			inboundQuote.setExternalQuoteId(currentQuote.getExternalQuoteId());
		}
		inboundQuote.setState(QuoteState.BUYER_OFFER);
		inboundQuote.setB2bcomments(currentQuote.getB2bcomments());
		inboundQuote.setCreationtime(currentQuote.getCreationtime());

		inboundQuote.setDate(currentQuote.getDate());
		inboundQuote.setDescription(currentQuote.getDescription());
		inboundQuote.setGuid(currentQuote.getGuid());
		inboundQuote.setLocale(currentQuote.getLocale());
		inboundQuote.setName(currentQuote.getName());
		inboundQuote.setOwner(currentQuote.getOwner());
		inboundQuote.setPreviousEstimatedTotal(currentQuote.getTotalPrice());
		inboundQuote.setSite(currentQuote.getSite());
		inboundQuote.setStore(currentQuote.getStore());
		inboundQuote.setUnit(currentQuote.getUnit());
		inboundQuote.setUser(currentQuote.getUser());
		inboundQuote.setVersion(currentQuote.getVersion().intValue() + 1);
		inboundQuote.setWorkflow(currentQuote.getWorkflow());
	}

	protected void processDiscount(QuoteModel quoteModel) {
		Double headerDiscount = quoteModel.getHeaderDiscount();
		if (headerDiscount != null && headerDiscount.doubleValue() > 0.0d) {
			List<DiscountValue> dvList = new ArrayList<DiscountValue>();
			DiscountValue dv = new DiscountValue(CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
					headerDiscount.doubleValue(), true, headerDiscount.doubleValue(),
					quoteModel.getCurrency().getIsocode());
			dvList.add(dv);
			quoteModel.setGlobalDiscountValues(dvList);
			quoteModel.setQuoteDiscountValuesInternal("<" + dv.toString() + ">");
		}
	}

	/**
	 * @param inboundQuote
	 */
	protected void processComments(QuoteModel inboundQuote) {
		List<CommentModel> comments = inboundQuote.getComments();
		if (comments != null) {
			final String domainCode = "quoteDomain";
			final DomainModel domain = getCommentService().getDomainForCode(domainCode);
			final String componentCode = "quoteComponent";
			final ComponentModel component = getCommentService().getComponentForCode(domain, componentCode);
			final String commentTypeCode = "quoteComment";
			final CommentTypeModel commentType = getCommentService().getCommentTypeForCode(component, commentTypeCode);
			for (CommentModel comment : comments) {
				if (comment.getAuthor() == null) {
					final UserModel author = getUserService().getUserForUID(comment.getCommentAuthorEmail());
					comment.setAuthor(author);
					comment.setComponent(component);
					comment.setCommentType(commentType);
					comment.setCreationtime(new Date());
					comment.setCode(UUID.randomUUID().toString());
				}
			}
			inboundQuote.setComments(comments);
		}
	}

	protected void saveProposalDocument(QuoteModel inboundQuote) {
		String externalProposalDocument = inboundQuote.getExternalQuoteDocument();
		if (externalProposalDocument != null && !externalProposalDocument.isEmpty()) {
			byte[] bytes = Base64.decode(externalProposalDocument);
			inboundQuote.setExternalQuoteDocumentBlob(bytes);
			inboundQuote.setExternalQuoteDocument(null);
		}

	} 

	public QuoteService getQuoteService() {
		return quoteService;
	}

	public void setQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}


	public SapQuoteService getSapQuoteService() {
		return sapQuoteService;
	}

	public void setSapQuoteService(SapQuoteService sapQuoteService) {
		this.sapQuoteService = sapQuoteService;
	}
}
