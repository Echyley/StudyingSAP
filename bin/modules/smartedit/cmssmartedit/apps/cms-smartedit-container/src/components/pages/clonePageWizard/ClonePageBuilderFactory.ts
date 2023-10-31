/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { Injectable } from '@angular/core';
import { CMSItemStructure } from 'cmscommons';
import { cloneDeep } from 'lodash';
import {
    stringUtils,
    GenericEditorStructure,
    ICatalogService,
    ICatalogVersion,
    IPageInfoService,
    IUriContext,
    CmsitemsRestService,
    IPageService,
    ICMSPage,
    CMSPageTypes
} from 'smarteditcommons';
import { TypeStructureRestService } from '../../../dao/TypeStructureRestService';
import { IRestrictionsStepHandler } from '../../../interfaces';
import { ContextAwarePageStructureService } from '../../../services';

export class ClonePageBuilder {
    // the page being cloned
    private basePage: ICMSPage;
    // holds current clone page tabs data
    private pageData: ICMSPage;
    private pageInfoStructure: GenericEditorStructure;
    private targetCatalogVersion: ICatalogVersion;

    private componentCloneOption: string;
    private basePageInfoAvailable: boolean;

    constructor(
        private restrictionsStepHandler: IRestrictionsStepHandler,
        private basePageUUID: string,
        private uriContext: IUriContext,
        private contextAwarePageStructureService: ContextAwarePageStructureService,
        private typeStructureRestService: TypeStructureRestService,
        private cmsitemsRestService: CmsitemsRestService,
        private catalogService: ICatalogService,
        private pageInfoService: IPageInfoService
    ) {
        this.basePage = {} as ICMSPage;
        this.pageData = {} as ICMSPage;
        this.pageInfoStructure = {} as GenericEditorStructure;

        this.basePageInfoAvailable = false;
        this.componentCloneOption = '';
    }

    public getPageTypeCode(): CMSPageTypes {
        return this.pageData.typeCode as CMSPageTypes;
    }

    public getPageTemplate(): string {
        return this.pageData.template;
    }

    public getPageLabel(): string {
        return this.pageData.label;
    }

    public getBasePageUuid(): string {
        return this.basePage.uuid;
    }

    public getPageInfo(): ICMSPage {
        return this.pageData;
    }

    public getBasePageInfo(): ICMSPage {
        return this.basePage;
    }

    public getPageProperties(): Partial<ICMSPage> {
        return {
            type: this.pageData.type,
            typeCode: this.pageData.typeCode,
            template: this.pageData.template,
            onlyOneRestrictionMustApply: this.pageData.onlyOneRestrictionMustApply,
            catalogVersion: this.pageData.catalogVersion
        };
    }

    public getPageInfoStructure(): GenericEditorStructure {
        return this.pageInfoStructure;
    }

    public getPageRestrictions(): string[] {
        return this.pageData.restrictions || [];
    }

    public getComponentCloneOption(): string {
        return this.componentCloneOption;
    }

    public async displayConditionSelected(displayConditionResult: ICMSPage): Promise<void> {
        const isPrimaryPage = displayConditionResult.isPrimary;

        this.pageData.defaultPage = isPrimaryPage;
        this.pageData.homepage = displayConditionResult.homepage;
        if (isPrimaryPage) {
            this.pageData.label = this.basePage.label;

            if (this.pageData.restrictions) {
                delete this.pageData.restrictions;
            }
            this.restrictionsStepHandler.hideStep();
        } else {
            this.pageData.label = displayConditionResult.primaryPage
                ? (displayConditionResult.primaryPage as ICMSPage).label
                : '';
            this.restrictionsStepHandler.showStep();
        }
        this.pageData.uid = '';
        await this.updatePageInfoFields();
    }

    public onTargetCatalogVersionSelected(targetCatalogVersion: ICatalogVersion): void {
        this.targetCatalogVersion = targetCatalogVersion;
        this.pageData.catalogVersion = targetCatalogVersion.uuid;
    }

    public componentCloneOptionSelected(cloneOptionResult: string): void {
        this.componentCloneOption = cloneOptionResult;
    }

    public restrictionsSelected(
        onlyOneRestrictionMustApply: boolean,
        restrictions: string[]
    ): void {
        this.pageData.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
        this.pageData.restrictions = restrictions;
    }

    public getTargetCatalogVersion(): ICatalogVersion {
        return this.targetCatalogVersion;
    }

    public isBasePageInfoAvailable(): boolean {
        return this.basePageInfoAvailable;
    }

    public async init(): Promise<void> {
        const pageUUID = await this.getPageUUID(this.basePageUUID);
        const uuid = await this.catalogService.getCatalogVersionUUid(this.uriContext);

        const page = await this.cmsitemsRestService.getById(pageUUID);
        this.basePage = cloneDeep(page) as ICMSPage;
        this.pageData = cloneDeep(this.basePage);
        this.pageData.catalogVersion = uuid;
        this.pageData.pageUuid = this.basePage.uuid;
        delete this.pageData.uuid;

        this.basePageInfoAvailable = true;

        this.pageData.template = this.basePage.masterTemplateId;

        const structure = (await this.typeStructureRestService.getStructureByTypeAndMode(
            this.pageData.typeCode,
            'DEFAULT',
            true
        )) as CMSItemStructure;

        this.pageData.type = structure.type;
    }

    private async getPageUUID(pageUUID: string): Promise<string> {
        return !stringUtils.isBlank(pageUUID) ? pageUUID : this.pageInfoService.getPageUUID();
    }

    private async updatePageInfoFields(): Promise<void> {
        if (typeof this.pageData.defaultPage === 'undefined') {
            return;
        }

        if (this.pageData.typeCode) {
            const pageInfoFields =
                await this.contextAwarePageStructureService.getPageStructureForNewPage(
                    this.pageData.typeCode,
                    this.pageData.defaultPage
                );
            this.pageInfoStructure = pageInfoFields;
        } else {
            this.pageInfoStructure = {} as GenericEditorStructure;
        }
    }
}

@Injectable()
export class ClonePageBuilderFactory {
    constructor(
        private contextAwarePageStructureService: ContextAwarePageStructureService,
        private typeStructureRestService: TypeStructureRestService,
        private cmsitemsRestService: CmsitemsRestService,
        private catalogService: ICatalogService,
        private pageInfoService: IPageInfoService,
        private pageService: IPageService
    ) {}
    public createClonePageBuilder(
        restrictionsStepHandler: IRestrictionsStepHandler,
        basePageUUID: string,
        uriContext: IUriContext
    ): ClonePageBuilder {
        return new ClonePageBuilder(
            restrictionsStepHandler,
            basePageUUID,
            uriContext,
            this.contextAwarePageStructureService,
            this.typeStructureRestService,
            this.cmsitemsRestService,
            this.catalogService,
            this.pageInfoService
        );
    }
}
