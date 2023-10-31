/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component } from '@angular/core';

/**
 * Component that is displayed when Angular route has not been found
 */
@Component({ selector: 'empty', template: "<div>This page doesn't exist</div>" })
export class InvalidRouteComponent {}
