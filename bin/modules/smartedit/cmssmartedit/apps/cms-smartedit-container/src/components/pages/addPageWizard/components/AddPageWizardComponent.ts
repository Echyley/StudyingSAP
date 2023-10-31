/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { defaultsDeep } from 'lodash';
import {
    EVENT_CONTENT_CATALOG_UPDATE,
    GenericEditorStructure,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    IConfirmationModalService,
    IExperienceService,
    IUriContext,
    SystemEventService,
    TypedMap,
    ValidationError,
    WizardConfig,
    WizardService,
    WizardStep,
    WIZARD_MANAGER,
    ICMSPage,
    CMSPageTypes,
    PageTemplateType
} from 'smarteditcommons';
import { PageType } from '../../../../dao/PageTypeService';
import { IRestrictionType } from '../../../../dao/RestrictionTypesRestService';
import { PageFacade } from '../../../../facades';
import { RestrictionTypesService } from '../../../../services/pageRestrictions/RestrictionTypesService';
import { PageBuilder, PageBuilderFactory } from '../../../../services/pages/PageBuilderFactory';
import {
    RestrictionsStepHandler,
    RestrictionsStepHandlerFactory
} from '../../../../services/pages/RestrictionsStepHandlerFactory';
import { RestrictionsEditorFunctionBindings } from '../../../../services/pages/types';
import { RestrictionsService } from '../../../../services/RestrictionsService';
import { RestrictionsDTO, WizardCallbacks, WizardStepApi } from '../../pageWizard';
import {
    PageDisplayConditionStepComponent,
    PageInfoStepComponent,
    PageRestrictionsStepComponent,
    PageTemplateStepComponent,
    PageTypeStepComponent
} from './wizardSteps';

export const ADD_PAGE_WIZARD_TITLE = 'se.cms.addpagewizard.pagetype.title';
@Component({
    selector: 'se-add-page-wizard',
    template: ''
})
export class AddPageWizardComponent implements WizardStepApi {
    public callbacks: WizardCallbacks;
    public pageBuilder: PageBuilder;
    public restrictionsEditorFunctionBindings: RestrictionsEditorFunctionBindings;
    public restrictionsStepHandler: RestrictionsStepHandler;
    public restrictionStepProperties: WizardStep;
    public uriContext: IUriContext;
    public saveInProgress: boolean;
    public typeChanged: boolean;

    private readonly ADD_PAGE_WIZARD_STEPS: TypedMap<string>;

    constructor(
        @Inject(WIZARD_MANAGER) private wizardManager: WizardService,
        private pageBuilderFactory: PageBuilderFactory,
        private restrictionsStepHandlerFactory: RestrictionsStepHandlerFactory,
        private experienceService: IExperienceService,
        private confirmationModalService: IConfirmationModalService,
        private systemEventService: SystemEventService,
        private restrictionTypesService: RestrictionTypesService,
        private restrictionsService: RestrictionsService,
        private pageFacade: PageFacade
    ) {
        const restrictionsEditorFunctionBindingsClosure = {}; // bound in the view for restrictions step

        this.uriContext = this.wizardManager.properties.uriContext;
        this.callbacks = {};

        this.restrictionStepProperties = {
            id: 'restrictionsStepId',
            name: 'se.cms.restrictions.editor.tab',
            title: ADD_PAGE_WIZARD_TITLE,
            component: PageRestrictionsStepComponent
        };
        this.restrictionsEditorFunctionBindings = restrictionsEditorFunctionBindingsClosure;
        this.restrictionsStepHandler =
            this.restrictionsStepHandlerFactory.createRestrictionsStepHandler(
                this.wizardManager,
                restrictionsEditorFunctionBindingsClosure,
                this.restrictionStepProperties
            );

        this.pageBuilder = this.pageBuilderFactory.createPageBuilder(
            this.restrictionsStepHandler,
            this.uriContext
        );

        this.saveInProgress = false;
        this.typeChanged = true;

        this.ADD_PAGE_WIZARD_STEPS = {
            PAGE_TYPE: 'pageType',
            PAGE_TEMPLATE: 'pageTemplate',
            PAGE_DISPLAY_CONDITION: 'pageDisplayCondition',
            PAGE_INFO: 'pageInfo',
            PAGE_RESTRICTIONS: this.restrictionsStepHandler.getStepId()
        };
    }

    // Enumerable methods
    // This way of defining method creates a public field in class instead of prototype method
    // which allows then to be reassigned/extended in ModalWizardTemplateComponent#assignAngularController
    public getWizardConfig = (): WizardConfig => ({
        isFormValid: (stepId): boolean => this.isFormValid(stepId),
        onNext: (): Promise<boolean> => this.onNext(),
        onDone: (): Promise<any> => this.onDone(),
        onCancel: (): Promise<any> => this.onCancel(),
        steps: [
            {
                id: this.ADD_PAGE_WIZARD_STEPS.PAGE_TYPE,
                name: 'se.cms.addpagewizard.pagetype.tabname',
                title: ADD_PAGE_WIZARD_TITLE,
                component: PageTypeStepComponent
            },
            {
                id: this.ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE,
                name: 'se.cms.addpagewizard.pagetemplate.tabname',
                title: ADD_PAGE_WIZARD_TITLE,
                component: PageTemplateStepComponent
            },
            {
                id: this.ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION,
                name: 'se.cms.addpagewizard.pageconditions.tabname',
                title: ADD_PAGE_WIZARD_TITLE,
                component: PageDisplayConditionStepComponent
            },
            {
                id: this.ADD_PAGE_WIZARD_STEPS.PAGE_INFO,
                name: 'se.cms.addpagewizard.pageinfo.tabname',
                title: ADD_PAGE_WIZARD_TITLE,
                component: PageInfoStepComponent
            }
        ]
    });

