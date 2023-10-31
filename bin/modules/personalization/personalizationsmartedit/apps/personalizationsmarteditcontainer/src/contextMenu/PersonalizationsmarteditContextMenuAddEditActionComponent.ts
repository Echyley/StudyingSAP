/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Inject, OnInit, Type } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    PersonalizationsmarteditMessageHandler,
    PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING
} from 'personalizationcommons';
import { Observable } from 'rxjs';
import {
    ISlotRestrictionsService,
    ModalManagerService,
    ModalButtonAction,
    ModalButtonStyle,
    IEditorModalService
} from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from '../service/PersonalizationsmarteditRestService';
import { ComponentDropdownItemPrinterComponent } from './ComponentDropdownItemPrinterComponent';
@Component({
    templateUrl: './PersonalizationsmarteditContextMenuAddEditActionComponent.html'
})
export class PersonalizationsmarteditContextMenuAddEditActionComponent implements OnInit {
    public catalogFilter: any;
    public catalogVersionFilter: any;
    public letterIndicatorForElement: string;
    public colorIndicatorForElement: string;
    public slotId: string;
    public actionId: string;
    public components: any[];
    public componentUuid: any;
    public defaultComponentId: any;
    public editEnabled: boolean;
    public slotCatalog: any;
    public componentCatalog: any;
    public selectedCustomizationCode: string;
    public selectedVariationCode: string;
    public componentType: string;
    public actions: any[];
    public actionSelected = '';
    public idComponentSelected = '';
    public newComponentTypes: any[];
    public selectedCustomization: any = {};
    public selectedVariation: any = {};
    public newComponentSelected = '';
    public componentSelected: any = {};
    public actionCreated: EventEmitter<void> = new EventEmitter<void>();
    public actionFetchStrategy: {
        fetchAll: any;
    };
    public componentsFetchStrategy: {
        fetchPage: any;
        fetchEntity: any;
    };
    public componentTypesFetchStrategy: {
        fetchAll: any;
    };
    public itemComponent: Type<any> = ComponentDropdownItemPrinterComponent;

    public modalButtons = [
        {
            id: 'submit',
            style: ModalButtonStyle.Primary,
            label: 'personalization.modal.addeditaction.button.submit',
            action: ModalButtonAction.Close,
            disabledFn: (): any =>
                !Boolean(this.idComponentSelected) ||
                (Boolean(this.componentUuid) && this.componentUuid === this.idComponentSelected),
            callback: (): Observable<any> => {
                this.idComponentSelected = undefined; // To disable 'save' button
                const componentCatalogId = this.componentSelected.catalogVersion.substring(
                    0,
                    this.componentSelected.catalogVersion.indexOf('/')
                );
                const filter = {
                    catalog: this.selectedCustomization.catalog,
                    catalogVersion: this.selectedCustomization.catalogVersion
                };
                const extraCatalogFilter = {
                    slotCatalog: this.slotCatalog,
                    oldComponentCatalog: this.componentCatalog
                };

                Object.assign(extraCatalogFilter, filter);

                if (this.editEnabled) {
                    this.editAction(
                        this.selectedCustomization.code,
                        this.selectedVariation.code,
                        this.actionId,
                        this.componentSelected.uid,
                        componentCatalogId,
                        filter
                    );
                } else {
                    this.personalizationsmarteditRestService
                        .replaceComponentWithContainer(
                            this.defaultComponentId,
                            this.slotId,
                            extraCatalogFilter
                        )
                        .then(
                            (result: any) => {
                                this.addActionToContainer(
                                    this.componentSelected.uid,
                                    componentCatalogId,
                                    result.sourceId,
                                    this.selectedCustomization.code,
                                    this.selectedVariation.code,
                                    filter
                                );
                            },
                            (): any => {
                                this.personalizationsmarteditMessageHandler.sendError(
                                    this.translateService.instant(
                                        'personalization.error.replacingcomponent'
                                    )
                                );
                            }
                        );
                }
                return this.actionCreated;
            }
        },
        {
            id: 'cancel',
            label: 'personalization.modal.addeditaction.button.cancel',
            style: ModalButtonStyle.Default,
            action: ModalButtonAction.Dismiss
        }
    ];

