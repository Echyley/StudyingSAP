/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable max-classes-per-file */
import { Injectable } from '@angular/core';
import {
    GenericEditorStructure,
    ICatalogService,
    IUriContext,
    Nullable,
    ICMSPage,
    CMSPageTypes,
    PageTemplateType
} from 'smarteditcommons';
import { PageType } from '../../dao/PageTypeService';
import { IRestrictionsStepHandler } from '../../interfaces';
import { ContextAwarePageStructureService } from '../ContextAwarePageStructureService';
import { PageBuilderModel } from './types';

export class PageBuilder {
    private model: PageBuilderModel;
    private page: ICMSPage;

    constructor(
        private catalogService: ICatalogService,
        private contextAwarePageStructureService: ContextAwarePageStructureService,
        private restrictionsStepHandler: IRestrictionsStepHandler,
        private uriContext: IUriContext
    ) {
        this.model = {};
        this.page = {
            restrictions: []
        } as ICMSPage;
        this.catalogService.getCatalogVersionUUid(this.uriContext).then((catalogVersionUuid) => {
            this.page.catalogVersion = catalogVersionUuid;
        });
    }

    public pageTypeSelected(pageTypeObject: PageType): Promise<void> {
        this.model.pageType = pageTypeObject;
        this.model.pageTemplate = null;
        return this.updatePageInfoFields();
    }

    public pageTemplateSelected(pageTemplateObject: PageTemplateType): void {
        this.model.pageTemplate = pageTemplateObject;
    }

    public getPageTypeCode(): Nullable<CMSPageTypes> {
        return this.model.pageType?.code || null;
    }

    public getTemplateUuid(): string {
        return this.model.pageTemplate?.uuid || '';
    }

    public getPage(): ICMSPage {
        this.page.typeCode = this.getPageTypeCode();
        this.page.itemtype = this.page.typeCode;
        this.page.type = this.model.pageType?.type || null;
        this.page.masterTemplate = this.getTemplateUuid() || null;
        this.page.template = this.model.pageTemplate?.uid || null;

        return this.page;
    }

    public getPageRestrictions(): string[] {
        return this.page.restrictions;
    }

    public setPageUid(uid: string): void {
        this.page.uid = uid;
    }

    public setRestrictions(onlyOneRestrictionMustApply: boolean, restrictions: string[]): void {
        this.page.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
        this.page.restrictions = restrictions;
    }

    public getPageInfoStructure(): GenericEditorStructure {
        return this.model.pageInfoFields;
    }

    public displayConditionSelected(displayConditionResult: ICMSPage): Promise<void> {
        const isPrimaryPage = displayConditionResult.isPrimary;
        this.page.defaultPage = isPrimaryPage;
        this.page.homepage = displayConditionResult.homepage;
        if (isPrimaryPage) {
            this.page.label = null;
            this.restrictionsStepHandler.hideStep();
        } else {
            this.page.label = displayConditionResult.primaryPage
                ? (displayConditionResult.primaryPage as ICMSPage).label
                : '';
            this.restrictionsStepHandler.showStep();
        }
        return this.updatePageInfoFields();
    }

    private async updatePageInfoFields(): Promise<void> {
        if (this.page.defaultPage !== undefined) {
            if (this.model.pageType) {
                const pageInfoFields =
                    await this.contextAwarePageStructureService.getPageStructureForNewPage(
                        this.model.pageType.code,
                        this.page.defaultPage
                    );
                this.model.pageInfoFields = pageInfoFields;
            } else {
                this.model.pageInfoFields = {} as GenericEditorStructure;
            }
        }
    }
}

@Injectable()
export class PageBuilderFactory {
    constructor(
        private catalogService: ICatalogService,
        private contextAwarePageStructureService: ContextAwarePageStructureService
    ) {}

    public createPageBuilder(
        restrictionsStepHandler: IRestrictionsStepHandler,
        uriContext: IUriContext
    ): PageBuilder {
        return new PageBuilder(
            this.catalogService,
            this.contextAwarePageStructureService,
            restrictionsStepHandler,
            uriContext
        );
    }
}
