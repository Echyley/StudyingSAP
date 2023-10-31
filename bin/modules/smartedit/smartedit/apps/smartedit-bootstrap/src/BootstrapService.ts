/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { moduleUtils, SMARTEDIT_COMPONENT_NAME, DOMAIN_TOKEN, LogService } from 'smarteditcommons';

class BootstrapService {
    private logService = new LogService();
    bootstrap(): void {
        const smarteditNamespace = (window as any).smartedit;

        // for legacy requires.push
        const modules = (smarteditNamespace.applications || [])
            .map((application: string) => ({
                application,
                module: moduleUtils.getNgModule(application)
            }))
            .filter((data: { application: string; module: NgModule }) => {
                if (!data.module) {
                    this.logService.info(
                        `WARNING: Unable to find @NgModule for application ${data.application}`
                    );
                    this.logService.info('Here is the list of registered @NgModule:');
                    this.logService.info(window.__smartedit__.modules);
                }
                return !!data.module;
            })
            .map((data: { application: string; module: NgModule }) => data.module);

        const constants = {
            [DOMAIN_TOKEN]: smarteditNamespace.domain,
            smarteditroot: smarteditNamespace.smarteditroot
        };

        /*
         * Bootstrap needs a reference ot the module hence cannot be achieved
         * in smarteditbootstrap.js that would then pull dependencies since it is an entry point.
         * We would then duplicate code AND kill overriding capabilities of "plugins"
         */
        document.body.appendChild(document.createElement(SMARTEDIT_COMPONENT_NAME));

        /* forbiddenNameSpaces window._:false */
        platformBrowserDynamic()
            .bootstrapModule((window.__smartedit__ as any).SmarteditFactory({ modules, constants }))
            .then(() => {
                delete (window.__smartedit__ as any).SmarteditFactory;
            })
            .catch((err) => this.logService.info(err));
    }
}
/** @internal */
export const bootstrapService = new BootstrapService();
