/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useClass:false */
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import {
    CmsCommonsModule,
    IPageContentSlotsComponentsRestService,
    IRemoveComponentService,
    VersionExperienceInterceptor,
    TRASHED_PAGE_LIST_PATH,
    IContextAwareEditableItemService,
    IComponentVisibilityAlertService,
    IComponentSharedService,
    ISlotVisibilityService,
    IEditorEnablerService,
    SlotUnsharedService,
    SlotSynchronizationService,
    IComponentMenuConditionAndCallbackService,
    IComponentEditingFacade,
    PAGE_LIST_PATH,
    NAVIGATION_MANAGEMENT_PAGE_PATH
} from 'cmscommons';
import {
    IToolbarServiceFactory,
    L10nPipeModule,
    MessageModule,
    moduleUtils,
    ResponseAdapterInterceptor,
    SeEntryModule,
    SeGenericEditorModule,
    SeRouteService,
    SeTranslationModule,
    SharedComponentsModule,
    ToolbarItemType,
    ToolbarSection,
    TooltipModule,
    IFeatureService,
    SystemEventService,
    CATALOG_DETAILS_COLUMNS,
    ICatalogDetailsService,
    ToolbarDropDownPosition,
    IPerspectiveService,
    COMPONENT_CLASS,
    InViewElementObserver,
    PAGE_TREE_NODE_CLASS,
    ShortcutLinkComponent,
    CMSModesService,
    ISlotRestrictionsService,
    IPageService,
    ISyncPollingService,
    IEditorModalService,
    IPageTreeService,
    EVENT_PAGE_TREE_PANEL_SWITCH,
    CrossFrameEventService
} from 'smarteditcommons';
import {
    CmsComponentsModule,
    ComponentMenuComponent,
    OPEN_COMPONENT_EVENT,
    GenericEditorWidgetsModule,
    MediaModule,
    CmsGenericEditorConfigurationService,
    NavigationEditorLinkComponent,
    NavigationManagementPageComponent,
    NavigationModule,
    ClonePageWizardService,
    DeletePageToolbarItemComponent,
    PageComponentsModule,
    PageInfoMenuComponent,
    PageListComponent,
    PageListLinkComponent,
    PagesLinkComponent,
    TrashedPageListComponent,
    TrashedPageListModule,
    TrashLinkComponent,
    RestrictionsModule,
    SynchronizationModule,
    PageSyncMenuToolbarItemComponent,
    CatalogDetailsSyncComponent,
    VersioningModule,
    ManagePageVersionService,
    RollbackPageVersionService,
    PageVersionMenuComponent,
    VersionItemContextComponent,
    PageApprovalSelectorComponent,
    PageDisplayStatusWrapperComponent,
    PageWorkflowMenuComponent,
    WorkflowInboxComponent,
    WorkflowModule,
    PageTreePanel,
    PageTreeModule
} from './components';
import {
    SE_CMS_CLONEPAGEMENU,
    SE_CMS_COMPONENTMENUTEMPLATE,
    SE_CMS_HTML5DRAGANDDROP_OUTER,
    SE_CMS_PAGEAPPROVALSELECTOR,
    SE_CMS_PAGEDISPLAYSTATU,
    SE_CMS_PAGEINFOMENU,
    SE_CMS_PAGEVERSIONSMENU,
    SE_CMS_PAGEWORKFLOWMENU,
    SE_VERSION_PAGE
} from './constants';
import {
    CatalogVersionRestService,
    PageRestrictionsRestService,
    PageTypesRestrictionTypesRestService,
    PageContentSlotsComponentsRestService,
    PagesFallbacksRestService,
    PagesRestService,
    PagesVariationsRestService,
    PageTypeService,
    RestrictionTypesRestService,
    StructureModeManagerFactory,
    StructuresRestService,
    TypeStructureRestService
} from './dao';
import { DisplayConditionsFacade, PageFacade } from './facades';
import {
    EditorModalService,
    HomepageService,
    ComponentVisibilityAlertService,
    CmsDragAndDropService,
    ContextAwareCatalogService,
    RulesAndPermissionsRegistrationService,
    PageRestoredAlertService,
    ActionableAlertService,
    ClonePageAlertComponent,
    ClonePageAlertService,
    PageRestoredAlertComponent,
    ComponentSharedService,
    GenericEditorModalComponent,
    ContextAwareEditableItemService,
    GenericEditorModalService,
    ExperienceGuard,
    DisplayConditionsEditorModel,
    PageDisplayConditionsService,
    PageRestrictionsCriteriaService,
    PageRestrictionsService,
    PageTypesRestrictionTypesService,
    RestrictionTypesService,
    PageService,
    PageTreeComponentMenuService,
    AddPageWizardService,
    ManagePageService,
    RestrictionsStepHandlerFactory,
    PageTemplateService,
    ProductCategoryService,
    RemoveComponentService,
    RestrictionsService,
    SlotRestrictionsService,
    SlotVisibilityService,
    SyncPollingService,
    EditorEnablerService,
    ComponentMenuConditionAndCallbackService,
    PageRestoreModalService,
    NodeInfoService,
    PageTreeSlotMenuService,
    PageTreeChildComponentMenuService,
    ComponentEditingFacade
} from './services';

