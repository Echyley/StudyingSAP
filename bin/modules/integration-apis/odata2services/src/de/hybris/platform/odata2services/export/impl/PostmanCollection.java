/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Postman collection bean built based on the Postman collection schema https://schema.getpostman.com/json/collection/v2.1.0/collection.json
 */
public class PostmanCollection
{
	private Info info;
	@JsonProperty("item")
	private List<Item> items;
	@JsonProperty("variable")
	private List<Variable> variables;
	private Auth auth;

	public Info getInfo()
	{
		return info;
	}

	public void setInfo(final Info info)
	{
		this.info = info;
	}

	public List<Item> getItems()
	{
		return Collections.unmodifiableList(items);
	}

	public void setItems(final List<Item> items)
	{
		this.items = Collections.unmodifiableList(items);
	}

	public List<Variable> getVariables()
	{
		return Collections.unmodifiableList(variables);
	}

	public void setVariables(final List<Variable> variables)
	{
		this.variables = Collections.unmodifiableList(variables);
	}

	public Auth getAuth()
	{
		return auth;
	}

	public void setAuth(final Auth auth)
	{
		this.auth = auth;
	}

	/**
	 * Detailed description of the info block.
	 */
	static class Info
	{
		private String name;
		private String schema;

		public String getName()
		{
			return name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public String getSchema()
		{
			return schema;
		}

		public void setSchema(final String schema)
		{
			this.schema = schema;
		}
	}

	/**
	 * Items are the basic unit for a Postman collection. You can think of them as corresponding to a single API endpoint.
	 * Each Item has one request and may have multiple API responses associated with it.
	 */
	static class Item
	{
		private String name;
		private Request request;

		public String getName()
		{
			return name;
		}

		public void setName(final String name)
		{
			this.name = name;
		}

		public Request getRequest()
		{
			return request;
		}

		public void setRequest(final Request request)
		{
			this.request = request;
		}
	}

	/**
	 * A request represents an HTTP request. If a string, the string is assumed to be the request URL and the method is assumed to be 'GET'.
	 */
	static class Request
	{
		private Auth auth;
		private Url url;
		private String method;
		private Body body;

		public Auth getAuth()
		{
			return auth;
		}

		public void setAuth(final Auth auth)
		{
			this.auth = auth;
		}

		public Url getUrl()
		{
			return url;
		}

		public void setUrl(final Url url)
		{
			this.url = url;
		}

		public String getMethod()
		{
			return method;
		}

		public void setMethod(final String method)
		{
			this.method = method;
		}

		public Body getBody()
		{
			return body;
		}

		public void setBody(final Body body)
		{
			this.body = body;
		}
	}

	/**
	 * The string representation of the request URL, including the protocol, host, path, hash, query parameter(s) and path variable(s).
	 */
	static class Url
	{
		private String raw;
		@JsonProperty("host")
		private List<String> hosts;
		@JsonProperty("path")
		private List<String> paths;

		public String getRaw()
		{
			return raw;
		}

		public void setRaw(final String raw)
		{
			this.raw = raw;
		}

		public List<String> getHosts()
		{
			return Collections.unmodifiableList(hosts);
		}

		public void setHosts(final List<String> hosts)
		{
			this.hosts = Collections.unmodifiableList(hosts);
		}

		public List<String> getPaths()
		{
			return Collections.unmodifiableList(paths);
		}

		public void setPaths(final List<String> paths)
		{
			this.paths = Collections.unmodifiableList(paths);
		}
	}

	/**
	 * Represents authentication helpers provided by Postman.
	 */
	static class Auth
	{
		private String type;
		@JsonProperty("basic")
		private List<Credential> credentials;

		public String getType()
		{
			return type;
		}

		public void setType(final String type)
		{
			this.type = type;
		}

		public List<Credential> getCredentials()
		{
			return Collections.unmodifiableList(credentials);
		}

		public void setCredentials(final List<Credential> credentials)
		{
			this.credentials = Collections.unmodifiableList(credentials);
		}
	}

	/**
	 * Represents an attribute for any authorization method provided by Postman.
	 * For example, username and password are set as auth attributes for Basic Authentication method.
	 */
	static class Credential
	{
		private String key;
		private String value;
		private String type;

		public String getKey()
		{
			return key;
		}

		public void setKey(final String key)
		{
			this.key = key;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(final String value)
		{
			this.value = value;
		}

		public String getType()
		{
			return type;
		}

		public void setType(final String type)
		{
			this.type = type;
		}
	}

	/**
	 * This field contains the data usually contained in the request body.
	 */
	static class Body
	{
		private String mode;
		private String raw;
		private Options options;

		public String getMode()
		{
			return mode;
		}

		public void setMode(final String mode)
		{
			this.mode = mode;
		}

		public String getRaw()
		{
			return raw;
		}

		public void setRaw(final String raw)
		{
			this.raw = raw;
		}

		public Options getOptions()
		{
			return options;
		}

		public void setOptions(final Options options)
		{
			this.options = options;
		}
	}

	/**
	 * Additional configurations and options set for various body modes.
	 */
	static class Options
	{
		private Raw raw;

		public Raw getRaw()
		{
			return raw;
		}

		public void setRaw(final Raw raw)
		{
			this.raw = raw;
		}
	}

	/**
	 * Represents the supported languages and the default value is Json.
	 */
	static class Raw
	{
		private String language;

		public String getLanguage()
		{
			return language;
		}

		public void setLanguage(final String language)
		{
			this.language = language;
		}
	}

	/**
	 * Collection variables allow you to define a set of variables, that are a part of the collection, as
	 * opposed to environments, which are separate entities. Note: Collection variables must not contain any sensitive information.
	 */
	static class Variable
	{
		private String id;
		private String key;
		private String value;

		public String getId()
		{
			return id;
		}

		public void setId(final String id)
		{
			this.id = id;
		}

		public String getKey()
		{
			return key;
		}

		public void setKey(final String key)
		{
			this.key = key;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(final String value)
		{
			this.value = value;
		}
	}

}
