/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.util.standalone;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Simple util to generate a property file raw format from impex file
 */
public class ImpexToPropertyUtil
{
	protected static class PropertyProcessor
	{
		protected Header header;

		protected class Header
		{
			private String type;
			private final String wholeDefinition;

			private List<String> columns = null;

			public String getType()
			{
				return type;
			}

			public String getWholeDefinition()
			{
				return wholeDefinition;
			}

			public List<String> getColumns()
			{
				return columns;
			}

			Header(final String firstLine)
			{
				//
				final StringTokenizer tokens = new StringTokenizer(firstLine, ";");
				wholeDefinition = firstLine.trim();
				while (tokens.hasMoreTokens())
				{
					if (type == null)
					{
						final String possiblyHeader = tokens.nextToken(); //strip operation

						if (isHeader(possiblyHeader))
						{
							type = extractTypeFromHeader(possiblyHeader);
						}
					}
					else
					{
						if (columns == null)
						{
							columns = new ArrayList<>(10);
							tokens.nextToken();//omit first
						}
						else
						{
							columns.add(tokens.nextToken());
						}
					}
				}
			}
		}

		protected static String extractTypeFromHeader(final String str)
		{
			String localStr = str.trim();
			if (localStr.startsWith("INSERT_UPDATE"))
			{
				localStr = localStr.replace("INSERT_UPDATE", "");
			}
			else if (localStr.startsWith("UPDATE"))
			{
				localStr = localStr.replace("UPDATE", "");
			}
			return localStr;
		}

		public void processPath(final String path) throws IOException
		{
			final File impexFile = new File(path);
			if (impexFile.exists() && impexFile.canRead())
			{
				final List<String> lines = FileUtils.readLines(impexFile, StandardCharsets.UTF_8);
				process(lines);
			}
			else
			{
				throw new IllegalArgumentException(" incorrect file given " + path);
			}
		}

		protected String extractProperty(final String text)
		{
			String localText = text;
			if (StringUtils.isNotBlank(localText))
			{
				if (localText.startsWith("@"))
				{
					localText = localText.substring(1);
				}
				if (localText.startsWith("$"))
				{
					localText = localText.substring(1);
				}
				if (localText.startsWith("&"))
				{
					localText = localText.substring(1);
				}
				if (localText.contains("[") && localText.contains("]"))
				{
					localText = localText.replaceAll("\\[.*\\]", "");
				}
				if (localText.contains("(") && localText.contains(")"))
				{
					localText = localText.replaceAll("\\(.*\\)", "");
				}
			}
			return localText;
		}

		/**
		 * @param lines
		 */
		protected void process(final List<String> lines)
		{
			for (final String single : lines)
			{
				if (single.startsWith("#"))
				{
					processComment(single);
					continue;
				}
				if (StringUtils.isBlank(single))
				{
					continue;
				}
				if (isHeader(single))
				{
					createHeader(single);
				}
				else
				{
					processLines(single);
				}
			}
		}

		protected boolean isHeader(final String str)
		{
			if (StringUtils.isNotBlank(str))
			{
				final String localStr = str.trim().toUpperCase();
				if (localStr.startsWith("INSERT_UPDATE"))
				{
					return true;
				}
				return localStr.startsWith("UPDATE");
			}
			return false;
		}

		protected void createHeader(final String single)
		{
			header = new Header(single);
		}

		/**
		 * @param single
		 */
		protected void processLines(final String single)
		{
			//read line
			final String[] tokens = single.split(";");
			String key = null;
			int startedAt = 0;
			Iterator<String> colsIter = null;
			for (int i = 1; i < tokens.length; i++)
			{
				if (key == null)
				{
					if (StringUtils.isNotBlank(tokens[i]))
					{
						key = extractProperty(tokens[i]);
						startedAt = i;
					}
				}
				else
				{
					if (colsIter == null)
					{
						colsIter = new ArrayList<>(header.columns.subList(startedAt - 1, header.columns.size())).iterator();
					}
					final String nextToken = tokens[i];
					if (colsIter.hasNext())
					{
						final String property = extractProperty(colsIter.next());
						printLine(key, nextToken, property);
					}
				}

			}
		}

		/**
		 * @param single
		 */
		protected void processComment(final String single)
		{
			System.out.println(single);
		}

		protected void printLine(final String key, final String nextToken, final String property)
		{
			System.out.println(header.type.trim() + "." + key + "." + property + "=" + nextToken);
		}
	}

	public static void main(final String[] args) throws IOException
	{
		final PropertyProcessor propertyProcessor = new PropertyProcessor();
		propertyProcessor.processPath(args[0]);
	}
}
