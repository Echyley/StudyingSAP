/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.content;

public class ODataJsonProductBuilder implements ChangeSetPartContentBuilder
{
	private String productCode;
	private String productName;
	private String catalogId;
	private String catalogVersion;

	private ODataJsonProductBuilder()
	{
		productCode = "TestProduct";
		catalogId = "Default";
		catalogVersion = "Staged";
	}

	public static ODataJsonProductBuilder product()
	{
		return new ODataJsonProductBuilder();
	}

	public ODataJsonProductBuilder withCode(final String code)
	{
		productCode = code;
		return this;
	}

	public ODataJsonProductBuilder withName(final String name)
	{
		productName = name;
		return this;
	}

	public ODataJsonProductBuilder withCatalog(final String id)
	{
		catalogId = id;
		return this;
	}

	public ODataJsonProductBuilder withCatalogVersion(final String version)
	{
		catalogVersion = version;
		return this;
	}

	@Override
	public String build()
	{
		return "{\n" +
				"   \"@odata.context\": \"$metadata#Products/$entity\",\n" +
					productCode() +
					productName() +
				"	\"catalogVersion\": {\n" +
				"		\"catalog\": {\n" +
							catalogId() +
				"		},\n" +
						catalogVersion() +
				"	}\n" +
				"}\n";
	}

	private String productCode()
	{
		return productCode != null
				? "	\"code\": \"" + productCode + "\",\n"
				: "";
	}

	private String productName()
	{
		return productName != null
				? "	\"name\": \"" + productName + "\",\n"
				: "";
	}

	private String catalogId()
	{
		return catalogId != null
				? "			\"id\": \"" + catalogId + "\"\n"
				: "";
	}

	private String catalogVersion()
	{
		return catalogVersion != null
				? "		\"version\": \"" + catalogVersion + "\"\n"
				: "";
	}
}
