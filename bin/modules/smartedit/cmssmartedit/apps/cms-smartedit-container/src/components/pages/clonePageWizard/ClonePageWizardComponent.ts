/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { merge, cloneDeep } from 'lodash';
import {
    GenericEditorStructure,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    IAlertService,
    ICatalogVersion,
    IConfirmationModalService,
    IExperience,
    IExperienceService,
    ISharedDataService,
    IUriContext,
    SystemEventService,
    TypedMap,
    ValidationError,
    WizardConfig,
    WizardService,
    WizardStep,
    WIZARD_MANAGER,
    EXPERIENCE_STORAGE_KEY,
    ICMSPage,
    CMSPageTypes,
    ISyncPollingService
} from 'smarteditcommons';
import { IRestrictionType } from '../../../dao/RestrictionTypesRestService';
import { PageFacade } from '../../../facades';
import { ClonePageAlertService } from '../../../services/actionableAlert/ClonePageAlertService';
import { RestrictionTypesService } from '../../../services/pageRestrictions/RestrictionTypesService';
import {
    RestrictionsStepHandler,
    RestrictionsStepHandlerFactory
} from '../../../services/pages/RestrictionsStepHandlerFactory';
import { RestrictionsEditorFunctionBindings } from '../../../services/pages/types';
import { RestrictionsService } from '../../../services/RestrictionsService';
import { RestrictionsDTO, WizardCallbacks, WizardStepApi } from '../pageWizard';

import { ClonePageBuilder, ClonePageBuilderFactory } from './ClonePageBuilderFactory';
import {
    ClonePageInfoStepComponent,
    ClonePageOptionsStepComponent,
    ClonePageRestrictionsStepComponent
} from './wizardSteps';
export const CLONE_PAGE_WIZARD_TITLE = 'se.cms.clonepagewizard.pageclone.title';

@Component({
    selector: 'se-clone-page-wizard',
    template: ''
})
export class ClonePageWizardComponent implements WizardStepApi {
    public uriContext: IUriContext;
    public callbacks: WizardCallbacks;
    public basePageUUID: string;
    public restrictionStepProperties: WizardStep;
    public restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings;
    public typeChanged: boolean;
    public infoChanged: boolean;

    private cloneInProgress: boolean;

    private readonly restrictionsStepHandler: RestrictionsStepHandler;

    private pageBuilder: ClonePageBuilder;
    private readonly CLONE_PAGE_WIZARD_STEPS: TypedMap<string>;

    constructor(
        @Inject(WIZARD_MANAGER) private wizardManager: WizardService,
        private clonePageBuilderFactory: ClonePageBuilderFactory,
        private restrictionsStepHandlerFactory: RestrictionsStepHandlerFactory,
        private experienceService: IExperienceService,
        private confirmationModalService: IConfirmationModalService,
        private systemEventService: SystemEventService,
        private restrictionTypesService: RestrictionTypesService,
        private restrictionsService: RestrictionsService,
        private sharedDataService: ISharedDataService,
        private clonePageAlertService: ClonePageAlertService,
        private alertService: IAlertService,
        private pageFacade: PageFacade,
        private syncPollingService: ISyncPollingService
    ) {
        this.basePageUUID = this.wizardManager.properties.basePageUUID;
        this.callbacks = {};
        this.uriContext = this.wizardManager.properties.uriContext;
        this.restrictionStepProperties = {
            id: 'restrictionsStepId',
            name: 'se.cms.restrictions.editor.tab',
            title: CLONE_PAGE_WIZARD_TITLE,
            component: ClonePageRestrictionsStepComponent
        };
        // bound in the view for restrictions step
        const restrictionsEditorFunctionBindingsClosure = {};
        this.restrictionsEditorFunctionBindings = restrictionsEditorFunctionBindingsClosure;

        this.cloneInProgress = false;
        this.infoChanged = true;
        this.typeChanged = true;

        this.restrictionsStepHandler =
            this.restrictionsStepHandlerFactory.createRestrictionsStepHandler(
                this.wizardManager,
                this.restrictionsEditorFunctionBindings,
                this.restrictionStepProperties
            );

        this.pageBuilder = this.clonePageBuilderFactory.createClonePageBuilder(
            this.restrictionsStepHandler,
            this.basePageUUID,
            this.uriContext
        );
        this.pageBuilder.init();

        this.CLONE_PAGE_WIZARD_STEPS = {
            PAGE_CLONE_OPTIONS: 'cloneOptions',
            PAGE_INFO: 'pageInfo',
            PAGE_RESTRICTIONS: this.restrictionStepProperties.id
        };
    }

