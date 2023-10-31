/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DOCUMENT } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { cloneDeep, isEqual } from 'lodash';
import {
    PersonalizationsmarteditDateUtils,
    PERSONALIZATION_MODEL_STATUS_CODES,
    Customization,
    PersonalizationsmarteditMessageHandler,
    CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS,
    PersonalizationsmarteditCommerceCustomizationService,
    CustomizationVariation,
    CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS,
    PERSONALIZATION_DATE_FORMATS
} from 'personalizationcommons';
import { from, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import {
    ModalButtonAction,
    ModalButtonStyle,
    ModalManagerService,
    IConfirmationModalService,
    ModalButtonOptions,
    LogService,
    SystemEventService,
    Tab,
    TabsComponent
} from 'smarteditcommons';
import { PersonalizationsmarteditRestService } from '../../service';
import { BasicInfoTabComponent } from './basicInfoTab';
import { CustomizationViewService, defaultCustomization } from './CustomizationViewService';
import { TargetGroupTabComponent } from './targetGroupTab';
import { ManageCustomizationViewModalData } from './types';

type ModalButtonKey = 'basicInfoTabComponent' | 'targetGroupTabComponent';
type TabId = keyof typeof CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS;

/** Component responsible for creation or edition of Customization.  */
@Component({
    selector: 'perso-manage-customization-view',
    templateUrl: './ManageCustomizationViewComponent.html',
    providers: [CustomizationViewService]
})
export class ManageCustomizationViewComponent implements OnInit {
    @ViewChild(TabsComponent, { static: false }) tabsCmp: TabsComponent<unknown>;

    public tabs: Tab[];
    public customizationCode: ManageCustomizationViewModalData['customizationCode'] | undefined;
    public variationCode: ManageCustomizationViewModalData['variationCode'] | undefined;
    /** Used to check if modal is dirty */
    public initialCustomization: Customization;
    /** Whether edit or creation mode. */
    public isEditMode: boolean;
    public isReady = false;
    private savingCustomizationView = false;

    private modalButtons: { [key in ModalButtonKey]: ModalButtonOptions[] };

    constructor(
        private persoDateUtils: PersonalizationsmarteditDateUtils,
        private persoCommerceCustomizationService: PersonalizationsmarteditCommerceCustomizationService,
        private persoMessageHandler: PersonalizationsmarteditMessageHandler,
        private persoCustomizationViewService: CustomizationViewService,
        private persoRestService: PersonalizationsmarteditRestService,
        private confirmationModalService: IConfirmationModalService,
        private systemEventService: SystemEventService,
        private modalManager: ModalManagerService<ManageCustomizationViewModalData | undefined>,
        private translateService: TranslateService,
        private logService: LogService,
        private cdr: ChangeDetectorRef,
        private renderer: Renderer2,
        @Inject(DOCUMENT) private document: Document
    ) {
        this.tabs = [];
        this.isReady = false;
        this.isEditMode = false;

        this.tabs = [
            {
                id: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_FORM_NAME,
                title: 'personalization.modal.customizationvariationmanagement.basicinformationtab',
                component: BasicInfoTabComponent,
                hasErrors: false,
                active: true
            },
            {
                id: CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME,
                title: 'personalization.modal.customizationvariationmanagement.targetgrouptab',
                component: TargetGroupTabComponent,
                hasErrors: false,
                active: false
            }
        ];
    }

    async ngOnInit(): Promise<void> {
        const modalData = await this.modalManager.getModalData().pipe(take(1)).toPromise();
        // undefined when creating a new Customization
        if (modalData) {
            ({ customizationCode: this.customizationCode, variationCode: this.variationCode } =
                modalData);
        }
        this.initModalButtons();

        await this.initCustomization();

        // edition
        if (this.customizationCode) {
            this.isEditMode = true;

            if (this.variationCode) {
                this.persoCustomizationViewService.editVariationAction(
                    this.persoCustomizationViewService.selectVariationByCode(this.variationCode)
                );
            }
        }

        // variation edition
        if (this.variationCode) {
            this.tabs[0].active = false;
            this.tabs[1].active = true;

            this.modalManager.addButtons(this.modalButtons.targetGroupTabComponent);
        } else {
            this.modalManager.addButtons(this.modalButtons.basicInfoTabComponent);
        }

        this.isReady = true;

        this.cdr.detectChanges();
    }

    public onTabSelected(tabId: TabId): void {
        this.modalManager.removeAllButtons();
        if (tabId === CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.BASIC_INFO_TAB_FORM_NAME) {
            this.modalManager.addButtons([...this.modalButtons.basicInfoTabComponent]);
        } else if (
            tabId === CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS.TARGET_GROUP_TAB_NAME
        ) {
            this.modalManager.addButtons([...this.modalButtons.targetGroupTabComponent]);
        }
    }

    private initModalButtons(): void {
        const modalButtonCancel = {
            id: 'cancel',
            label: 'se.cms.component.confirmation.modal.cancel',
            style: ModalButtonStyle.Default,
            action: ModalButtonAction.None,
            callback: (): Observable<void> => from(this.onCancel())
        };
        this.modalButtons = {
            basicInfoTabComponent: [
                {
                    id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_NEXT,
                    label: 'personalization.modal.customizationvariationmanagement.basicinformationtab.button.next',
                    style: ModalButtonStyle.Primary,
                    action: ModalButtonAction.None,
                    callback: (): Observable<void> => from(this.selectTargetGroupTab()),
                    disabledFn: (): boolean => {
                        const isBasicInfoTabValid = this.isBasicInfoTabValid();

                        this.fixDisabledTabStyles(isBasicInfoTabValid);

                        return !isBasicInfoTabValid;
                    }
                },
                modalButtonCancel
            ],
            targetGroupTabComponent: [
                {
                    id: CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS.CONFIRM_OK,
                    label: 'personalization.modal.customizationvariationmanagement.targetgrouptab.button.submit',
                    style: ModalButtonStyle.Primary,
                    action: ModalButtonAction.None,
                    callback: (): Observable<void> => from(this.save()),
                    disabledFn: (): boolean => {
                        const visibleVariations =
                            this.persoCustomizationViewService.selectVisibleVariations();
                        return visibleVariations.length === 0 || this.savingCustomizationView;
                    }
                },
                modalButtonCancel
            ]
        };
    }

    private selectTargetGroupTab(): Promise<void> {
        return new Promise(() => {
            this.tabsCmp.selectTab(this.tabs[1]);
        });
    }

    /**
     * Disable TargetGroupTabComponent when Basic Info Tab is invalid
     * TODO: Consider extending TabsComponent in SmartEdit core to avoid such workarounds.
     */
    private fixDisabledTabStyles(isTabValid: boolean): void {
        const tab = this.document.querySelector(`[tab-id="targetgrptab"]`);
        if (!tab) {
            return;
        }

        if (isTabValid) {
            this.renderer.setStyle(tab, 'pointer-events', 'unset');
            this.renderer.setStyle(tab, 'opacity', 'unset');
        } else {
            this.renderer.setStyle(tab, 'pointer-events', 'none');
            this.renderer.setStyle(tab, 'opacity', 0.4);
        }
    }

    private isBasicInfoTabValid(): boolean {
        const { name } = this.persoCustomizationViewService.selectCustomization();
        return !!name;
    }

    private async initCustomization(): Promise<void> {
        if (!this.customizationCode) {
            this.initialCustomization = cloneDeep(defaultCustomization);
            return;
        }

        const customization = await this.getCustomization();
        if (customization) {
            // set the state
            this.persoCustomizationViewService.setCustomization(customization);
            this.initialCustomization = cloneDeep(customization);
        }
    }

    private async fetchCustomizationAndSetDates(): Promise<Customization | undefined> {
        try {
            const customization = await this.persoRestService.getCustomization({
                code: this.customizationCode
            });

            customization.enabledStartDate = this.persoDateUtils.formatDate(
                customization.enabledStartDate,
                undefined
            );
            customization.enabledEndDate = this.persoDateUtils.formatDate(
                customization.enabledEndDate,
                undefined
            );
            customization.statusBoolean =
                customization.status === PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;

            return customization;
        } catch {
            this.persoMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingsegments')
            );
        }
    }

    private async getCustomization(): Promise<Customization | undefined> {
        const customization = await this.fetchCustomizationAndSetDates();
        if (!customization) {
            return;
        }

        const variations = await this.fetchVariations(this.customizationCode);
        if (!variations) {
            return;
        }
        customization.variations = variations;

        return customization;
    }

    private async fetchVariations(
        customizationCode: string
    ): Promise<CustomizationVariation[] | undefined> {
        try {
            const filter = {
                includeFullFields: true
            };
            const response = await this.persoRestService.getVariationsForCustomization(
                customizationCode,
                filter
            );
            // add commerce customizations to each variation
            response.variations.forEach((variation) => {
                variation.commerceCustomizations =
                    this.persoCommerceCustomizationService.getCommerceActionsCountMap(variation);
                variation.numberOfCommerceActions =
                    this.persoCommerceCustomizationService.getCommerceActionsCount(variation);
                delete variation.actions; // no more use for this property and it existence may be harmful
            });

            return response.variations;
        } catch {
            this.persoMessageHandler.sendError(
                this.translateService.instant('personalization.error.gettingcomponents')
            );
        }
    }

    private async onCancel(): Promise<void> {
        if (!this.isModalDirty()) {
            this.modalManager.dismiss();
            return;
        }
        try {
            await this.confirmationModalService.confirm({
                description:
                    'personalization.modal.customizationvariationmanagement.targetgrouptab.cancelconfirmation'
            });
            // confirmed
            this.modalManager.dismiss();
        } catch {
            this.logService.log('Confirmation Modal cancelled');
        }
    }

    private isModalDirty(): boolean {
        const customization = this.persoCustomizationViewService.selectCustomization();
        return !isEqual(this.initialCustomization, customization);
    }

    private async save(): Promise<void> {
        this.savingCustomizationView = true;
        const customization = { ...this.persoCustomizationViewService.selectCustomization() };

        const payload = this.prepareCustomizationPayload(customization, this.isEditMode);
        if (this.isEditMode) {
            try {
                await this.persoRestService.updateCustomizationPackage(payload);

                this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
                this.persoMessageHandler.sendSuccess(
                    this.translateService.instant('personalization.info.updatingcustomization')
                );

                this.modalManager.dismiss();
            } catch {
                this.savingCustomizationView = false;
                this.persoMessageHandler.sendError(
                    this.translateService.instant('personalization.error.updatingcustomization')
                );
            }
        } else {
            try {
                await this.persoRestService.createCustomization(payload);

                this.systemEventService.publishAsync('CUSTOMIZATIONS_MODIFIED', {});
                this.persoMessageHandler.sendSuccess(
                    this.translateService.instant('personalization.info.creatingcustomization')
                );

                this.modalManager.dismiss();
            } catch {
                this.savingCustomizationView = false;
                this.persoMessageHandler.sendError(
                    this.translateService.instant('personalization.error.creatingcustomization')
                );
            }
        }
    }

    private prepareCustomizationPayload(
        customization: Customization,
        isEditMode: boolean
    ): Customization {
        customization.enabledStartDate = customization.enabledStartDate
            ? this.persoDateUtils.formatDate(
                  customization.enabledStartDate,
                  PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT
              )
            : undefined;

        customization.enabledEndDate = customization.enabledEndDate
            ? this.persoDateUtils.formatDate(
                  customization.enabledEndDate,
                  PERSONALIZATION_DATE_FORMATS.MODEL_DATE_FORMAT
              )
            : undefined;

        if (!isEditMode) {
            delete customization.catalog;
            delete customization.code;
            delete customization.rank;
        }
        return customization;
    }
}
