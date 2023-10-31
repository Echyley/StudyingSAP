/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    Type,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { IContextAwareEditableItemService } from 'cmscommons';
import { clone, includes, cloneDeep, isEqual } from 'lodash';
import {
    FetchStrategy,
    GenericEditorFieldMessage,
    GENERIC_EDITOR_LOADED_EVENT,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    IUriContext,
    LogService,
    SliderPanelConfiguration,
    stringUtils,
    SystemEventService,
    CmsitemsRestService,
    CMSItem
} from 'smarteditcommons';
import { IRestrictionType } from '../../../../dao/RestrictionTypesRestService';
import {
    IPageRestrictionCriteria,
    PageRestrictionsCriteriaService
} from '../../../../services/pageRestrictions/PageRestrictionsCriteriaService';
import { RestrictionsDTO } from '../../../pages/pageWizard';
import {
    EditingRestrictionConfig,
    RestrictionPickerConfigService,
    SelectingRestrictionConfig
} from '../../services';
import { RestrictionCMSItem } from '../../types';
import { RestrictionEditorCriteriaSelectItemComponent } from './restrictionEditorCriteriaSelectItem/RestrictionEditorCriteriaSelectItemComponent';

const ITEM_MANAGEMENT_EDITOR_ID = 'se-item-management-editor';

@Component({
    selector: 'se-restrictions-editor',
    templateUrl: './RestrictionsEditorComponent.html',
    styleUrls: ['./RestrictionsEditorComponent.scss']
})
export class RestrictionsEditorComponent implements OnInit, OnDestroy, OnChanges {
    /**
     * item - The object for the item you want to manage restrictions.
     * item.onlyOneRestrictionMustApply - The restriction criteria for the item.
     * item.uuid - The uuid of the item. Required if not passing initial restrictions. Used to fetch and update restrictions for the item.
     * item.uriContext - the IUriContext
     */
    @Input() item: CMSItem;

    /**
     * Boolean to determine whether the editor is enabled
     */
    @Input() editable: boolean;

    /**
     * Function that returns true. This function is defined in the restrictionsEditor component and exists only to provide an external callback.
     */
    @Input() resetFn: () => boolean;

    /**
     * Function that returns a promise. This function is defined in the restrictionsEditor component and exists only to provide an external callback.
     */
    @Input() cancelFn: () => Promise<void>;

    /**
     * Function that returns a boolean. This function is defined in the restrictionsEditor component and exists only to provide an external callback.
     */
    @Input() isDirtyFn: () => boolean;

    /**
     * Function that return list of restriction types. The invoker can bind this to a custom function to fetch a list of restriction types.
     */
    @Input() getRestrictionTypes: () => Promise<IRestrictionType[]>;

    /**
     * Function that returns an array of supported restriction types.
     * The invoker can bind this to a custom function to fetch these values and perform other operations.
     * If not provide, all types are assumed to be supported.
     */
    @Input() getSupportedRestrictionTypes: () => Promise<string[]>;

    /**
     * Function that removes validation messages of Generic Editor.
     */
    @Input() removeValidationMessages: () => void;

    /**
     * An array of initial restriction objects to be loaded in the restrictions editor. If restrictions is not provided, it is used else it is assumed that there are no restrictions
     */
    @Input() restrictionUuids: string[];

    /**
     * EventEmitter that emits 'onlyOneRestrictionMustApply' boolean and an array of 'restrictions' as object argument.
     */

    @Output() onRestrictionsChange: EventEmitter<RestrictionsDTO>;
    @Output() resetFnChange: EventEmitter<() => boolean>;
    @Output() cancelFnChange: EventEmitter<() => Promise<boolean>>;
    @Output() isDirtyFnChange: EventEmitter<() => boolean>;

    public restrictions: RestrictionCMSItem[];
    public originalRestrictions: RestrictionCMSItem[];
    public isRestrictionsReady: boolean;
    public showRestrictionPicker: boolean;
    public disableAddButton: boolean;

    public sliderPanelConfiguration: SliderPanelConfiguration;
    public restrictionManagement: {
        config: EditingRestrictionConfig | SelectingRestrictionConfig;
        uriContext: IUriContext;
        submitFn: () => Promise<RestrictionCMSItem>;
        isDirtyFn: () => boolean;
    };