    public getRestrictionTypes = (): Promise<IRestrictionType[]> =>
        this.restrictionTypesService.getRestrictionTypesByPageType(this.getPageTypeCode());

    public getSupportedRestrictionTypes = (): Promise<string[]> =>
        this.restrictionsService.getSupportedRestrictionTypeCodes();

    public variationResult = (displayConditionResult: ICMSPage): void => {
        this.pageBuilder.displayConditionSelected(displayConditionResult);
    };

    // Class methods
    public typeSelected(pageType: PageType): void {
        this.typeChanged = true;
        this.pageBuilder.pageTypeSelected(pageType);
    }

    public templateSelected(pageTemplate: PageTemplateType): void {
        this.pageBuilder.pageTemplateSelected(pageTemplate);
    }

    public getPageTypeCode(): CMSPageTypes {
        return this.pageBuilder.getPageTypeCode();
    }

    public restrictionsResult(data: RestrictionsDTO): void {
        this.pageBuilder.setRestrictions(data.onlyOneRestrictionMustApply, data.restrictionUuids);
    }

    public isRestrictionsActive(): boolean {
        if (
            !this.typeChanged ||
            this.wizardManager.getCurrentStepId() === this.ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS
        ) {
            this.typeChanged = false;
            return true;
        }
        return false;
    }

    public getPageInfo(): ICMSPage {
        const page = this.pageBuilder.getPage();
        page.uriContext = this.uriContext;
        return page;
    }

    public getPageRestrictions(): string[] {
        return this.pageBuilder.getPageRestrictions();
    }

    public getPageInfoStructure(): GenericEditorStructure {
        return this.pageBuilder.getPageInfoStructure();
    }

    public isPageInfoActive(): boolean {
        return this.wizardManager.getCurrentStepId() === this.ADD_PAGE_WIZARD_STEPS.PAGE_INFO;
    }

    public onNext(): Promise<boolean> {
        return Promise.resolve(true);
    }

    public onCancel(): Promise<any> {
        return this.confirmationModalService.confirm({
            description: 'se.editor.cancel.confirm'
        }) as Promise<any>;
    }

    public isFormValid(stepId: string): boolean {
        switch (stepId) {
            case this.ADD_PAGE_WIZARD_STEPS.PAGE_TYPE:
                return !!this.pageBuilder.getPageTypeCode();

            case this.ADD_PAGE_WIZARD_STEPS.PAGE_TEMPLATE:
                return !!this.pageBuilder.getTemplateUuid();

            case this.ADD_PAGE_WIZARD_STEPS.PAGE_DISPLAY_CONDITION:
                return true;

            case this.ADD_PAGE_WIZARD_STEPS.PAGE_INFO:
                return (
                    !this.saveInProgress &&
                    !!this.callbacks.isDirtyPageInfo &&
                    this.callbacks.isDirtyPageInfo() &&
                    !!this.callbacks.isValidPageInfo &&
                    this.callbacks.isValidPageInfo()
                );

            case this.ADD_PAGE_WIZARD_STEPS.PAGE_RESTRICTIONS:
                return !this.saveInProgress && this.restrictionsStepHandler.isStepValid();
        }

        return false;
    }

    public async onDone(): Promise<any> {
        this.saveInProgress = true;
        const page = await this.callbacks.savePageInfo();
        defaultsDeep(page, this.pageBuilder.getPage());
        try {
            const pageCreated = await this.pageFacade.createPage(page);
            this.pageBuilder.setPageUid(pageCreated.uid);

            if (pageCreated.typeCode === 'EmailPage') {
                this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, pageCreated);
                return pageCreated;
            }

            await this.experienceService.loadExperience({
                siteId: this.uriContext.CURRENT_CONTEXT_SITE_ID,
                catalogId: this.uriContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion: this.uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
                pageId: this.pageBuilder.getPage().uid
            });
        } catch (exception) {
            this.saveInProgress = false;
            this.systemEventService.publishAsync(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                {
                    messages: exception.error.errors
                }
            );
            const errors = exception.error.errors as ValidationError[];
            if (!errors.find((error) => error.subject.indexOf('restrictions') === 0)) {
                this.wizardManager.goToStepWithId(this.ADD_PAGE_WIZARD_STEPS.PAGE_INFO);
            }

            // Reject is required to prevent modal wizard from closing when there's an error.
            return Promise.reject();
        }
        return Promise.resolve();
    }
}
