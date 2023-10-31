/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.util.standalone;

import java.io.IOException;

/**
 * Simple util to generate a property file raw format from impex file
 */
public class ImpexToTemplateUtil extends ImpexToPropertyUtil
{
	private static final String WHOLE_SCRIPT_PROPERTY = "cms-content";

	static class TemplateProcessor extends PropertyProcessor
	{
		@Override
		protected void createHeader(final String single)
		{
			super.createHeader(single);
			final String typeCode = header.getType();
			System.out.printf("#set ( $%s = $query.load('" + WHOLE_SCRIPT_PROPERTY + "', '%s') )%n",
					typeCode.toLowerCase().trim(), typeCode.trim());
			System.out.println(header.getWholeDefinition());
			System.out.printf("#foreach( $row in $%s )%n", typeCode.toLowerCase().trim());
			System.out.print(";$row.key");
			for (final String column : header.getColumns())
			{
				System.out.printf(";$row.values.%s", extractProperty(column));
			}
			System.out.println();
			System.out.println("#end");
			System.out.println();
			super.processLines(single);
		}

		@Override
		protected void printLine(final String key, final String nextToken, final String property)
		{
			// no impl.
		}
	}

	public static void main(final String[] args) throws IOException
	{
		final TemplateProcessor templateProcessor = new TemplateProcessor();
		templateProcessor.processPath(args[0]);
	}
}
