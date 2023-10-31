/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IPageTreeService, GatewayProxied } from 'smarteditcommons';

@GatewayProxied('registerTreeComponent', 'getTreeComponent')
export class PageTreeService extends IPageTreeService {
    constructor() {
        super();
    }
}
