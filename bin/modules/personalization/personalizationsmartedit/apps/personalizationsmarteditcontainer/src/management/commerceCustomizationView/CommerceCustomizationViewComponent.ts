/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    Optional,
    Inject,
    Type,
    Injector,
    ViewEncapsulation
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    SeData,
    Customization,
    CustomizationVariation,
    PersonalizationsmarteditUtils,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditCommerceCustomizationService,
    PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES,
    PersonlizationSearchExtensionComponent,
    PersonlizationSearchExtensionInjector,
    PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN,
    PERSONLIZATION_SEARCH_EXTENSION_TOKEN,
    PersonlizationPromotionExtensionComponent,
    PersonlizationPromotionExtensionInjector,
    PERSONLIZATION_PROMOTION_EXTENSION_TOKEN,
    PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN
} from 'personalizationcommons';
import { Action, BaseAction } from 'personalizationsmarteditcontainer/interfaces';
import { from as fromPromise, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import {
    SystemEventService,
    LogService,
    promiseUtils,
    ModalManagerService,
    ModalButtonAction,
    ModalButtonStyle
} from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../../service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from '../../service/PersonalizationsmarteditRestService';
import { ActionsDataFactory } from './ActionsDataFactory';

export interface CommerceCustomizationViewData {
    customization: Customization;
    variation: CustomizationVariation;
}

interface SearchProfileComponent {
    component: Type<any>;
    injector: Injector;
}

interface PromotionComponent {
    component: Type<any>;
    injector: Injector;
}
@Component({
    selector: 'commerce-customization-view',
    templateUrl: './CommerceCustomizationViewComponent.html',
    encapsulation: ViewEncapsulation.None
})
export class CommerceCustomizationViewComponent implements OnInit {
    public promotionCode: string;
    public searchProfilesCode: string;
    public customization: Customization;
    public variation: CustomizationVariation;
    public actions: Action[];

    public availableTypes: any[] = [];
    public select: any = {};
    public customizationStatusText: string;
    public variationStatusText: string;
    public customizationStatus: string;
    public variationStatus: string;
    public removedActions: Action[];

    public customizationTypeFetchStrategy = {
        fetchAll: (): Promise<any[]> => {
            this.availableTypes = [];
            this.availableTypes =
                this.personalizationsmarteditCommerceCustomizationService.getAvailableTypes(
                    this.personalizationsmarteditContextService.getSeData().seConfigurationData
                );
            this.availableTypes.forEach((type) => {
                type.id = type.type;
                type.label = type.text;
            });
            return Promise.resolve(this.availableTypes);
        }
    };

    public searchProfileComponent: SearchProfileComponent | undefined;
    public promotionComponent: PromotionComponent | undefined;

    constructor(
        private injector: Injector,
        @Optional()
        @Inject(PERSONLIZATION_SEARCH_EXTENSION_TOKEN)
        personlizationSearchExtensionComponent: PersonlizationSearchExtensionComponent,
        @Inject(PERSONLIZATION_PROMOTION_EXTENSION_TOKEN)
        personlizationPromotionExtensionComponent: PersonlizationPromotionExtensionComponent,
        private translateService: TranslateService,
        private actionsDataFactory: ActionsDataFactory,
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        private systemEventService: SystemEventService,
        private personalizationsmarteditCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService,
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        private modalManager: ModalManagerService,
        private logService: LogService
    ) {
        this.availableTypes =
            this.personalizationsmarteditCommerceCustomizationService.getAvailableTypes(
                this.personalizationsmarteditContextService.getSeData().seConfigurationData
            );
        this.availableTypes.forEach((type) => {
            type.id = type.type;
            type.label = type.text;
        });
        this.select = {
            typeId: this.availableTypes[0].id,
            template: this.availableTypes[0].template
        };

        this.promotionCode = 'cxPromotionActionData';
        this.searchProfilesCode = 'cxSearchProfileActionData';

        this.actionsDataFactory.resetActions();
        this.actionsDataFactory.resetRemovedActions();
        this.actions = this.actionsDataFactory.getActions();
        this.removedActions = this.actionsDataFactory.getRemovedActions();
        this.onActionTypeChange = this.onActionTypeChange.bind(this);

        if (personlizationSearchExtensionComponent) {
            this.initPersonlizationSearchExtensionComponent(personlizationSearchExtensionComponent);
        }

        if (personlizationPromotionExtensionComponent) {
            this.initPersonlizationPromotionExtensionComponent(
                personlizationPromotionExtensionComponent
            );
        }
    }

    ngOnInit(): void {
        this.modalManager
            .getModalData()
            .pipe(take(1))
            .subscribe((data: CommerceCustomizationViewData) => {
                this.customization = data.customization;
                this.variation = data.variation;
            });

        this.customizationStatusText =
            this.personalizationsmarteditUtils.getEnablementTextForCustomization(
                this.customization,
                'personalization.modal.commercecustomization'
            );
        this.variationStatusText = this.personalizationsmarteditUtils.getEnablementTextForVariation(
            this.variation,
            'personalization.modal.commercecustomization'
        );
        this.customizationStatus =
            this.personalizationsmarteditUtils.getActivityStateForCustomization(this.customization);
        this.variationStatus = this.personalizationsmarteditUtils.getActivityStateForVariation(
            this.customization,
            this.variation
        );

        this.modalManager.addButtons([
            {
                id: 'confirmOk',
                label: 'personalization.modal.commercecustomization.button.submit',
                style: ModalButtonStyle.Primary,
                action: ModalButtonAction.Close,
                callback: (): Observable<void> => fromPromise(this.onSave()),
                disabledFn: (): boolean => !this.isDirty()
            },
            {
                id: 'confirmCancel',
                label: 'personalization.modal.commercecustomization.button.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Close,
                callback: (): Observable<void> => fromPromise(this.onCancel())
            }
        ]);

        this.populateActions();
    }

    public onActionTypeChange(): void {
        this.availableTypes.forEach((type) => {
            if (type.id === this.select.typeId) {
                this.select.template = type.template;
            }
        });
    }

    public getSeData(): SeData {
        return this.personalizationsmarteditContextService.getSeData();
    }

    public isItemInSelectedActions = (action: BaseAction, comparer: (p1, p2) => boolean): Action =>
        this.actionsDataFactory.isItemInSelectedActions(action, comparer);

    public removeSelectedAction(actionWrapper: any): void {
        const index = this.actions.indexOf(actionWrapper);
        if (index < 0) {
            return;
        }
        const removed = this.actions.splice(index, 1);
        // only old item should be added to delete queue
        // new items are just deleted
        if (
            removed[0].status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD ||
            removed[0].status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE
        ) {
            removed[0].status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE;
            this.removedActions.push(removed[0]);
        }
    }

    // This function requires two parameters
    // action to be added
    // and comparer = function(action,action) for defining if two actions are identical
    // comparer is used
    public addAction = (action: BaseAction, comparer: (p1, p2) => boolean): void => {
        this.actionsDataFactory.addAction(action, comparer);
    };

    public displayAction(actionWrapper: any): string {
        const action = actionWrapper.action;
        const type = this.getType(action.type);
        if (type.getName) {
            return type.getName(action);
        } else {
            return action.code;
        }
    }

    public getActionsToDisplay(): Action[] {
        return this.actionsDataFactory.getActions();
    }

    public isDirty(): boolean {
        let dirty = false;
        // dirty if at least one new
        this.actions.forEach((wrapper: any) => {
            dirty =
                dirty ||
                wrapper.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW ||
                wrapper.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
        });
        // or one deleted
        dirty = dirty || this.removedActions.length > 0;
        return dirty;
    }

    private getSelectedTypeCode(): string {
        return this.select.typeId;
    }

    private getCustomization(): Customization {
        return this.customization;
    }

    private getVariation(): CustomizationVariation {
        return this.variation;
    }

    private initPersonlizationSearchExtensionComponent({
        component
    }: PersonlizationSearchExtensionComponent): void {
        this.searchProfileComponent = {
            component,
            injector: Injector.create({
                parent: this.injector,
                providers: [
                    {
                        provide: PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN,
                        useValue: {
                            seData: this.getSeData(),
                            actions: this.actions,
                            addAction: (action: any, comparer: (p1, p2) => boolean): void =>
                                this.addAction(action, comparer),
                            isActionInSelectDisabled: (
                                action: any,
                                comparer: (p1, p2) => boolean
                            ): any => this.isItemInSelectedActions(action, comparer),
                            getSelectedTypeCode: (): string => this.getSelectedTypeCode(),
                            getCustomization: (): Customization => this.getCustomization(),
                            getVariation: (): CustomizationVariation => this.getVariation()
                        } as PersonlizationSearchExtensionInjector
                    }
                ]
            })
        };
    }

    private initPersonlizationPromotionExtensionComponent({
        component
    }: PersonlizationPromotionExtensionComponent): void {
        this.promotionComponent = {
            component,
            injector: Injector.create({
                parent: this.injector,
                providers: [
                    {
                        provide: PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN,
                        useValue: {
                            addAction: (action: any, comparer: (p1, p2) => boolean): void =>
                                this.addAction(action, comparer),
                            isActionInSelectDisabled: (
                                action: any,
                                comparer: (p1, p2) => boolean
                            ): any => this.isItemInSelectedActions(action, comparer),
                            getSelectedTypeCode: (): string => this.getSelectedTypeCode()
                        } as PersonlizationPromotionExtensionInjector
                    }
                ]
            })
        };
    }

    private async populateActions(): Promise<void> {
        try {
            const response = await this.personalizationsmarteditRestService.getActions(
                this.customization.code,
                this.variation.code,
                {}
            );
            const actions = response.actions
                .filter((elem: any) => elem.type !== 'cxCmsActionData')
                .map((item: any) => ({
                    code: item.code,
                    action: item,
                    status: PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD
                }));
            this.actionsDataFactory.resetActions();
            this.personalizationsmarteditUtils.uniqueArray(
                this.actionsDataFactory.actions,
                actions || []
            );
        } catch (e) {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingactions')
            );
        }
    }

    private getType(type: any): any {
        for (const item of this.availableTypes) {
            if (item.type === type) {
                return item;
            }
        }
        return {};
    }

    private sendRefreshEvent(): void {
        this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
    }

    private onCancel(): Promise<void> {
        this.modalManager.close(null);
        return Promise.resolve();
    }

    private onSave(): Promise<void> {
        const createData = {
            actions: this.actions
                .filter(
                    (item) =>
                        item.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW
                )
                .map((item) => item.action)
        };
        const deleteData = this.removedActions
            .filter(
                (item) =>
                    item.status === PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.DELETE
            )
            .map((item) => item.action.code);
        const updateData = {
            actions: this.actions
                .filter(
                    (item) =>
                        item.status ===
                        PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE
                )
                .map((item) => item.action)
        };
        const shouldCreate = createData.actions.length > 0;
        const shouldDelete = deleteData.length > 0;
        const shouldUpdate = updateData.actions.length > 0;

        ((): any => {
            if (shouldCreate) {
                return this.createActions(this.customization.code, this.variation.code, createData);
            } else {
                return Promise.resolve();
            }
        })().then((respCreate: any) => {
            ((): any => {
                if (shouldDelete) {
                    return this.deleteActions(
                        this.customization.code,
                        this.variation.code,
                        deleteData
                    );
                } else {
                    return Promise.resolve();
                }
            })().then((respDelete: any) => {
                if (shouldUpdate) {
                    this.updateActions(
                        this.customization.code,
                        this.variation.code,
                        updateData,
                        respCreate,
                        respDelete
                    );
                } else {
                    return Promise.resolve();
                }
            });
        });

        this.modalManager.close(null);
        return Promise.resolve();
    }

    // customization and variation status helper functions
    private getActionTypesForActions(actions: BaseAction[]): any {
        return actions
            .map((action: BaseAction) => action.type)
            .filter(
                (item: any, index: any, arr: any) =>
                    // removes duplicates from mapped array
                    arr.indexOf(item) === index
            )
            .map(
                (typeCode: any) =>
                    this.availableTypes.filter(
                        (availableType: any) => availableType.type === typeCode
                    )[0]
            );
    }

    private async createActions(
        customizationCode: any,
        variationCode: any,
        createData: any
    ): Promise<any> {
        const deferred = promiseUtils.defer();
        try {
            const response = await this.personalizationsmarteditRestService.createActions(
                customizationCode,
                variationCode,
                createData,
                {}
            );
            this.personalizationsmarteditMessageHandler.sendSuccess(
                this.translateService.instant('personalization.info.creatingaction')
            );
            this.sendRefreshEvent();
            deferred.resolve(response);
        } catch (e) {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.creatingaction')
            );
            deferred.reject();
        }
        return deferred.promise;
    }

    private async deleteActions(
        customizationCode: string,
        variationCode: string,
        deleteData: any
    ): Promise<any> {
        const deferred = promiseUtils.defer();
        try {
            const response = await this.personalizationsmarteditRestService.deleteActions(
                customizationCode,
                variationCode,
                deleteData,
                {}
            );
            this.personalizationsmarteditMessageHandler.sendSuccess(
                this.translateService.instant('personalization.info.removingaction')
            );
            this.sendRefreshEvent();
            deferred.resolve(response);
        } catch (e) {
            this.personalizationsmarteditMessageHandler.sendError(
                this.translateService.instant('personalization.error.removingaction')
            );
            deferred.resolve();
        }
        return deferred.promise;
    }

    private updateActions(
        customizationCode: string,
        variationCode: string,
        updateData: any,
        respCreate: any,
        respDelete: any
    ): void {
        const updateTypes = this.getActionTypesForActions(updateData.actions);
        updateTypes.forEach((type: any) => {
            if (type.updateActions) {
                const actionsForType = updateData.actions.filter(
                    (a: any) => this.getType(a.type) === type
                );
                type.updateActions(
                    customizationCode,
                    variationCode,
                    actionsForType,
                    respCreate,
                    respDelete
                ).then(
                    () => {
                        this.personalizationsmarteditMessageHandler.sendSuccess(
                            this.translateService.instant('personalization.info.updatingactions')
                        );
                        this.sendRefreshEvent();
                    },
                    () => {
                        this.personalizationsmarteditMessageHandler.sendError(
                            this.translateService.instant('personalization.error.updatingactions')
                        );
                    }
                );
            } else {
                this.logService.debug(
                    this.translateService.instant('personalization.error.noupdatingactions')
                );
            }
        });
    }
}
