import { OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { SystemEventService, GenericEditorField, GenericEditorStackService, LogService, GenericEditorWidgetData, TypedMap, ActionableSearchItem } from 'smarteditcommons';
import { NavigationNodeCMSItem } from '../../../components/navigation/types';
import { CmsDropdownItemComponent } from './components';
import { NestedComponentManagementService, SelectComponentTypeModalService } from './services';
export interface CMSNavigationEntry {
    catalogVersion: string;
    creationtime: string;
    id: string;
    item: string;
    itemtype: string;
    label: string;
    modifiedtime: string;
    name: string;
    navigationNode: string;
    technicalUniqueId: string;
    typeCode?: string;
    uid: string;
    uuid: string;
}
export interface ItemWithQualifier {
    item: CMSNavigationEntry;
    qualifier: string;
}
export declare class CmsComponentDropdownComponent implements OnInit, OnDestroy {
    private systemEventService;
    private genericEditorStackService;
    private nestedComponentManagementService;
    private selectComponentTypeModalService;
    private logService;
    private cdr;
    componentButtonPressedEventId: string;
    createComponentButtonUnRegFn: () => void;
    editComponentClickedUnRegFn: () => void;
    editorStackId: string;
    field: GenericEditorField & {
        subTypes?: TypedMap<string>;
    };
    forceRecompile: boolean;
    id: string;
    itemComponent: typeof CmsDropdownItemComponent;
    model: NavigationNodeCMSItem;
    qualifier: string;
    actionableSearchItem: ActionableSearchItem;
    private searchQuery;
    constructor(data: GenericEditorWidgetData<NavigationNodeCMSItem>, systemEventService: SystemEventService, genericEditorStackService: GenericEditorStackService, nestedComponentManagementService: NestedComponentManagementService, selectComponentTypeModalService: SelectComponentTypeModalService, logService: LogService, cdr: ChangeDetectorRef);
    ngOnInit(): void;
    ngOnDestroy(): void;
    private onCreateComponentButtonPressed;
    private onEditComponentClicked;
    private createNestedComponent;
    private editComponent;
    private nestedComponentEditorHandler;
    private _detectChanges;
}