@SeEntryModule('cmssmarteditContainer')
@NgModule({
    imports: [
        CmsCommonsModule,
        BrowserModule,
        SharedComponentsModule,
        SeGenericEditorModule,
        MessageModule,
        TooltipModule,
        WorkflowModule,
        VersioningModule,
        SynchronizationModule,
        SeTranslationModule.forChild(),
        L10nPipeModule,
        NavigationModule,
        GenericEditorWidgetsModule,
        MediaModule,
        PageComponentsModule,
        TrashedPageListModule,
        RestrictionsModule,
        CmsComponentsModule,
        PageTreeModule,
        FormsModule,
        // Routes are "flat" because there are routes registered also in smarteditcontainer.ts
        // And they conflict each (overriding themselves)
        SeRouteService.provideNgRoute(
            [
                {
                    path: TRASHED_PAGE_LIST_PATH,
                    component: TrashedPageListComponent,
                    canActivate: [ExperienceGuard]
                },
                {
                    path: PAGE_LIST_PATH,
                    component: PageListComponent,
                    canActivate: [ExperienceGuard],
                    titleI18nKey: 'se.cms.pagelist.title',
                    priority: 20
                },
                {
                    path: NAVIGATION_MANAGEMENT_PAGE_PATH,
                    component: NavigationManagementPageComponent,
                    titleI18nKey: 'se.cms.toolbaritem.navigationmenu.name',
                    canActivate: [ExperienceGuard],
                    priority: 10
                }
            ],
            {
                useHash: true,
                initialNavigation: 'enabledNonBlocking',
                onSameUrlNavigation: 'reload'
            }
        )
    ],
    providers: [
        PageRestrictionsRestService,
        PageRestrictionsCriteriaService,
        ExperienceGuard,
        ActionableAlertService,
        PageRestrictionsCriteriaService,
        PageRestoredAlertService,
        PageRestoreModalService,
        HomepageService,
        ManagePageService,
        PageTypesRestrictionTypesRestService,
        PageTypesRestrictionTypesService,
        RestrictionTypesRestService,
        RestrictionTypesService,
        ProductCategoryService,
        CatalogVersionRestService,
        PagesRestService,
        PagesVariationsRestService,
        PagesFallbacksRestService,
        PageTypeService,
        StructuresRestService,
        StructureModeManagerFactory,
        TypeStructureRestService,
        RestrictionsService,
        PageRestrictionsService,
        GenericEditorModalService,
        RestrictionsStepHandlerFactory,
        PageFacade,
        PageDisplayConditionsService,
        PageTemplateService,
        DisplayConditionsFacade,
        CmsDragAndDropService,
        DisplayConditionsEditorModel,
        ContextAwareCatalogService,
        RulesAndPermissionsRegistrationService,
        AddPageWizardService,
        ClonePageAlertService,
        NodeInfoService,
        PageTreeComponentMenuService,
        PageTreeSlotMenuService,
        PageTreeChildComponentMenuService,
        SlotUnsharedService,
        SlotSynchronizationService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: VersionExperienceInterceptor,
            multi: true
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ResponseAdapterInterceptor,
            multi: true
        },
        {
            provide: IPageContentSlotsComponentsRestService,
            useClass: PageContentSlotsComponentsRestService
        },
        {
            provide: ISyncPollingService,
            useClass: SyncPollingService
        },
        {
            provide: IRemoveComponentService,
            useClass: RemoveComponentService
        },
        {
            provide: IPageService,
            useClass: PageService
        },
        {
            provide: IContextAwareEditableItemService,
            useClass: ContextAwareEditableItemService
        },
        {
            provide: IEditorModalService,
            useClass: EditorModalService
        },
        {
            provide: IComponentVisibilityAlertService,
            useClass: ComponentVisibilityAlertService
        },
        {
            provide: IComponentSharedService,
            useClass: ComponentSharedService
        },
        {
            provide: ISlotRestrictionsService,
            useClass: SlotRestrictionsService
        },
        {
            provide: ISlotVisibilityService,
            useClass: SlotVisibilityService
        },
        {
            provide: IEditorEnablerService,
            useClass: EditorEnablerService
        },
        {
            provide: IComponentMenuConditionAndCallbackService,
            useClass: ComponentMenuConditionAndCallbackService
        },
        {
            provide: IComponentEditingFacade,
            useClass: ComponentEditingFacade
        },
        moduleUtils.bootstrap(
            (
                toolbarServiceFactory: IToolbarServiceFactory,
                rulesAndPermissionsRegistrationService: RulesAndPermissionsRegistrationService,
                cmsGenericEditorConfigurationService: CmsGenericEditorConfigurationService,
                featureService: IFeatureService,
                systemEventService: SystemEventService,
                clonePageWizardService: ClonePageWizardService,
                managePageVersionService: ManagePageVersionService,
                rollbackPageVersionService: RollbackPageVersionService,
                catalogDetailsService: ICatalogDetailsService,
                cmsDragAndDropService: CmsDragAndDropService,
                perspectiveService: IPerspectiveService,
                inViewElementObserver: InViewElementObserver,
                // Iframe does not work properly without it. Nothing happens when you click on "Edit" component button.
                editorModalService: IEditorModalService,
                // Need to inject for gatewayProxy initialization of componentVisibilityAlertService.
                componentVisibilityAlertService: IComponentVisibilityAlertService,
                nodeInfoService: NodeInfoService,
                crossFrameEventService: CrossFrameEventService,
                pageTreeService: IPageTreeService
            ) => {
                rulesAndPermissionsRegistrationService.register();

                setForCmsGenericEditorConfiguration(cmsGenericEditorConfigurationService);

                pageTreeService.registerTreeComponent({
                    component: PageTreePanel
                });

                addToolbarItemForFeatureService(featureService, {
                    crossFrameEventService,
                    systemEventService,
                    clonePageWizardService,
                    managePageVersionService,
                    rollbackPageVersionService,
                    cmsDragAndDropService
                });

                addItemsForToolbar(toolbarServiceFactory);

                addItemsForCatalogDetails(catalogDetailsService);

                registerForPerspective(perspectiveService);

                addSelectorForInViewElementObserver(inViewElementObserver, cmsDragAndDropService);
            },
            [
                IToolbarServiceFactory,
                RulesAndPermissionsRegistrationService,
                CmsGenericEditorConfigurationService,
                IFeatureService,
                SystemEventService,
                ClonePageWizardService,
                ManagePageVersionService,
                RollbackPageVersionService,
                ICatalogDetailsService,
                CmsDragAndDropService,
                IPerspectiveService,
                InViewElementObserver,
                IEditorModalService,
                IComponentVisibilityAlertService,
                NodeInfoService,
                CrossFrameEventService,
                IPageTreeService
            ]
        )
    ],
    declarations: [
        GenericEditorModalComponent,
        PageRestoredAlertComponent,
        ClonePageAlertComponent
    ],
    entryComponents: [
        GenericEditorModalComponent,
        PageRestoredAlertComponent,
        ClonePageAlertComponent
    ]
})
export class CmssmarteditContainerModule {}

