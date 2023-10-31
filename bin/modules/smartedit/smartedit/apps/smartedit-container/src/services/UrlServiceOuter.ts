/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { GatewayProxied, IUrlService, WindowUtils } from 'smarteditcommons';

/** @internal */
@GatewayProxied('openUrlInPopup', 'path')
@Injectable()
export class UrlService extends IUrlService {
    constructor(
        private router: Router,
        private location: Location,
        private windowUtils: WindowUtils
    ) {
        super();
    }

    openUrlInPopup(url: string): void {
        const win: Window = this.windowUtils
            .getWindow()
            .open(url, '_blank', 'toolbar=no, scrollbars=yes, resizable=yes');
        win.focus();
    }

    path(url: string): void {
        /**
         * Angular route has been used to navigate to currently previewed page.
         */
        this.location.go(url);
        this.router.navigateByUrl(url);
    }
}
