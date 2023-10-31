import { TranslateService } from '@ngx-translate/core';
import { CMSItemStructure } from 'cmscommons';
import { LogService, PageVersionSelectionService, IPageService, ICMSPage } from 'smarteditcommons';
import { TypeStructureRestService } from '../../../dao/TypeStructureRestService';
import { DisplayConditionsFacade } from '../../../facades';
import { PageEditorModalService } from './PageEditorModalService';
export interface PageInfoForViewing extends ICMSPage {
    template: string;
    primaryPage: string | null;
    displayCondition: string;
    content: ICMSPage;
    localizedType: string;
    restrictionsCriteria: string | null;
}
/**
 * This service is used to provide all the information necessary to properly display the Page Info Menu.
 */
export declare class PageInfoMenuService {
    private displayConditionsFacade;
    private logService;
    private pageEditorModalService;
    private pageService;
    private pageVersionSelectionService;
    private translateService;
    private typeStructureRestService;
    private ALL_RESTRICTIONS_CRITERIA_LABEL;
    private ANY_RESTRICTIONS_CRITERIA_LABEL;
    private isPageEditorOpened;
    constructor(displayConditionsFacade: DisplayConditionsFacade, logService: LogService, pageEditorModalService: PageEditorModalService, pageService: IPageService, pageVersionSelectionService: PageVersionSelectionService, translateService: TranslateService, typeStructureRestService: TypeStructureRestService);
    openPageEditor(pageInfo: ICMSPage): void;
    /**
     * Retrieves the information for the current page and prepares it to be displayed in the Page Info Menu.
     */
    getCurrentPageInfo(): Promise<PageInfoForViewing | void>;
    /**
     * Retrieves the page structure and adapts the fields to match the order expected in the Page Info Menu.
     */
    getPageStructureForViewing(pageTypeCode: string, isPrimaryPage: boolean): Promise<CMSItemStructure | void>;
    private addFieldToStructure;
    private buildField;
    private structureContainsItemByFieldQualifier;
    private removeFieldFromStructure;
    private getPrimaryPageName;
    private isVariationPage;
    private getPageDisplayCondition;
    private getPageRestrictionsCriteria;
    private getCurrentPageVersionId;
}