    public criteriaOptions: IPageRestrictionCriteria[];
    public criteria: IPageRestrictionCriteria;
    public originalCriteria: IPageRestrictionCriteria;
    public matchCriteriaIsDirty: boolean;
    public criteriaFetchStrategy: FetchStrategy;
    public itemComponent: Type<RestrictionEditorCriteriaSelectItemComponent>;

    public errors: GenericEditorFieldMessage[];

    public sliderPanelHide: () => Promise<void>;
    public sliderPanelShow: () => Promise<void>;
    public isSliderVisible: boolean;

    private restrictionsArrayIsDirty: boolean;

    private unregisterErrorListener: () => void;
    private unregisterErrorPropagationEvent: () => void;

    constructor(
        private readonly logService: LogService,
        private readonly systemEventService: SystemEventService,
        private readonly restrictionPickerConfigService: RestrictionPickerConfigService,
        private readonly pageRestrictionsCriteriaService: PageRestrictionsCriteriaService,
        private readonly contextAwareEditableItemService: IContextAwareEditableItemService,
        private readonly cmsitemsRestService: CmsitemsRestService,
        private readonly cdr: ChangeDetectorRef
    ) {
        this.cancelFnChange = new EventEmitter();
        this.isDirtyFnChange = new EventEmitter();
        this.onRestrictionsChange = new EventEmitter();
        this.resetFnChange = new EventEmitter();

        this.errors = [];
        this.restrictions = [];
        this.originalRestrictions = [];
        this.restrictionUuids = [];
        this.isRestrictionsReady = false;
        this.restrictionsArrayIsDirty = false;
        this.showRestrictionPicker = false;
        this.disableAddButton = false;

        this.criteriaOptions = [];
        this.criteria = {} as IPageRestrictionCriteria;
        this.itemComponent = RestrictionEditorCriteriaSelectItemComponent;
        this.matchCriteriaIsDirty = false;
    }

    async ngOnInit(): Promise<void> {
        this.restrictionUuids = this.restrictionUuids || [];
        const restrictionsData = await this.cmsitemsRestService.getByIdsNoCache<RestrictionCMSItem>(
            this.restrictionUuids,
            'FULL'
        );
        const restrictionUuidsLength = 2;
        this.restrictions = (
            restrictionsData.response ? restrictionsData.response : [restrictionsData]
        ) as RestrictionCMSItem[];

        // setting restrictions criteria
        this.criteriaFetchStrategy = {
            fetchAll: (): Promise<IPageRestrictionCriteria[]> =>
                Promise.resolve(this.criteriaOptions)
        };
        this.prepareRestrictionsCriteria();

        // setting restrictions
        await this.setupResults();

        this.sliderPanelConfiguration = {
            modal: {
                showDismissButton: true,
                title: '',
                cancel: {
                    label: 'se.cms.restriction.management.panel.button.cancel',
                    onClick: (): void => {
                        this.sliderPanelHide();
                    },
                    isDisabledFn: (): boolean => false
                },
                save: {
                    onClick: (): void => {
                        //  do nothing
                    },
                    label: '',
                    isDisabledFn: (): boolean => false
                }
            },
            cssSelector: '#y-modal-dialog'
        };
        this.restrictionManagement = {
            uriContext: this.item.uriContext as IUriContext,
            submitFn: null,
            isDirtyFn: null,
            config: null
        };

        // It is necessary to put this function inside ngOnInit. Otherwise, the editor is never marked as dirty.
        this.isDirtyFnChange.emit(
            () =>
                this.restrictionsArrayIsDirty ||
                (this.matchCriteriaIsDirty &&
                    this.restrictionUuids.length >= restrictionUuidsLength)
        );

        this.resetFnChange.emit(() => true);

        this.cancelFnChange.emit(() => Promise.resolve(true));

        this.initEvents();
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        this.clearEvents();
    }

    async ngOnChanges(changes: SimpleChanges): Promise<void> {
        if (!changes.restrictionUuids) {
            return Promise.resolve();
        }

        this.restrictionsArrayIsDirty =
            !changes.restrictionUuids.firstChange &&
            JSON.stringify(this.originalRestrictions) !== JSON.stringify(this.restrictions);

        const haveRestrictionsChanged = !isEqual(
            changes.restrictionUuids.previousValue,
            changes.restrictionUuids.currentValue
        );

        if (haveRestrictionsChanged) {
            this.indexRestrictions();
        }
        return this.applyIsRestrictionEditable();
    }

