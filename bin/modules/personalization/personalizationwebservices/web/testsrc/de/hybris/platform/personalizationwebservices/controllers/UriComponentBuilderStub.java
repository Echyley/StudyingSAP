/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationwebservices.controllers;

import org.springframework.web.util.UriComponentsBuilder;


public class UriComponentBuilderStub extends UriComponentsBuilder
{

	UriComponentBuilderStub()
	{
		scheme("http");
		port(9090);
		host("test.local");
	}
}