function addSelectorForInViewElementObserver(
    inViewElementObserver: InViewElementObserver,
    cmsDragAndDropService: CmsDragAndDropService
): void {
    inViewElementObserver.addSelector(`.${COMPONENT_CLASS}`, () => {
        cmsDragAndDropService.update();
    });
    inViewElementObserver.addSelector(`.${PAGE_TREE_NODE_CLASS}`, () => {
        cmsDragAndDropService.updateInPageTree();
    });
}

function addItemsForCatalogDetails(catalogDetailsService: ICatalogDetailsService): void {
    catalogDetailsService.addItems([
        {
            component: PageListLinkComponent
        },
        {
            component: NavigationEditorLinkComponent
        }
    ]);

    catalogDetailsService.addItems(
        [
            {
                component: CatalogDetailsSyncComponent
            }
        ],
        CATALOG_DETAILS_COLUMNS.RIGHT
    );
}

function setForCmsGenericEditorConfiguration(
    cmsGenericEditorConfigurationService: CmsGenericEditorConfigurationService
): void {
    cmsGenericEditorConfigurationService.setDefaultEditorFieldMappings();
    cmsGenericEditorConfigurationService.setDefaultTabFieldMappings();
    cmsGenericEditorConfigurationService.setDefaultTabsConfiguration();
}

