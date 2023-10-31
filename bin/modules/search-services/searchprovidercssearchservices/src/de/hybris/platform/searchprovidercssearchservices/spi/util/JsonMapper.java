/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchprovidercssearchservices.spi.util;

import static com.hybris.charon.utils.Action2Exp.sAction;
import static com.hybris.charon.utils.SupplierExp.silent;
import static rx.Observable.empty;
import static rx.Observable.just;

import de.hybris.platform.searchservices.core.SnRuntimeException;
import de.hybris.platform.searchservices.util.JsonModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.hybris.charon.encdec.ByteArrayEncodeResult;
import com.hybris.charon.encdec.DeductiveEncoder;
import com.hybris.charon.encdec.EncodeRequest;
import com.hybris.charon.encdec.EncodeResult;
import com.hybris.charon.utils.CharonUtils;

import rx.Observable;


public final class JsonMapper
{
	private static final ObjectMapper objectMapper = new ObjectMapper();

	static
	{
		objectMapper.registerModule(new JsonModule());

		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
	}

	private JsonMapper()
	{
		// utility class
	}

	public static class JsonEncoder implements Function<EncodeRequest, Observable<EncodeResult>>
	{
		@Override
		public Observable<EncodeResult> apply(final EncodeRequest request)
		{
			return CharonUtils.singleNoAnnotatedArgument(
					new DeductiveEncoder(obj -> silent(() -> just(new ByteArrayEncodeResult(objectMapper.writeValueAsBytes(obj))))))
					.apply(request);
		}
	}

	public static class JsonDecoder implements BiFunction<Observable<byte[]>, Type, Observable>
	{
		@Override
		public Observable<Object> apply(final Observable<byte[]> incomingBytes, final Type type)
		{
			return incomingBytes.collect(ByteArrayOutputStream::new, (sAction(ByteArrayOutputStream::write))) //
					.map(ByteArrayOutputStream::toByteArray) //
					.flatMap(src -> decodeBytes(src, type));
		}

		private Observable<Object> decodeBytes(final byte[] src, final Type type)
		{
			if (src.length == 0)
			{
				return empty();
			}
			try
			{
				if (type instanceof Class)
				{
					return just(objectMapper.readValue(src, (Class) type));
				}
				if (type instanceof ParameterizedType)
				{
					final ParameterizedType clzType = (ParameterizedType) type;
					if (clzType.getRawType() == Map.class)
					{
						final MapLikeType mlt = objectMapper.getTypeFactory().constructMapLikeType(Map.class,
								(Class) clzType.getActualTypeArguments()[0], (Class) clzType.getActualTypeArguments()[1]);
						return just(objectMapper.readValue(src, mlt));
					}
					final CollectionType ct = objectMapper.getTypeFactory().constructCollectionType((Class) clzType.getRawType(),
							(Class) clzType.getActualTypeArguments()[0]);
					return just(objectMapper.readValue(src, ct));
				}
			}
			catch (final IOException e)
			{
				throw new SnRuntimeException(e);
			}
			return empty();
		}
	}
}
