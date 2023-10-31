/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.component.renderer.impl;

import de.hybris.platform.acceleratorcms.component.cache.CmsCacheService;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.regioncache.key.CacheKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.PageContext;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CachingCMSComponentRenderer extends GenericViewCMSComponentRenderer
{

	protected static final String DEFAULT_ENCODING = "UTF-8";
	private static final Logger LOG = LoggerFactory.getLogger(CachingCMSComponentRenderer.class);

	protected CmsCacheService cmsCacheService;
	protected String charEncoding;

	protected CmsCacheService getCmsCacheService()
	{
		return cmsCacheService;
	}

	@Required
	public void setCmsCacheService(final CmsCacheService cmsCacheService)
	{
		this.cmsCacheService = cmsCacheService;
	}

	@Override
	protected void renderView(final PageContext pageContext, final AbstractCMSComponentModel component, final String includePath)
			throws ServletException, IOException
	{
		final HttpServletRequest httpRequest = (HttpServletRequest) pageContext.getRequest();
		final boolean useCache = getCmsCacheService().useCache(httpRequest, component);

		if (useCache)
		{
			final String content;
			final CacheKey key = getCmsCacheService().getKey(httpRequest, component);

			if (key == null)
			{
				super.renderView(pageContext, component, includePath);
				return;
			}

			if ((content = getCmsCacheService().get(key)) != null)
			{
				pageContext.getOut().write(content);
			}
			else
			{
				final RequestDispatcher rd = httpRequest.getRequestDispatcher(includePath);
				final IncludeResponseWrapper irw = new IncludeResponseWrapper(pageContext);
				rd.include(httpRequest, irw);
				final String includedContent = irw.getString();
				getCmsCacheService().put(key, includedContent);
				irw.getPrintWriterWrapper().parentWrite(includedContent);
			}
		}
		else
		{
			super.renderView(pageContext, component, includePath);
		}
	}

	protected static class PrintWriterWrapper extends PrintWriter
	{
		private final StringWriter innerOut;
		private final Writer parentWriter;

		public PrintWriterWrapper(final StringWriter out, final Writer parentWriter)
		{
			super(out);
			this.innerOut = out;
			this.parentWriter = parentWriter;
		}

		public void parentWrite(final String includedContent)
		{
			try
			{
				this.parentWriter.write(includedContent);
				// StringBuffer cannot be replaced by StringBuilder due to the type returned by called function
				final StringBuffer sb = this.innerOut.getBuffer(); 
				sb.delete(0, sb.length());
			}
			catch (final IOException ex)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Exception occurred during writing the content", ex);
				}
			}
		}
	}

	protected class IncludeResponseWrapper extends HttpServletResponseWrapper
	{
		private PrintWriterWrapper pww;
		private final StringWriter sw = new StringWriter();

		private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		private final ServletOutputStream sos = new ServletOutputStream()
		{
			@Override
			public void write(final int b) throws IOException
			{
				bos.write(b);
			}

			@Override
			public boolean isReady()
			{
				return true;
			}

			@Override
			public void setWriteListener(final WriteListener arg0)
			{
				throw new UnsupportedOperationException("Operation is not supported");
			}
		};
		private boolean isWriterUsed;
		private boolean isStreamUsed;
		private int status = HttpStatus.OK.value();
		private final PageContext pageContext;

		public IncludeResponseWrapper(final PageContext paramPageContext)
		{
			super((HttpServletResponse) paramPageContext.getResponse());
			this.pageContext = paramPageContext;
		}

		@Override
		public PrintWriter getWriter() throws IOException
		{
			if (this.isStreamUsed)
			{
				throw new IllegalStateException("IMPORT_ILLEGAL_STREAM");
			}
			this.isWriterUsed = true;
			this.pww = new PrintWriterWrapper(this.sw, this.pageContext.getOut());
			return this.pww;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			if (this.isWriterUsed)
			{
				throw new IllegalStateException("IMPORT_ILLEGAL_WRITER");
			}
			this.isStreamUsed = true;
			return this.sos;
		}

		@Override
		public void setContentType(final String x)
		{
			// Empty
		}

		@Override
		public void setLocale(final Locale x)
		{
			// Empty
		}

		@Override
		public void setStatus(final int status)
		{
			this.status = status;
		}

		@Override
		public int getStatus()
		{
			return this.status;
		}

		public String getString() throws UnsupportedEncodingException
		{
			if (this.isWriterUsed)
			{
				return this.sw.toString();
			}
			if (this.isStreamUsed)
			{
				if (CachingCMSComponentRenderer.this.charEncoding != null
						&& !"".equals(CachingCMSComponentRenderer.this.charEncoding))
				{
					return bos.toString(CachingCMSComponentRenderer.this.charEncoding);
				}
				return bos.toString(DEFAULT_ENCODING);
			}
			return "";
		}

		public PrintWriterWrapper getPrintWriterWrapper()
		{
			return this.pww;
		}
	}
}
