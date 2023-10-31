import { ICatalogService, IUriContext } from 'smarteditcommons';
export interface IDisplayCondition {
    label: string;
    description: string;
    isPrimary: boolean;
}
/**
 * The pageDisplayConditionsService provides an abstraction layer for the business logic of
 * primary/variant display conditions of a page
 */
export declare class PageDisplayConditionsService {
    private catalogService;
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
    constructor(catalogService: ICatalogService);
    /**
     * @returns An array of page conditions that are the
     * possible conditions if you wish to create a new page of the given page type that has the given possible primary
     * pages
     */
    getNewPageConditions(pageTypeCode: string, uriContext: IUriContext): Promise<IDisplayCondition[]>;
    private fetchDisplayConditionsForPageType;
    private getPageDisplayConditionsByPageType;
}
