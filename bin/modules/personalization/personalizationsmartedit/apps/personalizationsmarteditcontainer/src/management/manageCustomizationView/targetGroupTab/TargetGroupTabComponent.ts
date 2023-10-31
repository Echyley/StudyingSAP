/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
    Customization,
    CustomizationVariation,
    PersonalizationsmarteditDateUtils,
    PersonalizationsmarteditUtils,
    PERSONALIZATION_MODEL_STATUS_CODES,
    TargetGroupState,
    Trigger
} from 'personalizationcommons';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import {
    IConfirmationModalService,
    LogService,
    SliderPanelConfiguration,
    stringUtils
} from 'smarteditcommons';
import { CustomizationViewService } from '../CustomizationViewService';
import { TriggerService } from '../TriggerService';
import { ModalFullScreenButtonComponent } from './modalFullScreenButton';
import { CustomizationVariationListItem } from './targetGroupVariationList';
@Component({
    selector: 'perso-target-group-tab',
    templateUrl: './TargetGroupTabComponent.html'
})
export class TargetGroupTabComponent implements OnInit, OnDestroy {
    @ViewChild(ModalFullScreenButtonComponent, { static: false })
    modalFullScreenButtonCmp: ModalFullScreenButtonComponent;

    public readonly sliderPanelConfiguration: SliderPanelConfiguration;
    /** Show Edit Target Group modal. */
    public readonly sliderPanelShow: () => Promise<void>;
    /** Hide Edit Target Group modal. */
    public readonly sliderPanelHide: () => Promise<void>;

    public customization: Customization;
    /**
     * TODO: refactor to  RxJS
     * selectedVariation is also manipulated by SegmentViewComponent when Editing or Creating a Target Group.
     */
    public edit: TargetGroupState;
    /** Whether Variation details content can be displayed. */
    public isVariationLoaded: boolean;
    /** Whether Variation details is displayed in fullscreen view. */
    public isFullscreen: boolean;
    /** List of variations. */
    public visibleVariations: CustomizationVariation[];

    private editVariationSubscription: Subscription;
    private customizationViewStateSubscription: Subscription;

    constructor(
        public persoDateUtils: PersonalizationsmarteditDateUtils,
        private persoUtils: PersonalizationsmarteditUtils,
        private persoTriggerService: TriggerService,
        private confirmationModalService: IConfirmationModalService,
        private customizationViewService: CustomizationViewService,
        private logService: LogService,
        private cdr: ChangeDetectorRef
    ) {
        this.visibleVariations = [];
        this.isVariationLoaded = false;
        this.isFullscreen = false;

        this.sliderPanelConfiguration = {
            modal: {
                showDismissButton: true,
                title: 'personalization.modal.customizationvariationmanagement.targetgrouptab.slidingpanel.title',
                cancel: {
                    label: 'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelchanges',
                    onClick: (): void => {
                        this.cancelChangesClick();
                    },
                    isDisabledFn: (): boolean => false
                },
                dismiss: {
                    onClick: (): void => {
                        this.cancelChangesClick();
                    },
                    label: '',
                    isDisabledFn: (): boolean => false
                },
                save: {
                    onClick: (): void => {},
                    label: '',
                    isDisabledFn: (): boolean => false
                }
            },
            cssSelector: '#y-modal-dialog'
        };

        this.edit = {
            code: '',
            name: '',
            expression: [],
            isDefault: false,
            showExpression: true,
            selectedVariation: undefined
        };
    }

    ngOnInit(): void {
        this.customizationViewStateSubscription = this.customizationViewService
            .getState$()
            .subscribe(({ customization, visibleVariations }) => {
                this.customization = customization;
                this.visibleVariations = visibleVariations;

                this.cdr.detectChanges();
            });

        // 1. Edit Target Group when edit has been clicked on ManageCustomizationLibrary.
        // 2. Edit Target Group when edit has been clicked on TargetGroupVariationListComponent.
        this.editVariationSubscription = this.customizationViewService
            .editVariationAction$()
            .pipe(filter((variation) => !!variation))
            .subscribe((variation) => {
                // Delay to ensure this.sliderPanelShow has been set via 2 way-binding.
                // SliderPanelComponent for some reason uses timeout to emit the change which causes this.sliderPanelShow to be undefined on initialization.
                setTimeout(() => {
                    this.showEditVariationPanel(variation);
                }, 100);
            });
    }

