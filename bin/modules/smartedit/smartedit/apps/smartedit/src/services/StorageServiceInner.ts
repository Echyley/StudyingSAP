/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IStorageService } from 'smarteditcommons';

/** @internal */
@GatewayProxied()
export class StorageService extends IStorageService {
    constructor() {
        super();
    }
}
