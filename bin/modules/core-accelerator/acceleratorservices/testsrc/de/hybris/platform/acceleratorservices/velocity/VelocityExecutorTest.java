/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.velocity.core.CoreVelocityConfigurer;
import de.hybris.platform.acceleratorservices.velocity.eval.key.InvalidPropertyKeyFormatException;
import de.hybris.platform.acceleratorservices.velocity.resource.PropertyResourceNotFoundException;
import de.hybris.platform.acceleratorservices.velocity.resource.store.ScriptReadWriteHandler;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;
import de.hybris.platform.testframework.TestUtils;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VelocityExecutorTest
{
	@InjectMocks
	private final VelocityExecutor executor = new VelocityExecutor();

	@Mock
	private ScriptUrlContext context;

	@Mock
	private VelocityEngine engine;


	@Mock
	private VelocityContext velocityContext;


	@Mock
	private ScriptReadWriteHandler scriptReadWriteHandler;

	@Mock
	private CoreVelocityConfigurer<VelocityEngine, Void> velocityEngineConfigurer;


	@Before
	public void prepare()
	{
		BDDMockito.given(velocityEngineConfigurer.configure()).willReturn(engine);
	}

	@Test
	public void testSuccess()
	{
		final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

		BDDMockito.given(
				Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
						Mockito.nullable(Reader.class)))).willReturn(Boolean.TRUE);


		executor.execute(context, velocityContext);

		order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
		order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
		order.verify(scriptReadWriteHandler).confirmTarget(context);

	}


	@Test
	public void testFailed()
	{
		final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

		BDDMockito.given(
				Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
						Mockito.nullable(Reader.class)))).willReturn(Boolean.FALSE);


		executor.execute(context, velocityContext);

		order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
		order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
		order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);

	}


	@Test
	public void testThrowsNPE()
	{
		final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

		BDDMockito.given(
				Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
						Mockito.nullable(Reader.class)))).willThrow(new NullPointerException("some blah"));

		try
		{
			executor.execute(context, velocityContext);
			Assert.fail("Should throw a NPE");
		}
		catch (final NullPointerException npe)
		{
			//ok here
		}

		order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
		order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
		order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);

	}

	@Test
	public void testThrowsIKE()
	{
		final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

		BDDMockito.given(
				Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
						Mockito.nullable(Reader.class)))).willThrow(new InvalidPropertyKeyFormatException("some blah"));

		try
		{
			executor.execute(context, velocityContext);
			Assert.fail("Should throw a IKE");
		}
		catch (final InvalidPropertyKeyFormatException npe)
		{
			//ok here
		}

		order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
		order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
		order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);

	}

	@Test
	public void testThrowsParseError()
	{
		final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

		BDDMockito.given(
				Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
						Mockito.nullable(Reader.class)))).willThrow(new ParseErrorException("some blah"));

		try
		{
			executor.execute(context, velocityContext);
			Assert.fail("Should throw a ParseError");
		}
		catch (final ParseErrorException npe)
		{
			//ok here
		}

		order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
		order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
		order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);

	}


	@Test
	public void testThrowsMethodInvocation()
	{
		TestUtils.disableFileAnalyzer("Should throw ParseError");
		try {
			final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

			BDDMockito.given(
					Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
							Mockito.nullable(Reader.class)))).willThrow(
					new MethodInvocationException("some blah", null, "method", "template", 0, 0));

			try
			{
				executor.execute(context, velocityContext);
				Assert.fail("Should throw a ParseError");
			}
			catch (final RuntimeException npe)
			{
				//ok here
			}

			order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
			order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
			order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);
		} finally {
			TestUtils.enableFileAnalyzer();
		}

	}


	@Test
	public void testThrowsMethodInvocationForMissingProperty()
	{
		TestUtils.disableFileAnalyzer("PropertyResourceNotFoundException is expected in this test");
		try
		{

			final InOrder order = Mockito.inOrder(scriptReadWriteHandler);

			BDDMockito.given(
					Boolean.valueOf(engine.evaluate(Mockito.nullable(Context.class), Mockito.nullable(Writer.class), Mockito.nullable(String.class),
							Mockito.nullable(Reader.class)))).willThrow(
					new MethodInvocationException("some blah", new PropertyResourceNotFoundException(null, "some resource"), "method",
							"template", 0, 0));


			executor.execute(context, velocityContext);
			//Assert.fail("Should throw a ParseError");


			order.verify(scriptReadWriteHandler).prepareTemplateReader(context);
			order.verify(scriptReadWriteHandler).prepareTemporaryTargetWriter(context);
			order.verify(scriptReadWriteHandler, Mockito.times(0)).confirmTarget(context);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}

	}

}
