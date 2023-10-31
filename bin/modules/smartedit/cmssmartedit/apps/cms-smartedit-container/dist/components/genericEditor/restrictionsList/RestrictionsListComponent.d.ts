import { ChangeDetectorRef } from '@angular/core';
import { GenericEditorWidgetData, CmsitemsRestService } from 'smarteditcommons';
import { PageInfoForViewing } from '../../pages/services';
import { RestrictionCMSItem } from '../../restrictions/types';
export declare class RestrictionsListComponent {
    data: GenericEditorWidgetData<PageInfoForViewing>;
    private cmsitemsRestService;
    private cdr;
    pageInfo: PageInfoForViewing;
    restrictions: RestrictionCMSItem[];
    constructor(data: GenericEditorWidgetData<PageInfoForViewing>, cmsitemsRestService: CmsitemsRestService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
}
