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
export declare class PageRestrictionsCriteriaService {
    private readonly ALL;
    private readonly ANY;
    private readonly restrictionCriteriaOptions;
    constructor();
    /**
     * @param onlyOneRestrictionMustApply A boolean to determine whether one restriction should be applied.
     * @returns The i18n key of the restriction criteria.
     */
    getMatchCriteriaLabel(onlyOneRestrictionMustApply: boolean): string;
    /**
     * @returns An array of criteria options.
     */
    getRestrictionCriteriaOptions(): IPageRestrictionCriteria[];
    /**
     * @returns An object of the restriction criteria for the given page.
     */
    getRestrictionCriteriaOptionFromPage(page?: ICMSPage): IPageRestrictionCriteria;
    private setupCriteria;
}
