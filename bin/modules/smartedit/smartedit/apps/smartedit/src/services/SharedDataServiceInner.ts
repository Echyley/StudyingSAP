/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, ISharedDataService } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
export class SharedDataService extends ISharedDataService {
    constructor() {
        super();
    }
}
