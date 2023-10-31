/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISharedDataService } from "smarteditcommons";
import { LoadConfigManagerService } from "smarteditcontainer";
export declare class MerchandisingSmartEditContainerModule {
    private loadConfigManagerService;
    private sharedDataService;
    constructor(loadConfigManagerService: LoadConfigManagerService, sharedDataService: ISharedDataService);
}
