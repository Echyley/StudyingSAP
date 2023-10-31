/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PopoverModule, ButtonModule } from '@fundamental-ngx/core';
import { LinkModule } from '@fundamental-ngx/core/link';
import { ListModule } from '@fundamental-ngx/core/list';
import {
    HasOperationPermissionDirectiveModule,
    L10nPipeModule,
    SeGenericEditorModule,
    SelectModule,
    SharedComponentsModule,
    TooltipModule,
    TranslationModule,
    ClientPagedListModule
} from 'smarteditcommons';
import { ToolbarModule } from 'smarteditcontainer';
import { ContextAwarePageStructureService } from '../../services/ContextAwarePageStructureService';
import { PageBuilderFactory } from '../../services/pages/PageBuilderFactory';
import { PageRestrictionsModule } from '../cmsComponents/restrictionEditor';
import { WorkflowModule } from '../workflow';
import { AddPageWizardComponent } from './addPageWizard/components/AddPageWizardComponent';
import { NewPageDisplayConditionComponent } from './addPageWizard/components/newPageDisplayCondition/NewPageDisplayConditionComponent';
import { SelectPageTemplateComponent } from './addPageWizard/components/selectPageTemplate/SelectPageTemplateComponent';
import { SelectPageTypeComponent } from './addPageWizard/components/selectPageType/SelectPageTypeComponent';
import { SelectTargetCatalogVersionComponent } from './addPageWizard/components/selectTargetCatalogVersion/SelectTargetCatalogVersionComponent';
import {
    PageTypeStepComponent,
    PageTemplateStepComponent,
    PageDisplayConditionStepComponent,
    PageInfoStepComponent,
    PageRestrictionsStepComponent
} from './addPageWizard/components/wizardSteps';
import {
    ClonePageBuilderFactory,
    ClonePageWizardComponent,
    ClonePageWizardService,
    ComponentCloneInfoFormComponent,
    ComponentCloneOptionFormComponent
} from './clonePageWizard';
import {
    ClonePageInfoStepComponent,
    ClonePageOptionsStepComponent,
    ClonePageRestrictionsStepComponent
} from './clonePageWizard/wizardSteps';
import { DeletePageToolbarItemComponent } from './deletePageMenu';
import { DisplayConditionsEditorComponent } from './displayConditions/displayConditionsEditor/DisplayConditionsEditorComponent';
import { DisplayConditionsPageInfoComponent } from './displayConditions/displayConditionsPageInfo/DisplayConditionsPageInfoComponent';
import {
    DisplayConditionsPageVariationsComponent,
    CreationDateRendererComponent
} from './displayConditions/displayConditionsPageVariations';
import { DisplayConditionsPrimaryPageComponent } from './displayConditions/displayConditionsPrimaryPage/DisplayConditionsPrimaryPageComponent';
import { HomepageIconComponent } from './homepageIcon/HomepageIconComponent';
import { PageInfoMenuComponent } from './pageInfoMenu/PageInfoMenuComponent';
import {
    EditPageItemComponent,
    ClonePageItemComponent,
    PermanentlyDeletePageItemComponent,
    RestorePageItemComponent,
    DeletePageItemComponent
} from './pageItems';
import { SyncPageItemComponent } from './pageItems/syncPageItem/SyncPageItemComponent';
import { UpdatePageStatusComponent } from './pageItems/updatePageStatus/UpdatePageStatusComponent';
import { PageListComponent } from './pageList/PageListComponent';
import {
    TrashListDropdownItemsWrapperComponent,
    ModifiedTimeWrapperComponent,
    NumberOfRestrictionsWrapperComponent,
    PageStatusWrapperComponent,
    PageNameWrapperComponent,
    PageListDropdownItemsWrapperComponent
} from './pageListComponentWrappers';
import { PageListLinkComponent } from './pageListLink/PageListLinkComponent';
import { PagesLinkComponent } from './pagesLink/PagesLinkComponent';
import { RestrictionsModalComponent } from './restrictionsViewer/RestrictionsModalComponent';
import { RestrictionsViewerComponent } from './restrictionsViewer/RestrictionsViewerComponent';
import {
    PageInfoMenuService,
    PageEditorModalConfigService,
    PageEditorModalService
} from './services';
import { TrashLinkComponent } from './trashLink/TrashLinkComponent';

