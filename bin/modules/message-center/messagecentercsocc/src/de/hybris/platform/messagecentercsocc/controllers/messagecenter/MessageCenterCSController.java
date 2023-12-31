/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.messagecentercsocc.controllers.messagecenter;

import de.hybris.platform.messagecentercsfacades.ConversationFacade;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData;
import de.hybris.platform.messagecentercsocc.constants.ErrorConstants;
import de.hybris.platform.messagecentercsocc.dto.conversation.ConversationListWsDTO;
import de.hybris.platform.messagecentercsocc.dto.conversation.ConversationMessageListWsDTO;
import de.hybris.platform.messagecentercsocc.dto.conversation.ConversationWsDTO;
import de.hybris.platform.messagecentercsocc.exceptions.MessageCenterCSException;
import de.hybris.platform.messagecentercsocc.validation.MessageCenterCSValidator;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Controller for message center.
 */
@Controller
@RequestMapping("/{baseSiteId}/messagecenter/im/conversations")
@Tag(name = "Message Center")
public class MessageCenterCSController
{
	private static final String STATUS_OPEN = "open";
	private static final String STATUS_UNASSIGNED = "unassigned";

	@Resource(name = "conversationFacade")
	private ConversationFacade conversationFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "messageCenterCSValidator")
	private MessageCenterCSValidator messageCenterCSValidator;

	@Resource(name = "conversationMessageListValidator")
	private Validator conversationMessageListValidator;
	

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(new String[] {});
	}


	@RequestMapping(value = "/customerconversations", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@Operation(summary = "Gets conversations of current customer", description = "Returns the conversation list of current customer.")
	@ApiBaseSiteIdParam
	public ConversationListWsDTO getConversationsForCustomer()
	{
		final List<ConversationData> conversations = conversationFacade.getConversationsForCustomer();
		return mapConversationList(conversations);
	}



	@RequestMapping(value = "/agentconversations", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@Operation(summary = "Gets unassigned or open conversations for current CSA", description = "Returns unassigned or open conversation list for current CSA.")
	@ApiBaseSiteIdParam
	public ConversationListWsDTO getConversationsForAgent(
			@Parameter(description = "the conversation status", schema = @Schema(allowableValues = {"open", "unassigned"}), required = true) @RequestParam(value = "status", required = true) final String status)
	{
		messageCenterCSValidator.checkIfStatusCorrect(status);
		if (STATUS_UNASSIGNED.equals(status))
		{
			final List<ConversationData> conversations = conversationFacade.getUnassignedConversations();
			return mapConversationList(conversations);
		}
		else if (STATUS_OPEN.equals(status))
		{
			final List<ConversationData> conversations = conversationFacade.getOpenConversations();
				return mapConversationList(conversations);
		}
		return null;

	}

	@RequestMapping(value = "/{conversationId}/pick", method = RequestMethod.PATCH)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@Operation(summary = "Picks an unassigned conversation", description = "Picks an unassigned conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public synchronized ConversationWsDTO pickConversation(
			@Parameter(description = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		messageCenterCSValidator.checkIfConversationClosed(conversation);
		messageCenterCSValidator.checkIfConversationAssigned(conversation);
		return dataMapper.map(conversationFacade.pickConversation(conversationId), ConversationWsDTO.class);
	}


	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@RequestMapping(value = "/{conversationId}/close", method = RequestMethod.PATCH)
	@Operation(summary = "Closes an open conversation", description = "Closes an open conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public ConversationWsDTO closeConversation(
			@Parameter(description = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		messageCenterCSValidator.checkIfConversationAccessible(conversation);
		messageCenterCSValidator.checkIfConversationClosed(conversation);

		return dataMapper.map(conversationFacade.closeConversation(conversationId), ConversationWsDTO.class);
	}


	/**
	 * @deprecated (deprecated since 2105, in order to avoid leaking of exception messages by configuring exceptions)
	 */
	@Deprecated(since = "2105", forRemoval = true)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody

	public ErrorListWsDTO handleExceptions(final MessageCenterCSException e)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(e.getType());
		error.setMessage(e.getMessage());
		error.setErrorCode(e.getErrorCode());
		errorListDto.setErrors(Collections.singletonList(error));
		return errorListDto;
	}

	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP", "ROLE_CUSTOMERGROUP" })
	@Operation(summary = "Sends messages", description = "Sends messages in a conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public ConversationWsDTO sendMessage(
			@Parameter(description = "the list of messages", required = true) @RequestBody final ConversationMessageListWsDTO conversationMessageList)
	{
		validate(conversationMessageList.getMessages(), "messages", conversationMessageListValidator);

		final ConversationMessageListData conversationMessageListData = dataMapper.map(conversationMessageList,
				ConversationMessageListData.class);

		final String conversationId = conversationMessageListData.getConversationId();
		if (StringUtils.isNotBlank(conversationId))
		{
			conversationMessageListData.setConversationId(YSanitizer.sanitize(conversationId));
			final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
			messageCenterCSValidator.checkIfConversationAccessible(conversation);
			messageCenterCSValidator.checkIfConversationClosed(conversation);
		}
		else
		{
			messageCenterCSValidator.checkIfConversationCreatable();
		}

		try
		{
			return dataMapper.map(conversationFacade.sendMessage(conversationMessageListData), ConversationWsDTO.class);
		}
		catch (final ModelSavingException e)
		{
			throw new MessageCenterCSException(ErrorConstants.MESSAGE_SAVE_ERROR, ErrorConstants.MESSAGE_SAVE_MESSAGE);
		}
	}


	protected ConversationListWsDTO mapConversationList(final List<ConversationData> conversations)
	{
		final ConversationDataList conversationList = conversationFacade.getConversationDataList(conversations);

		return dataMapper.map(conversationList, ConversationListWsDTO.class);
	}

	@ResponseBody
	@RequestMapping(value = "/{conversationId}/messages", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@Operation(summary = "Gets conversation messages for current customer or current CSA", description = "Returns all messages of a specific conversation.")
	@ApiBaseSiteIdParam
	public ConversationMessageListWsDTO getMessagesForConversation(
			@Parameter(description = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		if (conversationFacade.isCustomer())
		{
			messageCenterCSValidator.checkIfConversationAccessible(conversation);
		}

		final List<ConversationMessageData> messages = conversationFacade.getMessagesForConversation(conversationId);
		final ConversationMessageListData messageList = conversationFacade.getConversationMessageList(messages);
		return dataMapper.map(messageList, ConversationMessageListWsDTO.class);
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}
}