function registerForPerspective(perspectiveService: IPerspectiveService): void {
    perspectiveService.register({
        key: CMSModesService.BASIC_PERSPECTIVE_KEY,
        nameI18nKey: 'se.cms.perspective.basic.name',
        descriptionI18nKey: 'se.hotkey.tooltip',
        features: [
            'se.contextualMenu',
            'se.cms.dragandropbutton',
            'se.cms.remove',
            'se.cms.edit',
            SE_CMS_COMPONENTMENUTEMPLATE,
            SE_CMS_CLONEPAGEMENU,
            SE_CMS_PAGEINFOMENU,
            'se.emptySlotFix',
            'se.cms.html5DragAndDrop',
            'disableSharedSlotEditing',
            'sharedSlotDisabledDecorator',
            SE_CMS_HTML5DRAGANDDROP_OUTER,
            'externalComponentDecorator',
            'externalcomponentbutton',
            'externalSlotDisabledDecorator',
            'clonecomponentbutton',
            'deletePageMenu',
            SE_CMS_PAGEWORKFLOWMENU,
            SE_CMS_PAGEDISPLAYSTATU,
            SE_CMS_PAGEAPPROVALSELECTOR,
            'se.cms.sharedcomponentbutton'
        ],
        perspectives: []
    });

    /* Note: For advance edit mode, the ordering of the entries in the features list will determine the order the buttons will show in the slot contextual menu */
    /* externalSlotDisabledDecorator will be removed after 2105 release */
    perspectiveService.register({
        key: CMSModesService.ADVANCED_PERSPECTIVE_KEY,
        nameI18nKey: 'se.cms.perspective.advanced.name',
        descriptionI18nKey: 'se.hotkey.tooltip',
        features: [
            'se.slotContextualMenu',
            'se.slotSyncButton',
            'se.slotSharedButton',
            'se.slotContextualMenuVisibility',
            'se.contextualMenu',
            'se.cms.dragandropbutton',
            'se.cms.remove',
            'se.cms.edit',
            SE_CMS_COMPONENTMENUTEMPLATE,
            SE_CMS_CLONEPAGEMENU,
            SE_CMS_PAGEINFOMENU,
            'se.cms.pageSyncMenu',
            'se.emptySlotFix',
            'se.cms.html5DragAndDrop',
            SE_CMS_HTML5DRAGANDDROP_OUTER,
            'syncIndicator',
            'externalComponentDecorator',
            'externalcomponentbutton',
            'clonecomponentbutton',
            'slotUnsharedButton',
            'deletePageMenu',
            SE_CMS_PAGEVERSIONSMENU,
            SE_CMS_PAGEWORKFLOWMENU,
            SE_CMS_PAGEDISPLAYSTATU,
            SE_CMS_PAGEAPPROVALSELECTOR,
            'se.cms.sharedcomponentbutton',
            'se.cms.pageTreeMenu',
            'se.cms.openInPageTreeButton'
        ],
        perspectives: []
    });

    perspectiveService.register({
        key: CMSModesService.VERSIONING_PERSPECTIVE_KEY,
        nameI18nKey: 'se.cms.perspective.versioning.name',
        descriptionI18nKey: 'se.cms.perspective.versioning.description',
        features: [
            SE_CMS_PAGEVERSIONSMENU,
            'se.cms.createVersionMenu',
            'se.cms.rollbackVersionMenu',
            SE_CMS_PAGEINFOMENU,
            'disableSharedSlotEditing',
            'sharedSlotDisabledDecorator',
            'externalSlotDisabledDecorator',
            'externalComponentDecorator'
        ],
        perspectives: [],
        permissions: [SE_VERSION_PAGE],
        isHotkeyDisabled: true
    });
}

