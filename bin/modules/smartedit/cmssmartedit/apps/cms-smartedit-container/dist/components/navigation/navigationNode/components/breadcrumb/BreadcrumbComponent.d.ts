import { OnInit, ChangeDetectorRef } from '@angular/core';
import { NavigationEditorNodeService } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorNodeService';
import { IUriContext, TreeNodeWithLevel, CmsitemsRestService } from 'smarteditcommons';
export declare class BreadcrumbComponent implements OnInit {
    private navigationEditorNodeService;
    private cmsitemsRestService;
    private cdr;
    nodeUid: string;
    nodeUuid: string;
    uriContext: IUriContext;
    breadcrumb: TreeNodeWithLevel[];
    constructor(navigationEditorNodeService: NavigationEditorNodeService, cmsitemsRestService: CmsitemsRestService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    private getUid;
}