/**
 * Module containing all the components and services necessary to manage a page.
 */
@NgModule({
    imports: [
        CommonModule,
        L10nPipeModule,
        FormsModule,
        LinkModule,
        TranslationModule.forChild(),
        HasOperationPermissionDirectiveModule,
        TooltipModule,
        WorkflowModule,
        SharedComponentsModule,
        SelectModule,
        SeGenericEditorModule,
        PageRestrictionsModule,
        ToolbarModule,
        ClientPagedListModule,
        PopoverModule,
        ButtonModule,
        ListModule
    ],
    providers: [
        ContextAwarePageStructureService,
        PageBuilderFactory,
        PageEditorModalService,
        PageEditorModalConfigService,
        PageInfoMenuService,
        ClonePageBuilderFactory,
        ClonePageWizardService
    ],
    declarations: [
        UpdatePageStatusComponent,
        RestorePageItemComponent,
        PermanentlyDeletePageItemComponent,
        PagesLinkComponent,
        RestrictionsViewerComponent,
        RestrictionsModalComponent,
        NumberOfRestrictionsWrapperComponent,
        PageStatusWrapperComponent,
        ModifiedTimeWrapperComponent,
        TrashListDropdownItemsWrapperComponent,
        SelectPageTemplateComponent,
        SelectPageTypeComponent,
        SyncPageItemComponent,
        DeletePageToolbarItemComponent,
        HomepageIconComponent,
        AddPageWizardComponent,
        PageTypeStepComponent,
        PageTemplateStepComponent,
        PageDisplayConditionStepComponent,
        PageInfoStepComponent,
        PageRestrictionsStepComponent,
        ClonePageWizardComponent,
        ClonePageOptionsStepComponent,
        ClonePageRestrictionsStepComponent,
        SelectTargetCatalogVersionComponent,
        NewPageDisplayConditionComponent,
        ComponentCloneOptionFormComponent,
        ComponentCloneInfoFormComponent,
        ClonePageInfoStepComponent,
        EditPageItemComponent,
        ClonePageItemComponent,
        DeletePageItemComponent,
        PageListLinkComponent,
        PageListComponent,
        PageNameWrapperComponent,
        PageListDropdownItemsWrapperComponent,
        TrashLinkComponent,
        DisplayConditionsPrimaryPageComponent,
        DisplayConditionsPageInfoComponent,
        DisplayConditionsPageVariationsComponent,
        CreationDateRendererComponent,
        DisplayConditionsEditorComponent,
        PageInfoMenuComponent
    ],
    entryComponents: [
        UpdatePageStatusComponent,
        RestorePageItemComponent,
        PermanentlyDeletePageItemComponent,
        PagesLinkComponent,
        RestrictionsViewerComponent,
        RestrictionsModalComponent,
        NumberOfRestrictionsWrapperComponent,
        PageStatusWrapperComponent,
        ModifiedTimeWrapperComponent,
        TrashListDropdownItemsWrapperComponent,
        SelectPageTemplateComponent,
        SelectPageTypeComponent,
        SyncPageItemComponent,
        DeletePageToolbarItemComponent,
        HomepageIconComponent,
        AddPageWizardComponent,
        PageTypeStepComponent,
        PageTemplateStepComponent,
        PageDisplayConditionStepComponent,
        PageInfoStepComponent,
        PageRestrictionsStepComponent,
        ClonePageWizardComponent,
        ClonePageOptionsStepComponent,
        ClonePageRestrictionsStepComponent,
        SelectTargetCatalogVersionComponent,
        NewPageDisplayConditionComponent,
        ComponentCloneOptionFormComponent,
        ComponentCloneInfoFormComponent,
        ClonePageInfoStepComponent,
        EditPageItemComponent,
        ClonePageItemComponent,
        DeletePageItemComponent,
        PageListLinkComponent,
        PageNameWrapperComponent,
        PageListDropdownItemsWrapperComponent,
        TrashLinkComponent,
        DisplayConditionsPrimaryPageComponent,
        DisplayConditionsPageInfoComponent,
        DisplayConditionsPageVariationsComponent,
        CreationDateRendererComponent,
        DisplayConditionsEditorComponent,
        PageInfoMenuComponent
    ],
    exports: [HomepageIconComponent]
})
export class PageComponentsModule {}
