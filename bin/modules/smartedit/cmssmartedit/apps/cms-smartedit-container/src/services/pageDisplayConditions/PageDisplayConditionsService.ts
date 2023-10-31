/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IBaseCatalogVersion,
    ICatalogService,
    IPageDisplayConditions,
    IPageDisplayConditionsOption,
    IUriContext
} from 'smarteditcommons';

export interface IDisplayCondition {
    label: string;
    description: string;
    isPrimary: boolean;
}
/**
 * The pageDisplayConditionsService provides an abstraction layer for the business logic of
 * primary/variant display conditions of a page
 */
@Injectable()
export class PageDisplayConditionsService {
    /**
     * An object representing a page display condition<br/>
     * Structure:<br/>
     * ```
     * {
     *      label: [string] key to be localized to render this condition on a webpage
     *      description: [string] key to be localized to render this condition description on a webpage
     *      isPrimary: [boolean]
     * }
     * ```
     */

    constructor(private catalogService: ICatalogService) {}

    /**
     * @returns An array of page conditions that are the
     * possible conditions if you wish to create a new page of the given page type that has the given possible primary
     * pages
     */

    public getNewPageConditions(
        pageTypeCode: string,
        uriContext: IUriContext
    ): Promise<IDisplayCondition[]> {
        return this.getPageDisplayConditionsByPageType(pageTypeCode, uriContext);
    }

    private fetchDisplayConditionsForPageType(
        pageType: string,
        uriContext: IUriContext
    ): Promise<IPageDisplayConditions> {
        return this.catalogService
            .getContentCatalogVersion(uriContext)
            .then((catalogVersion: IBaseCatalogVersion) =>
                catalogVersion.pageDisplayConditions.find(
                    (condition: IPageDisplayConditions) => condition.typecode === pageType
                )
            );
    }

    private getPageDisplayConditionsByPageType(
        pageType: string,
        uriContext: IUriContext
    ): Promise<IDisplayCondition[]> {
        return this.fetchDisplayConditionsForPageType(pageType, uriContext).then((obj) => {
            if (!obj || !obj.options) {
                return [];
            }

            return obj.options.map((option: IPageDisplayConditionsOption) => ({
                label: option.label,
                description: option.label + '.description',
                isPrimary: option.id === 'PRIMARY'
            }));
        });
    }
}
