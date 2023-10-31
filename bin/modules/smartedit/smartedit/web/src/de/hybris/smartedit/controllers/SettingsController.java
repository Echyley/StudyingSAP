/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.controllers;

import de.hybris.smartedit.facade.SettingsFacade;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;


/**
 * Unauthenticated controller returning a map of non-protected settings necessary for front-end to self-configure
 * <p>
 */
@RestController("settingsController")
@RequestMapping("/settings")
@Tag(name = "settings")
public class SettingsController
{
	@Resource(name = "smarteditSettingsFacade")
	SettingsFacade settingsFacade;

	@GetMapping
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Get a map of application settings", description = "Endpoint to retrieve a map of non-protected settings necessary for front-end to self-configure",
					operationId = "getSettings")
	public Map<String, Object> getSettings()
	{
		return settingsFacade.getSettings();
	}
}