function addItemsForToolbar(toolbarServiceFactory: IToolbarServiceFactory): void {
    const smartEditTrashPageToolbarService = toolbarServiceFactory.getToolbarService(
        'smartEditTrashPageToolbar'
    );
    smartEditTrashPageToolbarService.addItems([
        {
            key: 'se.cms.pages.list.link',
            type: ToolbarItemType.TEMPLATE,
            component: PagesLinkComponent,
            priority: 1,
            section: ToolbarSection.left
        }
    ]);

    const smartEditHeaderToolbarService =
        toolbarServiceFactory.getToolbarService('smartEditHeaderToolbar');
    smartEditHeaderToolbarService.addItems([
        {
            key: 'se.cms.workflowInbox',
            type: ToolbarItemType.TEMPLATE,
            component: WorkflowInboxComponent,
            priority: 5,
            section: ToolbarSection.right,
            dropdownPosition: ToolbarDropDownPosition.right
        }
    ]);

    const smartEditNavigationToolbarService = toolbarServiceFactory.getToolbarService(
        'smartEditNavigationToolbar'
    );
    smartEditNavigationToolbarService.addItems([
        {
            key: 'se.cms.shortcut',
            type: ToolbarItemType.TEMPLATE,
            component: ShortcutLinkComponent,
            priority: 1,
            section: ToolbarSection.left
        }
    ]);

    const smartEditPagesToolbarService =
        toolbarServiceFactory.getToolbarService('smartEditPagesToolbar');
    smartEditPagesToolbarService.addItems([
        {
            key: 'se.cms.shortcut',
            type: ToolbarItemType.TEMPLATE,
            component: ShortcutLinkComponent,
            priority: 1,
            section: ToolbarSection.left
        },
        {
            key: 'se.cms.trash.page.link',
            type: ToolbarItemType.TEMPLATE,
            component: TrashLinkComponent,
            priority: 1,
            section: ToolbarSection.right
        }
    ]);
}

/**
 *
 * @param featureService
 * @param deps Set property to any type is because that it will need new dependency
 * when adding new feature.
 *
 */