    constructor(
        @Inject(ModalManagerService)
        private modalManager: ModalManagerService,
        @Inject(TranslateService) private translateService: TranslateService,
        @Inject(PersonalizationsmarteditRestService)
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        @Inject(PersonalizationsmarteditMessageHandler)
        private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        @Inject(PersonalizationsmarteditContextService)
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private readonly slotRestrictionsService: ISlotRestrictionsService,
        private readonly editorModalService: IEditorModalService
    ) {
        this.components = [];

        this.modalManager.addButton(this.modalButtons[0]);
        this.modalManager.addButton(this.modalButtons[1]);
        this.actionFetchStrategy = {
            fetchAll: (): any => Promise.resolve(this.actions)
        };
        this.componentsFetchStrategy = {
            fetchPage: (mask: string, pageSize: number, currentPage: number): any =>
                this.componentTypesFetchStrategy.fetchAll().then((componentTypes: any) => {
                    const typeCodes = componentTypes.map((elem: any) => elem.code).join(',');
                    const filter = {
                        currentPage,
                        mask,
                        typeCodes,
                        pageSize: 30,
                        sort: 'name',
                        catalog: this.catalogFilter,
                        catalogVersion: this.catalogVersionFilter
                    };

                    return this.personalizationsmarteditRestService.getComponents(filter).then(
                        (resp: any) => {
                            const filteredComponents = resp.response.filter(
                                (elem: any) => !elem.restricted
                            );

                            return Promise.resolve({
                                results: filteredComponents,
                                pagination: resp.pagination
                            });
                        },
                        () => {
                            // error
                            this.personalizationsmarteditMessageHandler.sendError(
                                this.translateService.instant(
                                    'personalization.error.gettingcomponents'
                                )
                            );
                            return Promise.reject();
                        }
                    );
                }),
            fetchEntity: (uuid: any): any =>
                this.personalizationsmarteditRestService.getComponent(uuid).then((resp: any) =>
                    Promise.resolve({
                        id: resp.uuid,
                        name: resp.name,
                        typeCode: resp.typeCode
                    })
                )
        };
        this.componentTypesFetchStrategy = {
            fetchAll: (): any => {
                if (this.newComponentTypes) {
                    return Promise.resolve(this.newComponentTypes);
                } else {
                    return this.initNewComponentTypes();
                }
            }
        };
    }

    public get modalData(): Observable<any> {
        return this.modalManager.getModalData();
    }

    ngOnInit(): void {
        this.init();
    }

    initNewComponentTypes = (): any =>
        this.slotRestrictionsService.getSlotRestrictions(this.slotId).then(
            (restrictions: any) =>
                this.personalizationsmarteditRestService.getNewComponentTypes().then(
                    (resp: any) => {
                        this.newComponentTypes = resp.componentTypes
                            .filter((elem: any) => restrictions.indexOf(elem.code) > -1)
                            .map((elem: any) => {
                                elem.id = elem.code;
                                return elem;
                            });
                        return this.newComponentTypes;
                    },
                    () => {
                        // error
                        this.personalizationsmarteditMessageHandler.sendError(
                            this.translateService.instant(
                                'personalization.error.gettingcomponentstypes'
                            )
                        );
                    }
                ),
            () => {
                this.personalizationsmarteditMessageHandler.sendError(
                    this.translateService.instant('personalization.error.gettingslotrestrictions')
                );
            }
        );

    getAndSetComponentById = (componentUuid: any): any => {
        this.personalizationsmarteditRestService.getComponent(componentUuid).then(
            (resp: any) => {
                this.idComponentSelected = resp.uuid;
            },
            () => {
                this.personalizationsmarteditMessageHandler.sendError(
                    this.translateService.instant('personalization.error.gettingcomponents')
                );
            }
        );
    };

    getAndSetColorAndLetter = (): any => {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        if (combinedView.enabled) {
            (combinedView.selectedItems || []).forEach((element: any, index: any) => {
                let state = this.selectedCustomizationCode === element.customization.code;
                state = state && this.selectedVariationCode === element.variation.code;
                const wrappedIndex =
                    index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
                if (state) {
                    this.letterIndicatorForElement = String.fromCharCode(
                        'a'.charCodeAt(0) + wrappedIndex
                    ).toUpperCase();
                    this.colorIndicatorForElement =
                        PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
                }
            });
        }
    };

