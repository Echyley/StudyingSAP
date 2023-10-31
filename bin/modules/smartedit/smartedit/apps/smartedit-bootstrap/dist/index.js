'use strict';

var platformBrowserDynamic = require('@angular/platform-browser-dynamic');
var smarteditcommons = require('smarteditcommons');

class BootstrapService {
    constructor() {
        this.logService = new smarteditcommons.LogService();
    }
    bootstrap() {
        const smarteditNamespace = window.smartedit;
        // for legacy requires.push
        const modules = (smarteditNamespace.applications || [])
            .map((application) => ({
            application,
            module: smarteditcommons.moduleUtils.getNgModule(application)
        }))
            .filter((data) => {
            if (!data.module) {
                this.logService.info(`WARNING: Unable to find @NgModule for application ${data.application}`);
                this.logService.info('Here is the list of registered @NgModule:');
                this.logService.info(window.__smartedit__.modules);
            }
            return !!data.module;
        })
            .map((data) => data.module);
        const constants = {
            [smarteditcommons.DOMAIN_TOKEN]: smarteditNamespace.domain,
            smarteditroot: smarteditNamespace.smarteditroot
        };
        /*
         * Bootstrap needs a reference ot the module hence cannot be achieved
         * in smarteditbootstrap.js that would then pull dependencies since it is an entry point.
         * We would then duplicate code AND kill overriding capabilities of "plugins"
         */
        document.body.appendChild(document.createElement(smarteditcommons.SMARTEDIT_COMPONENT_NAME));
        /* forbiddenNameSpaces window._:false */
        platformBrowserDynamic.platformBrowserDynamic()
            .bootstrapModule(window.__smartedit__.SmarteditFactory({ modules, constants }))
            .then(() => {
            delete window.__smartedit__.SmarteditFactory;
        })
            .catch((err) => this.logService.info(err));
    }
}
/** @internal */
const bootstrapService = new BootstrapService();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces angular.module:false */
$(function () {
    bootstrapService.bootstrap();
});