    public onAddRestriction(): void {
        this.setSliderConfigForAddOrCreate();
        this.restrictionManagement.config =
            this.restrictionPickerConfigService.getConfigForSelecting(
                [...this.restrictions],
                this.getRestrictionTypes,
                this.getSupportedRestrictionTypes
            );
        this.sliderPanelShow();
    }

    public onEditRestriction(restriction: RestrictionCMSItem): void {
        this.setSliderConfigForEditing();
        this.restrictionManagement.config = this.restrictionPickerConfigService.getConfigForEditing(
            { ...restriction },
            this.getSupportedRestrictionTypes
        );
        this.sliderPanelShow();
    }

    public onRemoveRestriction(restrictionIndex: number): void {
        this.restrictions = this.restrictions.filter((_, index) => index !== restrictionIndex);
        this.updateRestrictionsData();
    }

    public removeAllRestrictions(): void {
        this.restrictions = [];

        if (this.removeValidationMessages) {
            this.removeValidationMessages();
        }

        this.updateRestrictionsData();
    }

    public showRemoveAllButton(): boolean {
        return this.restrictionUuids.length > 0 && this.editable;
    }

    public matchCriteriaChanged(selectedCriteriaId: string): void {
        this.criteria = this.criteriaOptions.find((criteria) => criteria.id === selectedCriteriaId);
        this.matchCriteriaIsDirty = this.criteria !== this.originalCriteria;
        this.updateRestrictionsData();
    }

    public onSliderVisibilityChange(isVisible: boolean): void {
        this.isSliderVisible = isVisible;
    }

    private async setupResults(): Promise<void> {
        this.indexRestrictions();
        this.originalRestrictions = this.cloneRestrictions(this.restrictions);
        this.updateRestrictionsData();
        await this.applyIsRestrictionEditable();
        this.isRestrictionsReady = true;
    }

    private updateRestrictionsData(alwaysEnableSubmit = false): void {
        this.onRestrictionsChange.emit({
            onlyOneRestrictionMustApply: this.criteria.value,
            restrictionUuids: this.restrictions.map((restriction) => restriction.uuid),
            alwaysEnableSubmit
        });
    }

    private prepareRestrictionsCriteria(): void {
        this.criteriaOptions = this.pageRestrictionsCriteriaService.getRestrictionCriteriaOptions();
        if (!!this.item.onlyOneRestrictionMustApply) {
            this.criteria = this.criteriaOptions[1];
            this.originalCriteria = this.criteriaOptions[1];
        } else {
            this.criteria = this.criteriaOptions[0];
            this.originalCriteria = this.criteriaOptions[0];
        }
    }

    private isRestrictionRelatedError(validationError: GenericEditorFieldMessage): boolean {
        return includes(validationError.subject, 'restrictions');
    }

    private formatRestrictionRelatedError(
        validationError: GenericEditorFieldMessage
    ): GenericEditorFieldMessage {
        const error = clone(validationError);
        if (!stringUtils.isBlank(error.position)) {
            error.position = parseInt(error.position, 10);
        }
        if (!stringUtils.isBlank(error.subject)) {
            error.subject = error.subject.split('.').pop();
        }

        return error;
    }

    /**
     * Restriction Editor can be a part of a generic editor.
     * Whenever generic editor propagates unrelated errors, restriction editor
     * can extract errors related to itself.
     */
    private handleUnrelatedValidationErrors(validationData: {
        messages: GenericEditorFieldMessage[];
    }): void {
        this.errors = validationData.messages
            .filter((error) => this.isRestrictionRelatedError(error))
            .map((error) => this.formatRestrictionRelatedError(error));
    }

    /**
     * Whenever restriction editor opens a form to edit restriction,
     * errors related to this particular restriction are propagated to it.
     */
    private async propagateErrors(genericEditorId: string): Promise<void> {
        const restrictionInEditMode =
            this.restrictionManagement.config?.mode ===
                this.restrictionPickerConfigService.MODE_EDITING &&
            genericEditorId === ITEM_MANAGEMENT_EDITOR_ID;
        if (!restrictionInEditMode) {
            return;
        }

        const restrictionIndex = (this.restrictionManagement.config as EditingRestrictionConfig)
            .restriction.restrictionIndex;
        const errorsToPropagate = this.errors.filter(
            (error) => error.position === restrictionIndex
        );

        // Clear and reinitialize events so they do not interfere with GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT
        this.clearEvents();
        await this.systemEventService.publishAsync(
            GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
            {
                messages: errorsToPropagate,
                targetGenericEditorId: genericEditorId
            }
        );
        this.initEvents();
    }

