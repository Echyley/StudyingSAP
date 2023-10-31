import { EventEmitter, OnInit, Type } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PersonalizationsmarteditMessageHandler } from 'personalizationcommons';
import { Observable } from 'rxjs';
import { ISlotRestrictionsService, ModalManagerService, ModalButtonAction, ModalButtonStyle, IEditorModalService } from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from '../service/PersonalizationsmarteditRestService';
export declare class PersonalizationsmarteditContextMenuAddEditActionComponent implements OnInit {
    private modalManager;
    private translateService;
    private personalizationsmarteditRestService;
    private personalizationsmarteditMessageHandler;
    private personalizationsmarteditContextService;
    private readonly slotRestrictionsService;
    private readonly editorModalService;
    catalogFilter: any;
    catalogVersionFilter: any;
    letterIndicatorForElement: string;
    colorIndicatorForElement: string;
    slotId: string;
    actionId: string;
    components: any[];
    componentUuid: any;
    defaultComponentId: any;
    editEnabled: boolean;
    slotCatalog: any;
    componentCatalog: any;
    selectedCustomizationCode: string;
    selectedVariationCode: string;
    componentType: string;
    actions: any[];
    actionSelected: string;
    idComponentSelected: string;
    newComponentTypes: any[];
    selectedCustomization: any;
    selectedVariation: any;
    newComponentSelected: string;
    componentSelected: any;
    actionCreated: EventEmitter<void>;
    actionFetchStrategy: {
        fetchAll: any;
    };
    componentsFetchStrategy: {
        fetchPage: any;
        fetchEntity: any;
    };
    componentTypesFetchStrategy: {
        fetchAll: any;
    };
    itemComponent: Type<any>;
    modalButtons: ({
        id: string;
        style: ModalButtonStyle;
        label: string;
        action: ModalButtonAction;
        disabledFn: () => any;
        callback: () => Observable<any>;
    } | {
        id: string;
        label: string;
        style: ModalButtonStyle;
        action: ModalButtonAction;
        disabledFn?: undefined;
        callback?: undefined;
    })[];
    constructor(modalManager: ModalManagerService, translateService: TranslateService, personalizationsmarteditRestService: PersonalizationsmarteditRestService, personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler, personalizationsmarteditContextService: PersonalizationsmarteditContextService, slotRestrictionsService: ISlotRestrictionsService, editorModalService: IEditorModalService);
    get modalData(): Observable<any>;
    ngOnInit(): void;
    initNewComponentTypes: () => any;
    getAndSetComponentById: (componentUuid: any) => any;
    getAndSetColorAndLetter: () => any;
    componentSelectedEvent: (item: any) => any;
    newComponentTypeSelectedEvent: (item: any) => any;
    editAction: (customizationId: any, variationId: any, actionId: any, componentId: any, componentCatalog: any, filter: any) => any;
    addActionToContainer: (componentId: any, catalogId: any, containerSourceId: any, customizationId: any, variationId: any, filter: any) => any;
    catalogVersionFilterChange: (value: any) => any;
    private init;
}
