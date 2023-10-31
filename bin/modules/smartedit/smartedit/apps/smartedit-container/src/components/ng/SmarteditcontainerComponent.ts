/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ElementRef, Injector } from '@angular/core';
import { SafeResourceUrl } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import {
    registerCustomComponents,
    SMARTEDITCONTAINER_COMPONENT_NAME,
    SMARTEDITLOADER_COMPONENT_NAME,
    CrossFrameEventService,
    EVENT_THEME_CHANGED,
    EVENTS
} from 'smarteditcommons';
import { ThemeSwitchService } from 'smarteditcontainer/services/theme/ThemeSwitchService';

const LEGACY_APP_NAME = 'legacyApp';
const LIGHT_THEME = 'sap_fiori_3';
@Component({
    selector: SMARTEDITCONTAINER_COMPONENT_NAME,
    template: `
        <link rel="stylesheet" *ngIf="cssUrl && !defaultTheme" [href]="cssUrl" />
        <link rel="stylesheet" *ngIf="cssCustomUrl && !defaultTheme" [href]="cssCustomUrl" />
        <router-outlet></router-outlet>
        <div ng-attr-id="${LEGACY_APP_NAME}">
            <se-announcement-board></se-announcement-board>
            <se-notification-panel></se-notification-panel>
            <div ng-view></div>
        </div>
    `
})
export class SmarteditcontainerComponent {
    legacyAppName = LEGACY_APP_NAME;
    cssUrl: SafeResourceUrl;
    cssCustomUrl: SafeResourceUrl;
    defaultTheme: boolean;
    constructor(
        private translateService: TranslateService,
        injector: Injector,
        private elementRef: ElementRef,
        private readonly themeSwitchService: ThemeSwitchService,
        private readonly crossFrameEventService: CrossFrameEventService
    ) {
        this.legacyAppName = LEGACY_APP_NAME;
        this.setApplicationTitle();
        registerCustomComponents(injector);
        this.crossFrameEventService.subscribe(EVENTS.TOKEN_STORED, () => {
            themeSwitchService.selectTheme(null);
        });
        this.crossFrameEventService.subscribe(EVENT_THEME_CHANGED, () => {
            this.getThemeCss();
        });
    }

    async ngOnInit(): Promise<void> {
        /*
         * for e2e purposes:
         * in e2e, we sometimes add some test code in the parent frame to be added to the runtime
         * since we only bootstrap within smarteditcontainer-component node,
         * this code will be ignored unless added into the component before legacy AnguylarJS bootstrapping
         */
        Array.prototype.slice
            .call(document.body.childNodes)
            .filter(
                (childNode: ChildNode) =>
                    !this.isAppComponent(childNode) && !this.isSmarteditLoader(childNode)
            )
            .forEach((childNode: ChildNode) => {
                this.legacyAppNode.appendChild(childNode);
            });
        await this.themeSwitchService.selectTheme(null);
        this.getThemeCss();
    }

    private setApplicationTitle(): void {
        this.translateService.get('se.application.name').subscribe((pageTitle: string) => {
            document.title = pageTitle;
        });
    }

    private get legacyAppNode(): any {
        return this.elementRef.nativeElement.querySelector(
            `div[ng-attr-id="${this.legacyAppName}"]`
        );
    }

    private isAppComponent(childNode: ChildNode): boolean {
        return (
            childNode.nodeType === Node.ELEMENT_NODE &&
            (childNode as HTMLElement).tagName === SMARTEDITCONTAINER_COMPONENT_NAME.toUpperCase()
        );
    }

    private isSmarteditLoader(childNode: ChildNode): boolean {
        return (
            childNode.nodeType === Node.ELEMENT_NODE &&
            ((childNode as HTMLElement).id === 'smarteditloader' ||
                (childNode as HTMLElement).tagName === SMARTEDITLOADER_COMPONENT_NAME.toUpperCase())
        );
    }

    private getThemeCss(): void {
        this.cssUrl = this.themeSwitchService.cssUrl;
        this.cssCustomUrl = this.themeSwitchService.cssCustomUrl;
        this.defaultTheme = this.themeSwitchService.selectedTheme === LIGHT_THEME ? true : false;
    }
}
