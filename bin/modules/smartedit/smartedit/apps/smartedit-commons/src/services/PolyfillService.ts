/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { BrowserService } from '@smart/utils';

/* @internal */
@Injectable()
export class PolyfillService {
    constructor(private readonly browserService: BrowserService) {}

    public isEligibleForEconomyMode(): boolean {
        return this.browserService.isIE();
    }

    public isEligibleForExtendedView(): boolean {
        return this.browserService.isIE() || this.browserService.isFF();
    }

    public isEligibleForThrottledScrolling(): boolean {
        return this.browserService.isIE();
    }
}
