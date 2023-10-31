package de.hybris.platform.integrationservices.util;

/**
 * This JavaBean is used for testing the {@link ConfigurationRule}
 */
public class ConfigurationRuleTestJavaBean
{
	private int intProperty;
	private Integer integerProperty;
	private Object objectProperty;

	public int getIntProperty()
	{
		return intProperty;
	}

	public void setIntProperty(final int value)
	{
		intProperty = value;
	}

	public Integer getIntegerProperty()
	{
		return integerProperty;
	}

	public void setIntegerProperty(final Integer value)
	{
		integerProperty = value;
	}

	public Object getProperty()
	{
		return objectProperty;
	}

	public void setProperty(final Object value)
	{
		objectProperty = value;
	}

	public void setWriteOnlyProperty(final String value)
	{
		objectProperty = value;
	}

	public Object getReadOnlyProperty()
	{
		return objectProperty;
	}
}
