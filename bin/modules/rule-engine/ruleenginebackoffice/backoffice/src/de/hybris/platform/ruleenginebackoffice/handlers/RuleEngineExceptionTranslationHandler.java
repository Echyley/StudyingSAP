/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.ruleenginebackoffice.handlers;

import com.hybris.cockpitng.service.ExceptionTranslationHandler;

import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import org.springframework.core.Ordered;

import java.util.List;
import java.util.Objects;


public class RuleEngineExceptionTranslationHandler implements ExceptionTranslationHandler, Ordered
{
	private static final String EMPTY_MESSAGE = "";
	private static final int MESSAGE_LENGTH = 2;
	private List<String> supportedInterceptors;

	@Override
	public boolean canHandle(final Throwable throwable)
	{
		return Objects.nonNull(throwable) && isExceptionTypeSupported(throwable) && (
				isModelExceptionTranslationSupported(throwable.getMessage()) || isModelExceptionTranslationSupported(
						throwable.getCause().getMessage()));
	}

	@Override
	public String toString(final Throwable throwable)
	{
		final String[] message = Objects.toString(throwable.getCause().getLocalizedMessage(), EMPTY_MESSAGE).split(":", 2);
		return message.length == MESSAGE_LENGTH ? message[1] : throwable.getCause().getLocalizedMessage();
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}


	protected boolean isExceptionTypeSupported(final Throwable throwable)
	{
		return isModelSaveException(throwable) || isModelRemoveException(throwable);
	}

	protected boolean isModelRemoveException(final Throwable throwable)
	{
		return throwable instanceof ModelRemovalException || throwable.getCause() instanceof ModelRemovalException;
	}

	protected boolean isModelSaveException(final Throwable throwable)
	{
		return throwable instanceof ModelSavingException || throwable.getCause() instanceof ModelSavingException;
	}

	protected boolean isModelExceptionTranslationSupported(final String exceptionMessage)
	{
		return Objects.nonNull(exceptionMessage) && supportedInterceptors.stream().anyMatch(exceptionMessage::contains);
	}

	protected List<String> getSupportedInterceptors()
	{
		return supportedInterceptors;
	}

	public void setSupportedInterceptors(List<String> supportedInterceptors)
	{
		this.supportedInterceptors = supportedInterceptors;
	}
}