function addToolbarItemForFeatureService(
    featureService: IFeatureService,
    deps: Record<string, any>
): void {
    const {
        crossFrameEventService,
        systemEventService,
        clonePageWizardService,
        managePageVersionService,
        rollbackPageVersionService,
        cmsDragAndDropService
    } = deps;
    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: 'se.cms.pageTreeMenu',
        nameI18nKey: 'se.cms.toolbaritem.pagetreemenu.name',
        type: 'ACTION',
        iconClassName: 'sap-icon--tree se-toolbar-menu-ddlb--button__icon',
        priority: 100,
        section: 'left',
        callback: () => {
            crossFrameEventService.publish(EVENT_PAGE_TREE_PANEL_SWITCH);
        },
        permissions: ['se.read.page']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_COMPONENTMENUTEMPLATE,
        type: 'HYBRID_ACTION',
        nameI18nKey: 'se.cms.componentmenu.btn.label.addcomponent',
        descriptionI18nKey: 'cms.toolbaritem.componentmenutemplate.description',
        priority: 101,
        section: 'left',
        dropdownPosition: 'left',
        iconClassName: 'sap-icon--add se-toolbar-menu-ddlb--button__icon',
        callback: () => {
            systemEventService.publish(OPEN_COMPONENT_EVENT, {});
        },
        component: ComponentMenuComponent,
        permissions: ['se.add.component'],
        keepAliveOnClose: true
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_PAGEINFOMENU,
        type: 'TEMPLATE',
        nameI18nKey: 'se.cms.pageinfo.menu.btn.label',
        priority: 140,
        section: 'left',
        component: PageInfoMenuComponent,
        permissions: ['se.read.page']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_CLONEPAGEMENU,
        type: 'ACTION',
        nameI18nKey: 'se.cms.clonepage.menu.btn.label',
        iconClassName: 'sap-icon--duplicate se-toolbar-menu-ddlb--button__icon',
        callback: () => {
            clonePageWizardService.openClonePageWizard();
        },
        priority: 130,
        section: 'left',
        permissions: ['se.clone.page']
    });

    // sync 120
    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: 'se.cms.pageSyncMenu',
        nameI18nKey: 'se.cms.toolbaritem.pagesyncmenu.name',
        type: 'TEMPLATE',
        component: PageSyncMenuToolbarItemComponent,
        priority: 102,
        section: 'left',
        permissions: ['se.sync.page']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: 'deletePageMenu',
        nameI18nKey: 'se.cms.actionitem.page.trash',
        type: 'TEMPLATE',
        component: DeletePageToolbarItemComponent,
        priority: 150,
        section: 'left',
        permissions: ['se.delete.page.menu']
    });

    // versions 102
    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_PAGEVERSIONSMENU,
        type: 'HYBRID_ACTION',
        nameI18nKey: 'se.cms.actionitem.page.versions',
        priority: 104,
        section: 'left',
        iconClassName: 'sap-icon--timesheet se-toolbar-menu-ddlb--button__icon',
        component: PageVersionMenuComponent,
        contextComponent: VersionItemContextComponent,
        permissions: [SE_VERSION_PAGE],
        keepAliveOnClose: true
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: 'se.cms.createVersionMenu',
        type: 'ACTION',
        nameI18nKey: 'se.cms.actionitem.page.versions.create',
        iconClassName: 'sap-icon--add se-toolbar-menu-ddlb--button__icon',
        callback: () => {
            managePageVersionService.createPageVersion();
        },
        priority: 120,
        section: 'left',
        permissions: [SE_VERSION_PAGE, 'se.create.version.page']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: 'se.cms.rollbackVersionMenu',
        type: 'ACTION',
        nameI18nKey: 'se.cms.actionitem.page.versions.rollback',
        iconClassName: 'sap-icon--undo se-toolbar-menu-ddlb--button__icon',
        callback: () => {
            rollbackPageVersionService.rollbackPageVersion();
        },
        priority: 120,
        section: 'left',
        permissions: [SE_VERSION_PAGE, 'se.rollback.version.page']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_PAGEWORKFLOWMENU,
        type: 'TEMPLATE',
        nameI18nKey: 'se.cms.workflow.toolbar.view.workflow.menu',
        component: PageWorkflowMenuComponent,
        priority: 110,
        section: 'right'
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_PAGEDISPLAYSTATU,
        type: 'TEMPLATE',
        nameI18nKey: 'se.cms.page.display.status',
        component: PageDisplayStatusWrapperComponent,
        priority: 120,
        section: 'right',
        permissions: ['se.show.page.status']
    });

    featureService.addToolbarItem({
        toolbarId: 'smartEditPerspectiveToolbar',
        key: SE_CMS_PAGEAPPROVALSELECTOR,
        type: 'TEMPLATE',
        nameI18nKey: 'se.cms.page.approval.selector',
        component: PageApprovalSelectorComponent,
        priority: 165,
        section: 'right',
        permissions: ['se.force.page.approval']
    });

    featureService.register({
        key: SE_CMS_HTML5DRAGANDDROP_OUTER,
        nameI18nKey: 'se.cms.dragAndDrop.name',
        descriptionI18nKey: 'se.cms.dragAndDrop.description',
        enablingCallback: () => {
            cmsDragAndDropService.register();
            cmsDragAndDropService.apply();
        },
        disablingCallback: () => {
            cmsDragAndDropService.unregister();
        }
    });
}
