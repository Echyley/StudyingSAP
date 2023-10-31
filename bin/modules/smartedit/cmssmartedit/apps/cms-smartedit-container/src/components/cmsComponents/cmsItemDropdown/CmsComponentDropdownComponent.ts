/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    OnDestroy,
    Inject,
    ViewEncapsulation,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION } from 'cmscommons';
import {
    SystemEventService,
    GenericEditorField,
    GenericEditorStackService,
    LogService,
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    TypedMap,
    ActionableSearchItem
} from 'smarteditcommons';
import { NavigationNodeCMSItem } from '../../../components/navigation/types';
import {
    CREATE_COMPONENT_BUTTON_PRESSED_EVENT_ID,
    ON_EDIT_NESTED_COMPONENT_EVENT
} from './CmsComponentConstants';
import { CmsDropdownItemComponent } from './components';
import {
    NestedComponentManagementService,
    SelectComponentTypeModalService,
    NestedComponentInfo
} from './services';

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

@Component({
    selector: 'se-cms-component-dropdown',
    templateUrl: './CmsComponentDropdownComponent.html',
    styleUrls: ['./CmsComponentDropdownComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CmsComponentDropdownComponent implements OnInit, OnDestroy {
    public componentButtonPressedEventId: string;
    public createComponentButtonUnRegFn: () => void;
    public editComponentClickedUnRegFn: () => void;
    public editorStackId: string;
    public field: GenericEditorField & { subTypes?: TypedMap<string> };
    public forceRecompile: boolean;
    public id: string;
    public itemComponent: typeof CmsDropdownItemComponent;
    public model: NavigationNodeCMSItem;
    public qualifier: string;
    public actionableSearchItem: ActionableSearchItem;

    private searchQuery: string;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<NavigationNodeCMSItem>,
        private systemEventService: SystemEventService,
        private genericEditorStackService: GenericEditorStackService,
        private nestedComponentManagementService: NestedComponentManagementService,
        private selectComponentTypeModalService: SelectComponentTypeModalService,
        private logService: LogService,
        private cdr: ChangeDetectorRef
    ) {
        ({ field: this.field, model: this.model, id: this.id, qualifier: this.qualifier } = data);
        this.editorStackId = data.editor.editorStackId;

        this.forceRecompile = true;
    }

    ngOnInit(): void {
        this.itemComponent = CmsDropdownItemComponent;

        this.field.params = this.field.params || {};
        this.field.editorStackId = this.editorStackId;

        this.field.params.catalogId = CONTEXT_CATALOG;
        this.field.params.catalogVersion = CONTEXT_CATALOG_VERSION;

        this.componentButtonPressedEventId = `${CREATE_COMPONENT_BUTTON_PRESSED_EVENT_ID}_${this.qualifier}`;
        // if has no subTypes, it can not create nestComponent, then the actionableSearchItem button will be disable.
        this.actionableSearchItem =
            !!this.field.subTypes && Object.keys(this.field.subTypes).length > 0
                ? { eventId: this.componentButtonPressedEventId }
                : undefined;
        this.createComponentButtonUnRegFn = this.systemEventService.subscribe(
            this.componentButtonPressedEventId,
            (_eventId, data: string) => this.onCreateComponentButtonPressed(data)
        );
        this.editComponentClickedUnRegFn = this.systemEventService.subscribe(
            ON_EDIT_NESTED_COMPONENT_EVENT,
            (_eventId, data: ItemWithQualifier) => this.onEditComponentClicked(data)
        );
    }

    ngOnDestroy(): void {
        this.createComponentButtonUnRegFn();
        this.editComponentClickedUnRegFn();
    }

    private async onCreateComponentButtonPressed(query: string): Promise<void> {
        this.searchQuery = query;
        if (!this.genericEditorStackService.isTopEditorInStack(this.editorStackId, this.id)) {
            return;
        }
        if (this.field.subTypes) {
            const keys = Object.keys(this.field.subTypes);
            if (keys.length > 1) {
                const subTypeId = await this.selectComponentTypeModalService.open(
                    this.field.subTypes
                );
                if (!!subTypeId) {
                    this.createNestedComponent(subTypeId);
                }
            } else {
                this.createNestedComponent(keys[0]);
            }
        }
    }

    private onEditComponentClicked(payload: ItemWithQualifier): void {
        if (this.genericEditorStackService.isTopEditorInStack(this.editorStackId, this.id)) {
            if (this.qualifier === payload.qualifier) {
                this.editComponent(payload.item);
            }
        }
    }

    private async createNestedComponent(componentType: string): Promise<void> {
        const componentInfo = {
            componentId: null,
            componentUuid: null,
            componentType,
            content: {
                name: this.searchQuery,
                catalogVersion: this.model.catalogVersion
            }
        };

        await this.nestedComponentEditorHandler(componentInfo);
    }

    private async editComponent(itemToEdit: CMSNavigationEntry): Promise<void> {
        const componentInfo = {
            componentId: itemToEdit.uid,
            componentUuid: itemToEdit.uuid,
            componentType: itemToEdit.typeCode || itemToEdit.itemtype,
            content: itemToEdit
        };

        await this.nestedComponentEditorHandler(componentInfo);
    }

    private async nestedComponentEditorHandler(componentInfo: NestedComponentInfo): Promise<void> {
        try {
            const item = await this.nestedComponentManagementService.openNestedComponentEditor(
                componentInfo,
                this.editorStackId
            );

            if (this.field.collection) {
                if (!this.model[this.qualifier]) {
                    this.model[this.qualifier] = [];
                }
                // if the component is not existing in previous selected items, append it at the end
                if ((this.model[this.qualifier] as string[]).indexOf(item.uuid) === -1) {
                    this.model[this.qualifier] = (this.model[this.qualifier] as string[]).concat(
                        item.uuid
                    );
                }
                // otherwise, reload directly ( the name of existing nested component might be changed when editing )
                else {
                    this.forceRecompile = false;
                    setTimeout(() => {
                        this.forceRecompile = true;
                        this._detectChanges();
                    });
                }
            } else {
                // Because item.uuid stays the same we need to force reload the item in previous modal
                this.forceRecompile = false;
                setTimeout(() => {
                    this.forceRecompile = true;
                    this.model[this.qualifier] = item.uuid;
                    this._detectChanges();
                });
            }
            this._detectChanges();
        } catch (error) {
            // When modal is closed promise is rejected but it does not have error, so no need to log anything
            if (error) {
                this.logService.warn(
                    `Something went wrong when creating or editing Nested component (${componentInfo.componentType})`,
                    error
                );
            }
        }
    }

    private _detectChanges(): void {
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
