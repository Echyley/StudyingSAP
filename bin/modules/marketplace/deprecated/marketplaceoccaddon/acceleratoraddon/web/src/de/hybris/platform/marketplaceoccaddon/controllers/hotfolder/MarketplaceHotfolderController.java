/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceoccaddon.controllers.hotfolder;

import de.hybris.platform.marketplaceoccaddon.exceptions.FileDownloadException;
import de.hybris.platform.marketplaceoccaddon.exceptions.FileUploadException;
import de.hybris.platform.marketplaceoccaddon.helper.MarketplaceHotFolderHelper;
import de.hybris.platform.marketplacewebservices.hotfolder.dto.FileUploadWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


/**
 * Controller for marketplace hot folder.
 */
@Controller
@RequestMapping("/{baseSiteId}/hotfolder")
@Tag(name = "Marketplace Hot Folder")
public class MarketplaceHotfolderController
{

	private static final Logger LOG = Logger.getLogger(MarketplaceHotfolderController.class);

	@Resource(name = "marketplaceHotFolderHelper")
	private MarketplaceHotFolderHelper helper;

	@ResponseBody
	@Secured(
	{ "ROLE_VENDORADMINISTRATORGROUP", "ROLE_VENDORPRODUCTMANAGERGROUP", "ROLE_VENDORWAREHOUSESTAFFGROUP",
			"ROLE_VENDORCONTENTMANAGERGROUP" })
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@Operation(summary = "Uploads data", description = "Uploads data such as product data, order data and CMS data into the Marketplace.")
	@ApiBaseSiteIdParam
	public FileUploadWsDTO upload(final HttpServletRequest request)
	{
		return getHelper().processUpload(request);
	}

	@RequestMapping(value = "/download/orders", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_VENDORWAREHOUSESTAFFGROUP", "ROLE_VENDORADMINISTRATORGROUP" })
	@Operation(summary = "Downloads order data", description = "Downloads order data through hot folder.")
	@ApiBaseSiteIdParam
	public void downloadVendorOrders(final HttpServletResponse response)
	{
		getHelper().processOrdersDownload(response);
	}

	@RequestMapping(value = "/download/logs", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_VENDORADMINISTRATORGROUP", "ROLE_VENDORPRODUCTMANAGERGROUP", "ROLE_VENDORWAREHOUSESTAFFGROUP",
			"ROLE_VENDORCONTENTMANAGERGROUP" })
	@Operation(summary = "Downloads the logs of data importing results", description = "Downloads the logs of data importing results through hot folder.")
	@ApiBaseSiteIdParam
	public void downloadVendorLogs(final HttpServletResponse response)
	{
		getHelper().processLogsDownload(response);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(
	{ FileUploadException.class, FileDownloadException.class })
	public ErrorListWsDTO handleExceptions(final Exception e)
	{
		LOG.info("Handling Exception for this request - " + e.getClass().getSimpleName() + " - "
				+ YSanitizer.sanitize(e.getMessage()));
		return handleErrorInternal(e.getClass().getSimpleName(), e.getMessage());
	}

	protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(type.replace("Exception", "Error"));
		error.setMessage(YSanitizer.sanitize(message));
		errorListDto.setErrors(Lists.newArrayList(error));
		return errorListDto;
	}

	protected MarketplaceHotFolderHelper getHelper()
	{
		return helper;
	}

}
