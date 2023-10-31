import { TranslateService } from '@ngx-translate/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import { CmsitemsRestService, IPageService, ICMSPage, IGenericEditorModalServiceComponent } from 'smarteditcommons';
import { ContextAwarePageStructureService } from '../../../services';
export declare class PageEditorModalConfigService {
    private cmsitemsRestService;
    private pageService;
    private contextAwarePageStructureService;
    private contextAwareEditableItemService;
    private translateService;
    constructor(cmsitemsRestService: CmsitemsRestService, pageService: IPageService, contextAwarePageStructureService: ContextAwarePageStructureService, contextAwareEditableItemService: IContextAwareEditableItemService, translateService: TranslateService);
    /** Creates a config for given Page that can be used to open a modal.  */
    create(page: ICMSPage): Promise<IGenericEditorModalServiceComponent>;
    private filterPrimaryPageAttributes;
    private isPrimaryPageAttribute;
    private ensureReadOnlyMode;
}
