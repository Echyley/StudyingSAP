/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, IAlertService } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
@Injectable()
export class AlertService extends IAlertService {}
