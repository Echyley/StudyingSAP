/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Inject, Injectable } from '@angular/core';
import {
    CombinedView,
    Customize,
    Personalization,
    PersonalizationsmarteditContextUtils,
    SeData
} from 'personalizationcommons';
import { PersonalizationsmarteditContextServiceReverseProxy } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerReverseProxy';
import { IContextualMenuService, YJQUERY_TOKEN } from 'smarteditcommons';

@Injectable()
export class PersonalizationsmarteditContextService {
    protected personalization: Personalization;
    protected customize: Customize;
    protected combinedView: CombinedView;
    protected seData: SeData;

    constructor(
        @Inject(YJQUERY_TOKEN) protected yjQuery: JQueryStatic,
        protected contextualMenuService: IContextualMenuService,
        protected personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy,
        protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils
    ) {
        const context = personalizationsmarteditContextUtils.getContextObject();
        this.setPersonalization(context.personalization);
        try {
            const combinedView = window.sessionStorage.getItem('CombinedView');
            const customize = window.sessionStorage.getItem('Customize');
            this.setCustomize((JSON.parse(customize) as Customize) || context.customize);
            this.setCombinedView(
                (JSON.parse(combinedView) as CombinedView) || context.combinedView
            );
        } catch (e) {
            this.setCustomize(context.customize);
            this.setCombinedView(context.combinedView);
        }
        this.setSeData(context.seData);
    }

    public getPersonalization(): Personalization {
        return this.personalization;
    }

    public setPersonalization(personalization: Personalization): void {
        this.personalization = personalization;
        this.contextualMenuService.refreshMenuItems();
    }

    public getCustomize(): Customize {
        return this.customize;
    }

    public setCustomize(customize: Customize): void {
        this.customize = customize;
        this.contextualMenuService.refreshMenuItems();
    }

    public getCombinedView(): CombinedView {
        return this.combinedView;
    }

    public setCombinedView(combinedView: CombinedView): void {
        this.combinedView = combinedView;
        this.contextualMenuService.refreshMenuItems();
    }

    public getSeData(): SeData {
        return this.seData;
    }

    public setSeData(seData: SeData): void {
        this.seData = seData;
    }

    public isCurrentPageActiveWorkflowRunning(): Promise<boolean> {
        return this.personalizationsmarteditContextServiceReverseProxy.isCurrentPageActiveWorkflowRunning();
    }
}
