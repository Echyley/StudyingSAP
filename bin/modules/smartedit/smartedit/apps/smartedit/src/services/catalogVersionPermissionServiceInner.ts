/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, ICatalogVersionPermissionService } from 'smarteditcommons';

@GatewayProxied()
export class CatalogVersionPermissionService extends ICatalogVersionPermissionService {
    constructor() {
        super();
    }
}
