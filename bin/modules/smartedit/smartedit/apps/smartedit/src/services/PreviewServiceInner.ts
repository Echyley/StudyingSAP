/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IPreviewService, UrlUtils } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
export class PreviewService extends IPreviewService {
    constructor(urlUtils: UrlUtils) {
        super(urlUtils);
    }
}
