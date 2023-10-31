/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnDestroy, OnInit, VERSION } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    SettingsService,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import './cx-qtx-survey-button.bundle.js';
@Component({
    selector: 'se-qualtrics',
    template: `
        <cx-qtx-survey-button
            fiori3-compatible="true"
            [attr.aria-hidden]="false"
            attr.aria-label="{{ 'se.toolbar.qualtrics.button.arialabel' | translate }}"
            style="--qtxSurveyButton_Size:40px; --sapContent_ContrastFocusColor: transparent;"
            title="{{ 'se.toolbar.qualtrics.title' | translate }}"
            [attr.intercept-url]="interceptUrl"
            [attr.context-params]="contextParamsString"
            (click)="onClick()">
            <button type="button" class="fd-button">
                <span class="sap-icon--feedback"></span>
            </button>
        </cx-qtx-survey-button>
    `
})
export class QualtricsComponent implements OnInit, OnDestroy {
    public interceptUrl = '';
    public contextParamsString = '';
    private contextParams = {};

    constructor(
        private translateService: TranslateService,
        private readonly settingService: SettingsService,
        private readonly userTrackingService: IUserTrackingService
    ) {}

    ngOnDestroy(): void {
        document.querySelector('cx-qtx-survey-button').remove();
    }

    ngOnInit(): void {
        this.contextParams = this._getFixedContextParams();
        this.settingService.load().then((settings) => {
            const tenantId = `${settings['modelt.customer.code']}-${settings['modelt.project.code']}-${settings['modelt.environment.code']}`;
            const tenantRole = settings['modelt.environment.type'] as string;
            const appVersion = settings['build.version.api'] as string;

            this.contextParamsString = JSON.stringify({
                ...this.contextParams,
                tenantId,
                tenantRole,
                appVersion
            });
            this.interceptUrl = settings['smartedit.qualtrics.interceptUrl'] as string;
        });

        // change language when customer switch language
        this.translateService.onLangChange.subscribe((result: any) => {
            const language = result.lang;
            const productName = result.translations['se.application.name'];
            this.contextParamsString = JSON.stringify({
                ...this.contextParams,
                language,
                productName
            });
        });
    }

    onClick(): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.HEADER_TOOL,
            'Give Feedback'
        );
    }

    private _getFixedContextParams(): any {
        return {
            appFrameworkId: 4,
            appFrameworkVersion: VERSION.full,
            appId: 'SmartEdit', // pageId, need to change in future
            appTitle: 'SmartEdit',
            appSupportInfo: 'CEC-COM-ADM-SEDIT',
            pushSrcType: 2
        };
    }
}