    ngOnDestroy(): void {
        this.editVariationSubscription?.unsubscribe();
        this.customizationViewStateSubscription?.unsubscribe();
    }

    public isCustomizationEnabled(customization: Customization): boolean {
        return customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }

    public getActivityStateForCustomization(customization: Customization): string {
        return this.persoUtils.getActivityStateForCustomization(customization);
    }

    public toggleSliderFullscreen(enableFullscreen: boolean): void {
        this.modalFullScreenButtonCmp.toggle(enableFullscreen);
    }

    public onSegmentViewExpressionChange(expression: Trigger[]): void {
        this.edit.expression = expression;
    }

    /** Handles Move Up, Move Down called by MoveVariationUpItemComponent, MoveVariationDownItemComponent.  */
    public setVariationRank(
        variationListItem: CustomizationVariationListItem,
        increaseValue: number,
        $event: PointerEvent,
        firstOrLast: boolean
    ): void {
        if (firstOrLast) {
            $event.stopPropagation();
        } else {
            let fromIndex;
            if (variationListItem.isNew) {
                fromIndex = this.customization.variations.findIndex(
                    ({ tempcode }) => tempcode === variationListItem.tempcode
                );
            } else {
                fromIndex = this.customization.variations.findIndex(
                    ({ code }) => code === variationListItem.code
                );
            }

            const variation = this.customization.variations[fromIndex];
            const to = this.persoUtils.getValidRank(
                this.customization.variations,
                variation,
                increaseValue
            );

            const variations = [...this.customization.variations];
            if (to >= 0 && to < variations.length) {
                // swap by removing an element "from" its origin index and appending it before "to" index
                variations.splice(to, 0, variations.splice(fromIndex, 1)[0]);
                this.setVariations(variations);
            }
        }
    }

    /** Displays a confirm modal when "Applies to all users" is checked and some segments are selected. */
    public async showConfirmForDefaultTrigger(isDefault: boolean): Promise<void> {
        if (isDefault && this.persoTriggerService.isValidExpression(this.edit.expression[0])) {
            try {
                await this.confirmationModalService.confirm({
                    description:
                        'personalization.modal.manager.targetgrouptab.defaulttrigger.content'
                });
                this.edit.showExpression = false;
            } catch {
                // canceled
                this.edit.isDefault = false;
            }
        } else {
            this.edit.showExpression = !isDefault;
        }
    }

    public async removeVariation(variation: CustomizationVariation): Promise<void> {
        try {
            await this.confirmationModalService.confirm({
                description: 'personalization.modal.manager.targetgrouptab.deletevariation.content'
            });
            // when confirmed

            // variation was created temporarily. The state has been not yet sent to the API (save button was not clicked).
            if (variation.isNew) {
                // filter out removed variation
                const newVariations = this.customization.variations.filter(
                    (_variation) => _variation.tempcode !== variation.tempcode
                );
                this.setVariations(newVariations);
            } else {
                // variations with "DELETED" status will be not displayed.
                //
                // *Note These variations are also sent in payload to the API and they are returned. So they are not completely removed.
                // Does the API not support deletion?
                variation.status = PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
                this.updateVariation(variation);
            }
        } catch {
            // when canceled / dismissed
            this.logService.log('Remove Variation canceled');
        }
    }

    public updateVariation(variationToUpdate: CustomizationVariation): void {
        const newVariations = this.customization.variations.map((variation) =>
            (variationToUpdate.isNew && variation.tempcode === variationToUpdate.tempcode) ||
            (!variationToUpdate.isNew && variation.code === variationToUpdate.code)
                ? variationToUpdate
                : variation
        );
        this.setVariations(newVariations);
    }

    public showAddVariationPanel(): void {
        this.clearEditedVariationDetails();

        this.setSliderConfigForAdd();
        this.sliderPanelShow();

        this.edit.selectedVariation = { triggers: [] } as any;
        setTimeout(() => {
            this.isVariationLoaded = true;
        }, 0);
    }

