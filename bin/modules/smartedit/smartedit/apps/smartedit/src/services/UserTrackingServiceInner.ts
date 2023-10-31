/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, IUserTrackingService } from 'smarteditcommons';

/** @internal */
@GatewayProxied('initConfiguration', 'trackingUserAction')
@Injectable()
export class UserTrackingService extends IUserTrackingService {}
