/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ICMSPage } from 'smarteditcommons';

export interface IPageRestrictionCriteria {
    id: string;
    editLabel: string;
    label: string;
    value: boolean;
}

/**
 * A service for working with restriction criteria.
 */
@Injectable()
export class PageRestrictionsCriteriaService {
    private readonly ALL: IPageRestrictionCriteria = {} as IPageRestrictionCriteria;
    private readonly ANY: IPageRestrictionCriteria = {} as IPageRestrictionCriteria;
    private readonly restrictionCriteriaOptions: IPageRestrictionCriteria[] = [this.ALL, this.ANY];

    constructor() {
        this.setupCriteria(this.ALL, 'all', false);
        this.setupCriteria(this.ANY, 'any', true);
    }

    /**
     * @param onlyOneRestrictionMustApply A boolean to determine whether one restriction should be applied.
     * @returns The i18n key of the restriction criteria.
     */
    public getMatchCriteriaLabel(onlyOneRestrictionMustApply: boolean): string {
        if (onlyOneRestrictionMustApply) {
            return this.ANY.label;
        }
        return this.ALL.label;
    }

    /**
     * @returns An array of criteria options.
     */
    public getRestrictionCriteriaOptions(): IPageRestrictionCriteria[] {
        return this.restrictionCriteriaOptions;
    }

    /**
     * @returns An object of the restriction criteria for the given page.
     */
    public getRestrictionCriteriaOptionFromPage(page?: ICMSPage): IPageRestrictionCriteria {
        if (page && typeof page.onlyOneRestrictionMustApply === 'boolean') {
            if (page.onlyOneRestrictionMustApply) {
                return this.ANY;
            }
        }
        return this.ALL;
    }

    private setupCriteria(
        criteria: IPageRestrictionCriteria,
        id: string,
        boolValue: boolean
    ): void {
        Object.defineProperty(criteria, 'id', {
            writable: false,
            value: id
        });
        Object.defineProperty(criteria, 'label', {
            writable: false,
            value: 'se.cms.restrictions.criteria.' + id
        });
        Object.defineProperty(criteria, 'editLabel', {
            writable: false,
            value: 'se.cms.restrictions.criteria.select.' + id
        });
        Object.defineProperty(criteria, 'value', {
            writable: false,
            value: boolValue
        });
    }
}
