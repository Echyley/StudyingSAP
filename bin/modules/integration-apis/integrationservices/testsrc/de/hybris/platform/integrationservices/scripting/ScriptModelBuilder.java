/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.scripting;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.scripting.enums.ScriptType;
import de.hybris.platform.scripting.model.ScriptModel;

import java.util.Collection;
import java.util.HashSet;

import org.junit.rules.ExternalResource;

/**
 * A builder for creating scripts in the integration tests.
 */
public final class ScriptModelBuilder extends ExternalResource
{
	private static final ScriptType DEFAULT_SCRIPT_TYPE = ScriptType.GROOVY;

	private final Collection<String> createdScriptCodes;
	private String scriptCode;
	private ScriptType scriptType;
	private String scriptContent;
	private boolean disabled;

	private ScriptModelBuilder()
	{
		createdScriptCodes = new HashSet<>();
	}

	/**
	 * Creates a script builder with GROOVY script type already specified.
	 *
	 * @return a new script builder to use
	 */
	public static ScriptModelBuilder groovyScript()
	{
		return script().withType(ScriptType.GROOVY);
	}

	/**
	 * Creates a script builder without a script type specified.
	 *
	 * @return a new script builder to use
	 */
	public static ScriptModelBuilder script()
	{
		return new ScriptModelBuilder();
	}

	/**
	 * Specifies code (ID) of the script in the model to create.
	 *
	 * @param code unique identifier of the script
	 * @return a builder with the code specified.
	 */
	public ScriptModelBuilder withCode(final String code)
	{
		scriptCode = code;
		return this;
	}

	/**
	 * Specifies a programming language to be used for the script content. If script type is not specified, i.e. this
	 * method is not called, then default {@link ScriptType#GROOVY} will be used.
	 *
	 * @param type a script type (language) to use for the script being created.
	 * @return a builder with the script type specified.
	 */
	public ScriptModelBuilder withType(final ScriptType type)
	{
		scriptType = type;
		return this;
	}

	/**
	 * Specifies content of the script.
	 *
	 * @param content the script content written in the {@link ScriptType} language.
	 * @return a builder with the script content specified
	 */
	public ScriptModelBuilder withContent(final String content)
	{
		scriptContent = content;
		return this;
	}

	/**
	 * Specifies whether this script is disabled  or not.
	 *
	 * @param disabled {@code true} if script will be disabled otherwise {@code false}.
	 * @return a builder with the script disabled value specified
	 */
	public ScriptModelBuilder withDisabled(final boolean disabled)
	{
		this.disabled = disabled;
		return this;
	}

	/**
	 * Builds a script model with the values specified by {@code with...} methods.
	 *
	 * @throws ImpExException if provided values are not correct for the impex script to create the model in the
	 *                        database.
	 */
	public void build() throws ImpExException
	{
		final var type = scriptType != null ? scriptType : DEFAULT_SCRIPT_TYPE;
		final var content = scriptContent != null ? '"' + scriptContent + '"' : "";
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Script; code[unique = true]; scriptType(code); content; disabled",
				"                    ; " + scriptCode + " ; " + type.getCode() + "; " + content + "; " + disabled);
		createdScriptCodes.add(scriptCode);
	}

	/**
	 * Removes all script models from the database.
	 */
	public void cleanup()
	{
		after();
	}

	@Override
	protected void before() throws ImpExException
	{
		build();
	}

	@Override
	protected void after()
	{
		IntegrationTestUtil.remove(ScriptModel.class, m -> createdScriptCodes.contains(m.getCode()));
		createdScriptCodes.clear();
	}
}
