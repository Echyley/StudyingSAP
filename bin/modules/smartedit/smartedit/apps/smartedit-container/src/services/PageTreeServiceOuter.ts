/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GatewayProxied, IPageTreeService } from 'smarteditcommons';

@GatewayProxied('registerTreeComponent', 'getTreeComponent')
export class PageTreeService extends IPageTreeService {
    constructor() {
        super();
    }

    registerTreeComponent(item: any): Promise<void> {
        this.item = item;
        return Promise.resolve();
    }

    getTreeComponent(): Promise<any> {
        return Promise.resolve(this.item);
    }
}
