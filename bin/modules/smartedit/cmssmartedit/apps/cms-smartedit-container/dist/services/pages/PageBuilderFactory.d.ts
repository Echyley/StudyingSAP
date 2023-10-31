import { GenericEditorStructure, ICatalogService, IUriContext, Nullable, ICMSPage, CMSPageTypes, PageTemplateType } from 'smarteditcommons';
import { PageType } from '../../dao/PageTypeService';
import { IRestrictionsStepHandler } from '../../interfaces';
import { ContextAwarePageStructureService } from '../ContextAwarePageStructureService';
export declare class PageBuilder {
    private catalogService;
    private contextAwarePageStructureService;
    private restrictionsStepHandler;
    private uriContext;
    private model;
    private page;
    constructor(catalogService: ICatalogService, contextAwarePageStructureService: ContextAwarePageStructureService, restrictionsStepHandler: IRestrictionsStepHandler, uriContext: IUriContext);
    pageTypeSelected(pageTypeObject: PageType): Promise<void>;
    pageTemplateSelected(pageTemplateObject: PageTemplateType): void;
    getPageTypeCode(): Nullable<CMSPageTypes>;
    getTemplateUuid(): string;
    getPage(): ICMSPage;
    getPageRestrictions(): string[];
    setPageUid(uid: string): void;
    setRestrictions(onlyOneRestrictionMustApply: boolean, restrictions: string[]): void;
    getPageInfoStructure(): GenericEditorStructure;
    displayConditionSelected(displayConditionResult: ICMSPage): Promise<void>;
    private updatePageInfoFields;
}
export declare class PageBuilderFactory {
    private catalogService;
    private contextAwarePageStructureService;
    constructor(catalogService: ICatalogService, contextAwarePageStructureService: ContextAwarePageStructureService);
    createPageBuilder(restrictionsStepHandler: IRestrictionsStepHandler, uriContext: IUriContext): PageBuilder;
}
