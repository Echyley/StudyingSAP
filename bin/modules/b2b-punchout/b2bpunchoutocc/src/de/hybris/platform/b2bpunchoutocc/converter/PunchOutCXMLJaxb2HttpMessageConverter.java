/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.converter;

import de.hybris.platform.b2b.punchout.PunchOutUtils;

import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;

import org.cxml.CXML;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;


/**
 * An XmlHttpMessageConverter that reads and writes messages.
 */
public class PunchOutCXMLJaxb2HttpMessageConverter extends Jaxb2RootElementHttpMessageConverter
{

	@Override
	protected void writeToResult(final Object o, final HttpHeaders headers, final Result result) throws Exception
	{
		if (o instanceof CXML)
		{
			try
			{
				final Class<?> clazz = ClassUtils.getUserClass(o);
				final Marshaller marshaller = createMarshaller(clazz);
				PunchOutUtils.removeStandalone(marshaller);
				PunchOutUtils.setHeader(marshaller);
				marshaller.marshal(o, result);
			}
			catch (final MarshalException ex)
			{
				throw new HttpMessageNotWritableException("Could not marshal [" + o + "]: " + ex.getMessage(), ex);
			}
			catch (final JAXBException ex)
			{
				throw new HttpMessageConversionException("Could not instantiate JAXBContext: " + ex.getMessage(), ex);
			}

		}
		else
		{
			super.writeToResult(o, headers, result);
		}
	}

	@Override
	public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
		return CXML.class.isAssignableFrom(clazz) && super.canRead(clazz, mediaType);
	}

	@Override
	public boolean canWrite(final Class<?> clazz, final @Nullable MediaType mediaType)
	{
		return CXML.class.isAssignableFrom(clazz) && super.canWrite(clazz, mediaType);
	}
}