    // Enumerable methods
    // This way of defining method creates a public field in class instead of prototype method
    // which allows then to be reassigned/extended in ModalWizardTemplateComponent#assignAngularController
    public getWizardConfig = (): WizardConfig => ({
        isFormValid: (stepId: string): boolean => this.isFormValid(stepId),
        onNext: (): boolean => this.onNext(),
        onDone: (): Promise<any> => this.onDone(),
        onCancel: (): Promise<any> => this.onCancel(),
        steps: [
            {
                id: this.CLONE_PAGE_WIZARD_STEPS.PAGE_CLONE_OPTIONS,
                name: 'se.cms.clonepagewizard.pagecloneoptions.tabname',
                title: CLONE_PAGE_WIZARD_TITLE,
                component: ClonePageOptionsStepComponent
            },
            {
                id: this.CLONE_PAGE_WIZARD_STEPS.PAGE_INFO,
                name: 'se.cms.clonepagewizard.pageinfo.tabname',
                title: CLONE_PAGE_WIZARD_TITLE,
                component: ClonePageInfoStepComponent
            }
        ]
    });

    public getRestrictionTypes = (): Promise<IRestrictionType[]> =>
        this.restrictionTypesService.getRestrictionTypesByPageType(this.getPageTypeCode());

    public getSupportedRestrictionTypes = (): Promise<string[]> =>
        this.restrictionsService.getSupportedRestrictionTypeCodes();

    public variationResult = (displayConditionResult: ICMSPage): void => {
        this.infoChanged = true;
        this.pageBuilder.displayConditionSelected(displayConditionResult);
    };

    // Class Methods
    public getPageTypeCode(): CMSPageTypes {
        return this.pageBuilder.getPageTypeCode();
    }

    public getPageLabel(): string {
        return this.pageBuilder.getPageLabel();
    }

    public getBasePageUuid(): string {
        return this.pageBuilder.getBasePageUuid();
    }

    public getPageTemplate(): string {
        return this.pageBuilder.getPageTemplate();
    }

    public getPageInfo(): ICMSPage {
        const page = this.pageBuilder.getPageInfo();
        page.uriContext = this.uriContext;

        return page;
    }

    public getBasePageInfo(): ICMSPage {
        const page = this.pageBuilder.getBasePageInfo();
        page.uriContext = this.uriContext;
        return page;
    }

    public getPageRestrictions(): string[] {
        return this.pageBuilder.getPageRestrictions();
    }

    public onTargetCatalogVersionSelected(catalogVersion: ICatalogVersion): void {
        this.pageBuilder.onTargetCatalogVersionSelected(catalogVersion);
    }

    public triggerUpdateCloneOptionResult(cloneOptionResult: string): void {
        this.pageBuilder.componentCloneOptionSelected(cloneOptionResult);
    }

    public getPageInfoStructure(): GenericEditorStructure {
        return this.pageBuilder.getPageInfoStructure();
    }

    public restrictionsResult(data: RestrictionsDTO): void {
        this.pageBuilder.restrictionsSelected(
            data.onlyOneRestrictionMustApply,
            data.restrictionUuids
        );
    }

    public isRestrictionsActive(): boolean {
        if (
            !this.typeChanged ||
            this.wizardManager.getCurrentStepId() === this.CLONE_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS
        ) {
            this.typeChanged = false;
            return true;
        }
        return false;
    }

    public isPageInfoActive(): boolean {
        if (
            !this.infoChanged ||
            this.wizardManager.getCurrentStepId() === this.CLONE_PAGE_WIZARD_STEPS.PAGE_INFO
        ) {
            this.infoChanged = false;
            return true;
        }
        return false;
    }