    public showEditVariationPanel(variation: CustomizationVariation): void {
        this.setSliderConfigForEditing();

        // set model of selected variation to be edited in Edit Target Group
        this.edit.selectedVariation = variation;
        this.edit.code = variation.code;
        this.edit.name = variation.name;
        this.edit.isDefault = this.persoTriggerService.isDefault(variation.triggers);
        this.edit.showExpression = !this.edit.isDefault;

        this.isVariationLoaded = true;

        this.sliderPanelShow();
    }

    private setSliderConfigForAdd(): void {
        this.sliderPanelConfiguration.modal.save.label =
            'personalization.modal.customizationvariationmanagement.targetgrouptab.addvariation';
        this.sliderPanelConfiguration.modal.save.isDisabledFn = (): boolean =>
            !this.canSaveVariation();
        this.sliderPanelConfiguration.modal.save.onClick = (): void => {
            this.onAddVariation();
        };
    }

    private setSliderConfigForEditing(): void {
        this.sliderPanelConfiguration.modal.save.label =
            'personalization.modal.customizationvariationmanagement.targetgrouptab.savechanges';
        this.sliderPanelConfiguration.modal.save.isDisabledFn = (): boolean =>
            !this.canSaveVariation();
        this.sliderPanelConfiguration.modal.save.onClick = (): any => {
            this.onSaveEditedVariation();
        };
    }

    private canSaveVariation(): boolean {
        const isValidOrEmpty = this.isSavedVariationValidOrEmpty();

        const isTriggerValid = this.persoTriggerService.isValidExpression(this.edit.expression[0]);

        const isNameValid = !stringUtils.isBlank(this.edit.name);
        const canSaveVariation =
            isNameValid && (this.edit.isDefault || (isTriggerValid && isValidOrEmpty));

        return canSaveVariation;
    }

    private isSavedVariationValidOrEmpty(): boolean {
        this.edit.expression = this.edit.expression || ([{ nodes: [] }] as any);
        return (
            this.persoTriggerService.isValidExpression(this.edit.expression[0]) ||
            this.edit.expression.length === 0 ||
            this.edit.expression[0].nodes.length === 0
        );
    }

    private setVariations(variations: CustomizationVariation[]): void {
        this.customizationViewService.setVariations(variations);
    }

    private onAddVariation(): void {
        const random = 10000;
        this.customizationViewService.addVariation({
            tempcode: 'Temp' + (Math.random() * random).toFixed(0),
            code: this.edit.code,
            name: this.edit.name,
            enabled: true,
            status: PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
            triggers: this.persoTriggerService.buildTriggers(
                this.edit,
                this.edit.selectedVariation.triggers || []
            ),
            rank: this.customization.variations.length,
            isNew: true,
            catalog: null,
            catalogVersion: null
        });

        this.clearEditedVariationDetails();
        this.toggleSliderFullscreen(false);

        this.sliderPanelHide();

        this.isVariationLoaded = false;
    }

    private onSaveEditedVariation(): void {
        const triggers = this.persoTriggerService.buildTriggers(
            this.edit,
            this.edit.selectedVariation.triggers || []
        );

        const newVariations = this.customization.variations.map((variation) =>
            variation.code === this.edit.selectedVariation.code
                ? {
                      ...variation,
                      triggers,
                      name: this.edit.name
                  }
                : variation
        );
        this.setVariations(newVariations);

        this.toggleSliderFullscreen(false);

        this.sliderPanelHide();

        this.isVariationLoaded = false;
    }

    private cancelChangesClick(): void {
        if (this.isVariationSelected()) {
            this.edit.selectedVariation = undefined;
        } else {
            this.clearEditedVariationDetails();
        }
        this.toggleSliderFullscreen(false);
        this.sliderPanelHide();
        this.isVariationLoaded = false;
    }

    private isVariationSelected(): boolean {
        return typeof this.edit.selectedVariation !== 'undefined';
    }

    private clearEditedVariationDetails(): void {
        this.edit.code = '';
        this.edit.name = '';
        this.edit.expression = [];
        this.edit.isDefault = false;
        this.edit.showExpression = true;
    }
}