    componentSelectedEvent = (item: any): any => {
        if (!item) {
            return;
        }
        this.componentSelected = item;
        this.idComponentSelected = item.uuid;
    };

    newComponentTypeSelectedEvent = (item: any): any => {
        if (!item) {
            return;
        }
        const componentAttributes = {
            smarteditComponentType: item.code,
            catalogVersionUuid:
                this.personalizationsmarteditContextService.getSeData().seExperienceData.pageContext
                    .catalogVersionUuid
        };

        this.editorModalService.open(componentAttributes).then(
            (response: any) => {
                this.actionSelected = this.actions.filter((action) => action.id === 'use')[0].id;
                this.idComponentSelected = response.uuid;
                this.componentSelected = response;
            },
            () => {
                this.newComponentSelected = '';
            }
        );
    };

    editAction = (
        customizationId: any,
        variationId: any,
        actionId: any,
        componentId: any,
        componentCatalog: any,
        filter: any
    ): any => {
        this.personalizationsmarteditRestService
            .editAction(
                customizationId,
                variationId,
                actionId,
                componentId,
                componentCatalog,
                filter
            )
            .then(
                () => {
                    // success
                    this.personalizationsmarteditMessageHandler.sendSuccess(
                        this.translateService.instant('personalization.info.updatingaction')
                    );
                    this.actionCreated.emit();
                },
                () => {
                    // error
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant('personalization.error.updatingaction')
                    );
                    this.actionCreated.emit();
                }
            );
    };

    addActionToContainer = (
        componentId: any,
        catalogId: any,
        containerSourceId: any,
        customizationId: any,
        variationId: any,
        filter: any
    ): any => {
        this.personalizationsmarteditRestService
            .addActionToContainer(
                componentId,
                catalogId,
                containerSourceId,
                customizationId,
                variationId,
                filter
            )
            .then(
                () => {
                    this.personalizationsmarteditMessageHandler.sendSuccess(
                        this.translateService.instant('personalization.info.creatingaction')
                    );
                    this.actionCreated.emit(containerSourceId);
                },
                () => {
                    this.personalizationsmarteditMessageHandler.sendError(
                        this.translateService.instant('personalization.error.creatingaction')
                    );
                    this.actionCreated.emit();
                }
            );
    };

    catalogVersionFilterChange = (value: any): any => {
        if (!value) {
            return;
        }
        const arr = value.split('/');
        this.catalogFilter = arr[0];
        this.catalogVersionFilter = arr[1];
    };

    private init(): void {
        this.actions = [
            {
                id: 'create',
                name: this.translateService.instant(
                    'personalization.modal.addeditaction.createnewcomponent'
                )
            },
            {
                id: 'use',
                name: this.translateService.instant(
                    'personalization.modal.addeditaction.usecomponent'
                )
            }
        ];

        this.modalData.subscribe((config) => {
            this.colorIndicatorForElement = config.colorIndicatorForElement;
            this.slotId = config.slotId;
            this.actionId = config.actionId;
            this.componentUuid = config.componentUuid;
            this.defaultComponentId = config.componentId;
            this.editEnabled = config.editEnabled;
            this.slotCatalog = config.slotCatalog;
            this.componentCatalog = config.componentCatalog;
            this.selectedCustomizationCode = config.selectedCustomizationCode;
            this.selectedVariationCode = config.selectedVariationCode;
            this.componentType = config.componentType;

            this.personalizationsmarteditRestService
                .getCustomization({
                    code: this.selectedCustomizationCode
                })
                .then(
                    (response: any) => {
                        this.selectedCustomization = response;
                        this.selectedVariation = response.variations.filter(
                            (elem: any) => elem.code === this.selectedVariationCode
                        )[0];
                    },
                    () => {
                        // error callback
                        this.personalizationsmarteditMessageHandler.sendError(
                            this.translateService.instant(
                                'personalization.error.gettingcustomization'
                            )
                        );
                    }
                );

            if (this.editEnabled) {
                this.getAndSetComponentById(this.componentUuid);
                this.actionSelected = this.actions.filter((item) => item.id === 'use')[0].id;
            } else {
                this.actionSelected = '';
            }
        });

        this.initNewComponentTypes();
        this.getAndSetColorAndLetter();
    }
}
