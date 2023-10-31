/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;
import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteCommentModel;
import de.hybris.platform.comments.model.CommentModel;


/**
 * Provides mapping from {@link CommentModel} to {@link SAPCPQOutboundQuoteCommentModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */

public interface SapCpqCpiQuoteCommentMapperService<SOURCE extends CommentModel, TARGET extends SAPCPQOutboundQuoteCommentModel> {


	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Comment Model
	 * @param target
	 *           SAP CPQ Outbound Quote Comment Model
	 */
	void map(SOURCE source, TARGET target);

	
}
