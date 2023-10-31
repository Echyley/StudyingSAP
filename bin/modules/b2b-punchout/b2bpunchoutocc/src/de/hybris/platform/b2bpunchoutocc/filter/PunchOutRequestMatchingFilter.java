/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.filter;

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutocc.exception.BadPunchOutRequestException;
import de.hybris.platform.b2bpunchoutocc.exception.PunchOutCartMissingException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter to allow to access only PunchOut related OCC API's
 */
public class PunchOutRequestMatchingFilter extends OncePerRequestFilter
{
	protected static final String PUNCHOUT_OAUTH_ROLE = "ROLE_PUNCHOUTOAUTH2";
	protected static final String PUNCHOUT_HEADER_SID = "punchoutSid";
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	private static final Logger LOG = LoggerFactory.getLogger(PunchOutRequestMatchingFilter.class);

	@Resource(name = "b2bPunchOutAllowedUrlList")
	private List<Map<String, Object>> b2bPunchOutAllowedUrlList;
	@Resource(name = "punchOutSessionService")
	private PunchOutSessionService punchoutSessionService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
			throws ServletException, IOException
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && containsRole(auth, PUNCHOUT_OAUTH_ROLE) && excludesFromAllowList(request))
		{
			throw new NotFoundException("Path not found！");
		}

		chain.doFilter(request, response);
	}

	protected boolean excludesFromAllowList(final HttpServletRequest request)
	{
		return b2bPunchOutAllowedUrlList.stream().noneMatch(
				allowedUrlMap -> matchAllowedList(processPathInfo(request.getPathInfo()), request.getMethod(), allowedUrlMap,
						request.getHeader(PUNCHOUT_HEADER_SID)));
	}

	protected String processPathInfo(final String pathInfo)
	{
		if (pathInfo.charAt(pathInfo.length() - 1) == '/')
		{
			return pathInfo.substring(0, pathInfo.length() - 1);
		}
		else
		{
			return pathInfo;
		}
	}

	protected boolean containsRole(final Authentication auth, final String role)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean matchAllowedList(final String requestUrl, final String requestMethod,
			final Map<String, Object> allowedUrlMap, final String punchOutSid)
	{
		final List<String> requireMethodList = Arrays.asList((String[])allowedUrlMap.get("methods"));
		final String pattern = (String) allowedUrlMap.get("url");
		if (requireMethodList.contains(requestMethod.toUpperCase(Locale.ENGLISH)) && pathMatcher.match(pattern, requestUrl))
		{
			if (("DATA").equals(allowedUrlMap.get("isolationLevel")))
			{
				Map<String, String> pathParameterMap = pathMatcher.extractUriTemplateVariables(pattern, requestUrl);
				if (MapUtils.isNotEmpty(pathParameterMap) && pathParameterMap.containsKey("cartId"))
				{
					return matchAllowedForCartId(punchOutSid, pathParameterMap);
				}
			}
			return true;
		}
		return false;
	}

	private boolean matchAllowedForCartId(final String punchOutSid,
			final Map<String, String> pathParameterMap)
	{
		if (StringUtils.isBlank(punchOutSid))
		{
			throw new BadPunchOutRequestException("no sid found");
		}
		final StoredPunchOutSessionModel storedPunchOutSessionModel = punchoutSessionService
				.loadStoredPunchOutSessionModel(punchOutSid);
		if (storedPunchOutSessionModel == null || storedPunchOutSessionModel.getCart() == null)
		{
			LOG.error("empty cart should not appear.");
			throw new PunchOutCartMissingException("There was an internal error due to empty cart. Please try again.");
		}
		try
		{
			if (punchoutSessionService
					.isPunchOutSessionExpired((PunchOutSession) storedPunchOutSessionModel.getPunchOutSession()))
			{
				throw new BadPunchOutRequestException("sid expired");
			}
		}
		catch (PunchOutSessionNotFoundException exception)
		{
			LOG.error("punchOutSession not found.");
			throw new BadPunchOutRequestException("sid invalid");
		}
		return pathParameterMap.get("cartId").equals(storedPunchOutSessionModel.getCart().getCode());
	}
}
