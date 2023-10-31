/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.searchservices.support;

import de.hybris.platform.testframework.UnifiedHybrisTestRunner;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.spockframework.junit4.JUnitDescriptionGenerator;
import org.spockframework.runtime.SpecInfoBuilder;


public class CustomSpockRunner extends JUnitPlatform implements UnifiedHybrisTestRunner
{
	private final CustomTestClassRunnerLogic testClassRunnerLogic;
	private final Class classUnderTest;

	public CustomSpockRunner(final Class<?> clazz) throws InitializationError
	{
		super(clazz);
		classUnderTest = clazz;
		testClassRunnerLogic = new CustomTestClassRunnerLogic(this, this);
	}

	@Override
	public void filter(final Filter filter) throws NoTestsRemainException
	{
		testClassRunnerLogic.filter(filter);
	}

	@Override
	public void run(final RunNotifier notifier)
	{
		testClassRunnerLogic.run(notifier);
	}

	@Override
	public Class getCurrentTestClass()
	{
		return classUnderTest;
	}

	@Override
	public Description getDescription() {
		return JUnitDescriptionGenerator.describeSpec((new SpecInfoBuilder(classUnderTest)).build());
	}

	@Override
	public void superFilter(final Filter filter) throws NoTestsRemainException
	{
		super.filter(filter);
	}

	@Override
	public void superRun(final RunNotifier notifier)
	{
		super.run(notifier);
	}

}
