/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IAuthenticationService } from 'smarteditcommons';

@GatewayProxied()
export class AuthenticationService extends IAuthenticationService {}
