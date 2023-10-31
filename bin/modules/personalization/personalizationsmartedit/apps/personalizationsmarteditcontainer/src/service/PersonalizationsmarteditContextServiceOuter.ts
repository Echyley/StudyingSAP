/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IPersonalizationsmarteditContextObject,
    PersonalizationsmarteditContextUtils,
    Personalization,
    Customize,
    CombinedView,
    SeData,
    IPersonalizationsmarteditContextServiceProxy
} from 'personalizationcommons';
import { ISharedDataService } from 'smarteditcommons';
import { LoadConfigManagerService } from 'smarteditcontainer';
import { PersonalizationsmarteditContextServiceProxy } from './PersonalizationsmarteditContextServiceOuterProxy';

@Injectable()
export class PersonalizationsmarteditContextService {
    protected personalization: Personalization;
    protected customize: Customize;
    protected combinedView: CombinedView;
    protected seData: SeData;
    protected customizeFiltersState: any;

    constructor(
        protected sharedDataService: ISharedDataService,
        protected loadConfigManagerService: LoadConfigManagerService,
        protected personalizationsmarteditContextServiceProxy: IPersonalizationsmarteditContextServiceProxy,
        protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils
    ) {
        const context: IPersonalizationsmarteditContextObject =
            personalizationsmarteditContextUtils.getContextObject();
        this.setPersonalization(context.personalization);
        this.setCustomize(context.customize);
        this.setCombinedView(context.combinedView);
        this.setSeData(context.seData);
        this.customizeFiltersState = {};
    }

    public getPersonalization(): Personalization {
        return this.personalization;
    }

    public setPersonalization(personalization: Personalization): void {
        this.personalization = personalization;
        this.personalizationsmarteditContextServiceProxy.setPersonalization(personalization);
    }

    public getCustomize(): Customize {
        return this.customize;
    }

    public setCustomize(customize: Customize): void {
        this.customize = customize;
        this.personalizationsmarteditContextServiceProxy.setCustomize(customize);
        window.sessionStorage.setItem('Customize', JSON.stringify(customize));
    }

    public getCombinedView(): CombinedView {
        return this.combinedView;
    }

    public setCombinedView(combinedView: CombinedView): void {
        this.combinedView = combinedView;
        this.personalizationsmarteditContextServiceProxy.setCombinedView(combinedView);
        window.sessionStorage.setItem('CombinedView', JSON.stringify(combinedView));
    }

    public getSeData(): SeData {
        return this.seData;
    }

    public setSeData(seData: SeData): void {
        this.seData = seData;
        this.personalizationsmarteditContextServiceProxy.setSeData(seData);
    }

    public refreshExperienceData(): any {
        return this.sharedDataService.get('experience').then((data: any) => {
            const seData = this.getSeData();
            seData.seExperienceData = data;
            seData.pageId = data.pageId;
            this.setSeData(seData);
            return Promise.resolve('ok');
        });
    }

    public refreshConfigurationData(): any {
        this.loadConfigManagerService.loadAsObject().then((configurations: any) => {
            const seData = this.getSeData();
            seData.seConfigurationData = configurations;
            this.setSeData(seData);
        });
    }

    public applySynchronization(): void {
        this.personalizationsmarteditContextServiceProxy.setPersonalization(this.personalization);
        this.personalizationsmarteditContextServiceProxy.setCustomize(this.customize);
        this.personalizationsmarteditContextServiceProxy.setCombinedView(this.combinedView);
        this.personalizationsmarteditContextServiceProxy.setSeData(this.seData);

        this.refreshExperienceData();
        this.refreshConfigurationData();
    }

    public getContexServiceProxy(): PersonalizationsmarteditContextServiceProxy {
        return this.personalizationsmarteditContextServiceProxy;
    }

    public getCustomizeFiltersState(): any {
        return this.customizeFiltersState;
    }

    public setCustomizeFiltersState(filters: any): any {
        this.customizeFiltersState = filters;
    }
}