    public getTargetCatalogVersion(): ICatalogVersion {
        return this.pageBuilder.getTargetCatalogVersion();
    }

    public isBasePageInfoAvailable(): boolean {
        return this.pageBuilder.isBasePageInfoAvailable();
    }

    public onCancel(): Promise<any> {
        return this.confirmationModalService.confirm({
            description: 'se.editor.cancel.confirm'
        }) as Promise<any>;
    }

    public isFormValid(stepId: string): boolean {
        switch (stepId) {
            case this.CLONE_PAGE_WIZARD_STEPS.PAGE_CLONE_OPTIONS:
                // This step has no required inputs. However we set the valid status as soon as the data is fetched
                // to avoid modal NEXT button enabled status to be true before content is actually loaded.
                return this.isBasePageInfoAvailable();

            case this.CLONE_PAGE_WIZARD_STEPS.PAGE_INFO:
                return (
                    !this.cloneInProgress &&
                    !!this.callbacks.isValidPageInfo &&
                    this.callbacks.isValidPageInfo()
                );

            case this.CLONE_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS:
                return !this.cloneInProgress && this.pageBuilder.getPageRestrictions().length > 0;
        }

        return false;
    }

    public onNext(): boolean {
        return true;
    }

    public async onDone(): Promise<any> {
        this.cloneInProgress = true;
        try {
            const page = await this.callbacks.savePageInfo();
            const payload = this.preparePagePayload(page);

            if (this.pageBuilder.getTargetCatalogVersion()) {
                payload.siteId = this.pageBuilder.getTargetCatalogVersion().siteId;
                payload.catalogId = this.pageBuilder.getTargetCatalogVersion().catalogId;
                payload.version = this.pageBuilder.getTargetCatalogVersion().version;
            }
            const experience = (await this.sharedDataService.get(
                EXPERIENCE_STORAGE_KEY
            )) as IExperience;

            await this.createPage(payload, experience);
        } catch {
            // re-enable the button
            this.cloneInProgress = false;
            // prevent closing modal when there's an error
            return Promise.reject();
        }
        return Promise.resolve();
    }

    private async createPage(payload: ICMSPage, experience: IExperience): Promise<any> {
        try {
            const response = await this.pageFacade.createPageForSite(payload, payload.siteId);
            this.syncPollingService.getSyncStatus(payload.pageUuid, this.uriContext, true);

            if (experience.catalogDescriptor.catalogVersionUuid === response.catalogVersion) {
                this.experienceService.loadExperience({
                    siteId: payload.siteId,
                    catalogId: payload.catalogId,
                    catalogVersion: payload.version,
                    pageId: response.uid
                });
            } else {
                this.clonePageAlertService.displayClonePageAlert(response);
            }

            return this.alertService.showSuccess({
                message: 'se.cms.clonepage.alert.success',
                minWidth: '',
                mousePersist: true,
                duration: 1000,
                dismissible: true,
                width: '300px'
            });
        } catch (exception) {
            this.cloneInProgress = false; // re-enable the button
            this.systemEventService.publishAsync(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                {
                    messages: exception.error.errors
                }
            );
            const errors = exception.error.errors as ValidationError[];

            if (!errors.find((error) => error.subject.indexOf('restrictions') === 0)) {
                this.wizardManager.goToStepWithId(this.CLONE_PAGE_WIZARD_STEPS.PAGE_INFO);
            }

            return Promise.reject();
        }
    }

    private preparePagePayload(page: ICMSPage): ICMSPage {
        const newClonePage = cloneDeep(page);

        // set page info properties
        merge(newClonePage, this.pageBuilder.getPageProperties());
        // set clone option
        newClonePage.cloneComponents = this.pageBuilder.getComponentCloneOption() === 'clone';
        newClonePage.itemtype = page.typeCode;
        // set linkComponents to [].
        // As when clone page,
        // the link component should remain refer to old page.
        if (newClonePage.linkComponents && newClonePage.linkComponents.length > 0) {
            newClonePage.linkComponents = [];
        }

        if (this.isRestrictionsActive()) {
            // set restrictions
            newClonePage.restrictions = this.pageBuilder.getPageRestrictions();
        }

        return newClonePage;
    }
}
