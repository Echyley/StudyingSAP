/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IUrlService } from 'smarteditcommons';

/** @internal */
@GatewayProxied('openUrlInPopup', 'path')
export class UrlService extends IUrlService {}