    // Index is provided for each restriction so any restriction without
    // uuid can be easily identified for error propagation
    private indexRestrictions(): void {
        this.restrictions = (this.restrictions || []).map(
            (restriction, index) =>
                ({
                    ...restriction,
                    restrictionIndex: index
                } as RestrictionCMSItem)
        );
    }

    private cloneRestrictions(restrictions: RestrictionCMSItem[]): RestrictionCMSItem[] {
        return cloneDeep(restrictions);
    }

    private initEvents(): void {
        this.unregisterErrorListener = this.systemEventService.subscribe(
            GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
            (_eventId, data) => this.handleUnrelatedValidationErrors(data)
        );
        this.unregisterErrorPropagationEvent = this.systemEventService.subscribe(
            GENERIC_EDITOR_LOADED_EVENT,
            (_eventId, data) => this.propagateErrors(data)
        );
    }

    private clearEvents(): void {
        try {
            this.unregisterErrorListener();
            this.unregisterErrorPropagationEvent();
        } catch (e) {
            // no catch handler necessary
        }
    }

    /**
     * Verifies whether restrictions are editable or not.
     * Populates internal canBeEdited attribute.
     */
    private async applyIsRestrictionEditable(): Promise<void> {
        this.restrictions = await Promise.all(
            this.restrictions.map(async (restriction) => {
                if (restriction.uid) {
                    restriction.canBeEdited =
                        await this.contextAwareEditableItemService.isItemEditable(restriction.uid);
                    if (!(this.cdr as ViewRef).destroyed) {
                        this.cdr.detectChanges();
                    }
                    return restriction;
                }
                restriction.canBeEdited = true;
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
                return restriction;
            })
        );
    }

    private setSliderConfigForAddOrCreate(): void {
        this.sliderPanelConfiguration.modal.title = 'se.cms.restriction.management.panel.title.add';
        this.sliderPanelConfiguration.modal.save.label =
            'se.cms.restriction.management.panel.button.add';

        this.sliderPanelConfiguration.modal.save.isDisabledFn = (): boolean => {
            //  disable the add button once click it to avoid duplicate actions.
            if (this.disableAddButton) {
                return true;
            }
            if (this.restrictionManagement.isDirtyFn) {
                return !this.restrictionManagement.isDirtyFn();
            }
            // disable save until save FN is bound by restriction management component
            return true;
        };

        this.sliderPanelConfiguration.modal.save.onClick = async (): Promise<void> => {
            try {
                this.disableAddButton = true;
                const restriction = await this.restrictionManagement.submitFn();
                this.disableAddButton = false;
                this.restrictions = this.restrictions.concat(restriction);
                this.updateRestrictionsData();
                this.sliderPanelHide();
            } catch {
                this.disableAddButton = false;
                this.logService.warn(
                    'RestrictionsEditorComponent.setSliderConfigForAddOrCreate - Failed to create restriction'
                );
            }
        };
    }

    private setSliderConfigForEditing(): void {
        this.sliderPanelConfiguration.modal.title =
            'se.cms.restriction.management.panel.title.edit';
        this.sliderPanelConfiguration.modal.save.label =
            'se.cms.restriction.management.panel.button.save';

        this.sliderPanelConfiguration.modal.save.isDisabledFn = (): boolean => {
            if (this.restrictionManagement.isDirtyFn) {
                return !this.restrictionManagement.isDirtyFn();
            }
            // disable save until save FN is bound by restriction management component
            return true;
        };

        this.sliderPanelConfiguration.modal.save.onClick = async (): Promise<void> => {
            const restrictionEdited = await this.restrictionManagement.submitFn();
            if (this.restrictionManagement.config) {
                const payloadRestriction = (
                    this.restrictionManagement.config as EditingRestrictionConfig
                ).restriction;
                // Copy index back because of the backend returns response without one.
                restrictionEdited.restrictionIndex = payloadRestriction.restrictionIndex;
            }

            const restrictionIndex = restrictionEdited.restrictionIndex;

            if (restrictionIndex !== -1) {
                this.restrictions = this.restrictions.map((restriction, index) => {
                    if (index === restrictionIndex) {
                        return restrictionEdited;
                    }
                    return restriction;
                });
            } else {
                throw new Error(
                    'RestrictionsEditorComponent - edited restriction not found in list: ' +
                        restrictionEdited
                );
            }

            this.updateRestrictionsData(true);
            this.sliderPanelHide();
        };
    }
}
