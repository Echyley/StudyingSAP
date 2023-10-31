/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.occ.v2.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.platform.b2b.occ.exceptions.CartValidationException;
import de.hybris.platform.b2b.occ.exceptions.QuoteAssemblingException;
import de.hybris.platform.b2b.occ.exceptions.QuoteException;
import de.hybris.platform.b2b.occ.security.SecuredAccessConstants;
import de.hybris.platform.b2b.occ.v2.helper.QuoteHelper;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;
import de.hybris.platform.commercefacades.util.CommerceCartMetadataUtils;
import de.hybris.platform.commercefacades.util.builder.CommerceCartMetadataBuilder;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceQuoteExpirationTimeException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.comments.CreateCommentWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteActionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteDiscountWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteMetadataWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteStarterWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.quote.QuoteWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotes")
@Tag(name = "Quotes")
public class QuoteController extends BaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(QuoteController.class);

	@Resource
	private Validator quoteNameValidator;
	@Resource
	private Validator quoteDescriptionValidator;
	@Resource
	private Validator discountTypeValidator;
	@Resource
	private Validator quoteCommentValidator;
	@Resource
	private QuoteHelper quoteHelper;

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@Operation(summary = "Request a quote.", description = "In the edit state the quote can be requested via cartId, the current content of a cart will be then linked with the quote. The requote action can be triggered by providing the quoteCode parameter, instead of cartId inside the body. The response will contain the new quote's data.", operationId = "createQuote")
	@ApiBaseSiteIdAndUserIdParam
	public QuoteWsDTO createQuote(
			@Parameter(description = "Object representing ways of creating new quote - by cartId for creating a new quote from the cart, by quoteCode for the requote action ", required = true)
			@RequestBody @Nonnull @Valid final QuoteStarterWsDTO quoteStarter,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws VoucherOperationException, CommerceCartModificationException
	{
		final boolean quoteCodePresent = StringUtils.hasText(quoteStarter.getQuoteCode());
		final boolean cartIdPresent = StringUtils.hasText(quoteStarter.getCartId());

		if ( cartIdPresent && !quoteCodePresent )
		{
			return getQuoteHelper().initiateQuote(quoteStarter.getCartId(), fields);
		}
		if ( !cartIdPresent && quoteCodePresent )
		{
			return getQuoteHelper().requote(quoteStarter.getQuoteCode(), fields);
		}
		throw new IllegalArgumentException("Either cartId or quoteCode must be provided");
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP  })
	@Operation(summary = "Get all quotes for user.", description = "Returns history data for all quotes requested by a specified user for a specified base store. The response can display the results across multiple pages, if required.", operationId = "getQuotes")
	@ApiBaseSiteIdAndUserIdParam
	public QuoteListWsDTO getQuotes(
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam(defaultValue = FieldSetLevelHelper.BASIC_LEVEL) @RequestParam(required = false, defaultValue = FieldSetLevelHelper.BASIC_LEVEL) final String fields)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		pageableData.setSort(sort);
		return getQuoteHelper().getQuotes(pageableData, fields);
	}

	@GetMapping(value = "/{quoteCode}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Get a specific quote.", description = "Returns quote details based on a specific quote code. The response contains detailed quote information", operationId = "getQuote")
	@ApiBaseSiteIdAndUserIdParam
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	public QuoteWsDTO getQuote(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		return getQuoteHelper().getQuote(quoteCode, fields);
	}

	@PatchMapping(value = "/{quoteCode}", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Edit the quote.", description = "Updates name, description or expiry date of the quote.", operationId = "updateQuote")
	@ApiBaseSiteIdAndUserIdParam
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	public void updateQuote(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "Updated name, description or expiry date of the quote", required = true) @RequestBody @Nonnull @Valid final QuoteMetadataWsDTO metadata)
	{
		final QuoteUserType userType = getQuoteHelper().getCurrentQuoteUserType().orElse(null);
		if (QuoteUserType.BUYER.equals(userType))
		{
			updateNameAndDescription(metadata, quoteCode);
		}
		else if (QuoteUserType.SELLER.equals(userType))
		{
			updateExpirationTime(metadata, quoteCode);
		}
		else
		{
			throw new AccessDeniedException("Access is denied");
		}
	}

	@PutMapping(value = "/{quoteCode}", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Edit the quote.", description = "Updates name, description or expiry date of the quote.", operationId = "replaceQuote")
	@ApiBaseSiteIdAndUserIdParam
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	public void replaceQuote(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "Updated name, description or expiry date of the quote", required = true) @RequestBody @Nonnull @Valid final QuoteMetadataWsDTO metadata)
	{
		final QuoteUserType userType = getQuoteHelper().getCurrentQuoteUserType().orElse(null);
		if (QuoteUserType.BUYER.equals(userType))
		{
			replaceNameAndDescription(metadata, quoteCode);
		}
		else if (QuoteUserType.SELLER.equals(userType))
		{
			replaceExpirationTime(metadata, quoteCode);
		}
		else
		{
			throw new AccessDeniedException("Access is denied");
		}
	}

	@PostMapping(value = "/{quoteCode}/comments", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Add a comment to a quote.", description = "Adds a comment to the quote", operationId = "createCommentForQuote")
	@ApiBaseSiteIdAndUserIdParam
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	public void createCommentForQuote(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "Text of the comment", required = true) @RequestBody @Nonnull @Valid final CreateCommentWsDTO comment)
	{
		validate(comment, "text", getQuoteCommentValidator());
		getQuoteHelper().addCommentToQuote(quoteCode, comment.getText());
	}

	@PostMapping(value = "/{quoteCode}/action", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Perform workflow actions with the quote.", description = "Perform cancel, submit, edit, checkout, approve, reject actions with the quote.", operationId = "performQuoteAction")
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@ApiBaseSiteIdAndUserIdParam
	public void performQuoteAction(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "The action with the quote. The quote action field is mandatory.", required = true) @RequestBody @Nonnull @Valid final QuoteActionWsDTO quoteAction)
			throws VoucherOperationException, CommerceCartModificationException
	{
		if (quoteAction.getAction() == null)
		{
			throw new IllegalArgumentException("Provided action cannot be null");
		}

		final String action = quoteAction.getAction().toUpperCase(Locale.ENGLISH);
		switch (action)
		{
			case "SUBMIT":
				getQuoteHelper().submitQuote(quoteCode);
				break;

			case "CANCEL":
				getQuoteHelper().cancelQuote(quoteCode);
				break;

			case "APPROVE":
				getQuoteHelper().approveQuote(quoteCode);
				break;

			case "REJECT":
				getQuoteHelper().rejectQuote(quoteCode);
				break;

			case "CHECKOUT":
				getQuoteHelper().acceptAndPrepareCheckout(quoteCode);
				break;

			case "EDIT":
				getQuoteHelper().enableQuoteEdit(quoteCode);
				break;

			default:
				throw new IllegalArgumentException("Provided action not supported");
		}
	}

	@PostMapping(value = "/{quoteCode}/entries/{entryNumber}/comments", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Add a comment to a line item of a quote.", description = "Add a comment to a line item of a quote.", operationId = "createQuoteEntryComment")
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@ApiBaseSiteIdAndUserIdParam
	public void createQuoteEntryComment(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "The entry number. Each entry in a quote has an entry number. Quote entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable @Nonnull @Valid final long entryNumber,
			@Parameter(description = "Text of the comment", required = true) @RequestBody @Nonnull @Valid final CreateCommentWsDTO comment)
	{
		validate(comment, "text", quoteCommentValidator);
		getQuoteHelper().addCommentToQuoteEntry(quoteCode, entryNumber, comment.getText());
	}

	@PostMapping(value = "/{quoteCode}/discounts", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@Operation(summary = "Apply a discount to an existing quote.", description = "In the edit state, a seller can apply a discount to a quote. Type of the discount - PERCENT for discount by percentage, ABSOLUTE for discount by amount, TARGET for discount by adjustment of the total value", operationId = "createQuoteDiscount")
	@ApiBaseSiteIdAndUserIdParam
	public void createQuoteDiscount(
			@Parameter(description = "Identifying code of the quote", required = true) @PathVariable @Nonnull @Valid final String quoteCode,
			@Parameter(description = "Discount applied to the quote - discountType for type of the discount, discountRate for value of the discount ", required = true) @RequestBody @Nonnull @Valid final QuoteDiscountWsDTO quoteDiscount)
	{
		validate(new String[] { quoteDiscount.getDiscountType() }, "discountType", getDiscountTypeValidator());
		getQuoteHelper().applyDiscount(quoteCode, quoteDiscount.getDiscountType(), quoteDiscount.getDiscountRate());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	@ExceptionHandler({ VoucherOperationException.class, QuoteAssemblingException.class, CommerceCartModificationException.class })
	public ErrorListWsDTO handleInternalServerError(final Throwable ex)
	{
		if( LOG.isErrorEnabled() )
		{
			LOG.error(sanitize(ex.getMessage()), ex);
		}
		return handleErrorInternal(ex.getClass().getSimpleName(), "The application has encountered an error");
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ QuoteException.class, CommerceQuoteExpirationTimeException.class })
	public ErrorListWsDTO handleQuoteException(final Throwable ex)
	{
		if( LOG.isErrorEnabled() )
		{
			LOG.error(sanitize(ex.getMessage()), ex);
		}
		return handleErrorInternal(ex.getClass().getSimpleName(), ex.getMessage());
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	@ExceptionHandler({ NotFoundException.class })
	public ErrorListWsDTO handleNotFoundException(final Throwable ex)
	{
		if( LOG.isErrorEnabled() )
		{
			LOG.error(sanitize(ex.getMessage()), ex);
		}
		return handleErrorInternal(ex.getClass().getSimpleName(), ex.getMessage());
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ CartValidationException.class })
	public ErrorListWsDTO handleCartValidationException(final CartValidationException exception)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final List<ErrorWsDTO> errorsList = exception.getModifications()
				.stream()
				.map(this::mapError)
				.collect(Collectors.toList());
		errorListDto.setErrors(errorsList);
		return errorListDto;
	}

	protected ErrorWsDTO mapError(final CartModificationData cartModificationData)
	{
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setErrorCode(cartModificationData.getStatusCode());
		error.setMessage(cartModificationData.getStatusMessage());
		error.setType("CartValidationError");
		return error;
	}

	protected void updateNameAndDescription(final QuoteMetadataWsDTO metadata, final String quoteCode)
	{
		final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
		Preconditions.checkArgument(metadata.getExpirationTime() == null, "User not allowed to change expiration date");
		if (metadata.getName() != null) // name is provided and quote needs to be updated
		{
			validate(metadata, "name", getQuoteNameValidator());
			builder.name(Optional.of(metadata.getName()));
		}
		if (metadata.getDescription() != null) // description is provided and quote needs to be updated
		{
			validate(metadata, "description", getQuoteDescriptionValidator());
			builder.description(Optional.of(metadata.getDescription()));
		}
		final CommerceCartMetadata cartMetadata = builder.build();
		getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
	}

	protected void updateExpirationTime(final QuoteMetadataWsDTO metadata, final String quoteCode)
	{
		final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
		Preconditions.checkArgument(metadata.getName() == null && metadata.getDescription() == null, "User not allowed to change name or description");
		if (metadata.getExpirationTime() != null) // expiration time is provided and quote needs to be updated
		{
			builder.expirationTime(Optional.of(metadata.getExpirationTime()));
		}
		final CommerceCartMetadata cartMetadata = builder.build();
		getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
	}

	protected void replaceNameAndDescription(final QuoteMetadataWsDTO metadata, final String quoteCode)
	{
		final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
		Preconditions.checkArgument(metadata.getExpirationTime() == null, "User not allowed to change expiration date");
		if (metadata.getName() != null)
		{
			validate(metadata, "name", getQuoteNameValidator());
			builder.name(Optional.of(metadata.getName()));
			if (metadata.getDescription() != null) // name and description are provided and need to be replaced
			{
				validate(metadata, "description", getQuoteDescriptionValidator());
				builder.description(Optional.of(metadata.getDescription()));
			}
			else
			{
				builder.description(Optional.of("")); // description should be cleared when only providing name
			}
			final CommerceCartMetadata cartMetadata = builder.build();
			getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
		}
		else if (metadata.getDescription() != null)
		{
			throw new IllegalArgumentException("Name is required."); // an error message should be produced when only providing description
		}
		else
		{
			throw new IllegalArgumentException("Please provide the fields you want to edit");
		}
	}

	protected void replaceExpirationTime(final QuoteMetadataWsDTO metadata, final String quoteCode)
	{
		final CommerceCartMetadataBuilder builder = CommerceCartMetadataUtils.metadataBuilder();
		Preconditions.checkArgument(metadata.getName() == null && metadata.getDescription() == null, "User not allowed to change name or description");
		if (metadata.getExpirationTime() == null) // expirationTime is provided and needs to be replaced
		{
			builder.removeExpirationTime(true); // expirationTime needs to be removed
		}
		else
		{
			builder.expirationTime(Optional.of(metadata.getExpirationTime()));
		}
		final CommerceCartMetadata cartMetadata = builder.build();
		getQuoteHelper().updateQuoteMetadata(quoteCode, cartMetadata);
	}

	public QuoteHelper getQuoteHelper()
	{
		return quoteHelper;
	}

	public void setQuoteHelper(final QuoteHelper quoteHelper)
	{
		this.quoteHelper = quoteHelper;
	}

	public Validator getQuoteNameValidator()
	{
		return quoteNameValidator;
	}

	protected void setQuoteNameValidator(final Validator quoteNameValidator)
	{
		this.quoteNameValidator = quoteNameValidator;
	}

	public Validator getQuoteDescriptionValidator()
	{
		return quoteDescriptionValidator;
	}

	protected void setQuoteDescriptionValidator(final Validator quoteDescriptionValidator)
	{
		this.quoteDescriptionValidator = quoteDescriptionValidator;
	}

	public Validator getQuoteCommentValidator()
	{
		return quoteCommentValidator;
	}

	protected void setQuoteCommentValidator(final Validator quoteCommentValidator)
	{
		this.quoteCommentValidator = quoteCommentValidator;
	}

	public Validator getDiscountTypeValidator()
	{
		return discountTypeValidator;
	}

	protected void setDiscountTypeValidator(final Validator discountTypeValidator)
	{
		this.discountTypeValidator = discountTypeValidator;
	}
}
