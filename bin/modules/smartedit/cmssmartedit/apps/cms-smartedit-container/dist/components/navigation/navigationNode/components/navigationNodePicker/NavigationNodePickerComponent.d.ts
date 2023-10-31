import { OnInit } from '@angular/core';
import { SystemEventService, IUriContext, TypedMap, CmsitemsRestService } from 'smarteditcommons';
import { NavigationNodePickerRenderComponent } from './NavigationNodePickerRenderComponent';
export declare class NavigationNodePickerComponent implements OnInit {
    private cmsitemsRestService;
    private systemEventService;
    uriContext: IUriContext;
    editable: boolean;
    nodeURI: string;
    rootNodeUid: string;
    removeDefaultTemplate: boolean;
    actions: TypedMap<(...args: any[]) => void>;
    nodePickerRenderComponent: typeof NavigationNodePickerRenderComponent;
    constructor(cmsitemsRestService: CmsitemsRestService, systemEventService: SystemEventService);
    ngOnInit(): void;
}
