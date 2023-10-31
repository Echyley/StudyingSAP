/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { LinkModule } from '@fundamental-ngx/core/link';
import { forEach } from 'lodash';
import {
    BaseSiteHeaderInterceptor,
    IPersonalizationsmarteditContextMenuServiceProxy,
    PersonalizationsmarteditCommonsComponentsModule,
    ScrollZoneModule,
    CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
    COMBINED_VIEW_TOOLBAR_ITEM_KEY,
    PersonalizationsmarteditMessageHandler,
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditUtils,
    PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER,
    PERSONALIZATION_VIEW_STATUS_MAPPING_CODES,
    PERSONALIZATION_CATALOG_FILTER
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditContextServiceReverseProxy } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuterReverseProxy';
import { PersonalizationsmarteditPreviewService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditPreviewService';
import {
    ISharedDataService,
    moduleUtils,
    SeEntryModule,
    TranslationModule,
    IFeatureService,
    IPerspective,
    IPerspectiveService,
    IExperienceService,
    SmarteditBootstrapGateway,
    CrossFrameEventService,
    SHOW_TOOLBAR_ITEM_CONTEXT,
    SystemEventService,
    EVENTS,
    EVENT_PERSPECTIVE_ADDED,
    SWITCH_LANGUAGE_EVENT,
    EVENT_PERSPECTIVE_UNLOADING,
    promiseUtils
} from 'smarteditcommons';
import { CombinedViewModule, CombinedViewMenuComponent } from './combinedView';
import { PersonalizationsmarteditSharedComponentsModule } from './commonComponents/PersonalizationsmarteditSharedComponentsModule';
import { PersonalizationsmarteditContextMenuServiceProxy } from './contextMenu';
import { PersonalizationsmarteditContextMenuModule } from './contextMenu/PersonalizationsmarteditContextMenuModule';
import {
    SePersonalizationsmarteditCustomizeViewModule,
    CustomizeViewComponent
} from './customizeView';
import { PersonalizationsmarteditDataFactoryModule } from './dataFactory';
import { ManageCustomizationViewModule } from './management';
import { PersonalizationsmarteditCommerceCustomizationModule } from './management/commerceCustomizationView/PersonalizationsmarteditCommerceCustomizationModule';
import { PersonalizationsmarteditSegmentViewModule } from './management/manageCustomizationView/segmentView/PersonalizationsmarteditSegmentViewModule';
import { ManagerViewModule } from './management/managerView/ManagerViewModule';
import { SePersonalizationsmarteditServicesModule } from './service';
import { CombinedViewToolbarContextComponent } from './toolbarContext/CombinedViewToolbarContextComponent';
import { CustomizeToolbarContextComponent } from './toolbarContext/CustomizeToolbarContextComponent';
import { ManageCustomizationViewMenuComponent } from './toolbarContext/ManageCustomizationViewMenuComponent';
import { SePersonalizationsmarteditToolbarContextModule } from './toolbarContext/SePersonalizationsmarteditToolbarContextModule';
import '../../styling/style.less';

const SE_EDIT_PAGE = 'se.edit.page';
@SeEntryModule('personalizationsmarteditcontainer')
@NgModule({
    imports: [
        TranslationModule.forChild(),
        BrowserModule,
        ScrollZoneModule,
        LinkModule,
        ManageCustomizationViewModule,
        CombinedViewModule,
        PersonalizationsmarteditDataFactoryModule,
        PersonalizationsmarteditCommonsComponentsModule,
        PersonalizationsmarteditContextMenuModule,
        PersonalizationsmarteditSharedComponentsModule,
        SePersonalizationsmarteditServicesModule,
        SePersonalizationsmarteditToolbarContextModule,
        SePersonalizationsmarteditCustomizeViewModule,
        PersonalizationsmarteditCommerceCustomizationModule,
        ManagerViewModule,
        PersonalizationsmarteditSegmentViewModule
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: BaseSiteHeaderInterceptor,
            multi: true,
            deps: [ISharedDataService]
        },
        {
            provide: IPersonalizationsmarteditContextMenuServiceProxy,
            useClass: PersonalizationsmarteditContextMenuServiceProxy
        },
        moduleUtils.bootstrap(
            (
                experienceService: IExperienceService,
                crossFrameEventService: CrossFrameEventService,
                smarteditBootstrapGateway: SmarteditBootstrapGateway,
                perspectiveService: IPerspectiveService,
                featureService: IFeatureService,
                systemEventService: SystemEventService,
                personalizationsmarteditUtils: PersonalizationsmarteditUtils,
                personalizationsmarteditContextService: PersonalizationsmarteditContextService,
                personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
                personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
                personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService,
                // needs to be initialized here because it's required to work but it's not imported by any component/service in personalizationsmartedit container app
                personalizationsmarteditContextMenuServiceProxy: IPersonalizationsmarteditContextMenuServiceProxy,
                personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy
            ) => {
                const PERSONALIZATION_PERSPECTIVE_KEY = 'personalizationsmartedit.perspective';

                const clearAllContextsAndReloadPreview = (): void => {
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
                        personalizationsmarteditContextService
                    );
                    personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(
                        personalizationsmarteditPreviewService,
                        personalizationsmarteditContextService
                    );
                    personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(
                        personalizationsmarteditPreviewService,
                        personalizationsmarteditContextService
                    );
                };

                const clearAllContexts = (): void => {
                    personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
                        personalizationsmarteditContextService
                    );
                    personalizationsmarteditContextUtils.clearCustomizeContext(
                        personalizationsmarteditContextService
                    );
                    personalizationsmarteditContextUtils.clearCombinedViewContext(
                        personalizationsmarteditContextService
                    );
                };

                crossFrameEventService.subscribe(
                    EVENT_PERSPECTIVE_UNLOADING,
                    (eventId: any, unloadingPerspective: string) => {
                        if (unloadingPerspective === PERSONALIZATION_PERSPECTIVE_KEY) {
                            clearAllContextsAndReloadPreview();
                        }
                    }
                );

                systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, () => {
                    clearAllContexts();
                    personalizationsmarteditContextService.setCustomizeFiltersState({
                        catalogFilter: PERSONALIZATION_CATALOG_FILTER.CURRENT,
                        pageFilter: PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER.ONLY_THIS_PAGE,
                        statusFilter: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
                        nameFilter: ''
                    });
                    return promiseUtils.promise;
                });

                systemEventService.subscribe(EVENT_PERSPECTIVE_ADDED, () => {
                    personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
                    return promiseUtils.promise;
                });

                systemEventService.subscribe(SWITCH_LANGUAGE_EVENT, () => {
                    const combinedView = personalizationsmarteditContextService.getCombinedView();
                    forEach(combinedView.selectedItems, function (item: any) {
                        personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(
                            item.variation
                        );
                    });
                    personalizationsmarteditContextService.setCombinedView(combinedView);
                    return promiseUtils.promise;
                });

                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.pagecustomizations',
                    priority: 4,
                    section: 'left',
                    component: CustomizeViewComponent,
                    contextComponent: CustomizeToolbarContextComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--palette se-toolbar-menu-ddlb--button__icon',
                    permissions: [SE_EDIT_PAGE, 'se.personalization.page']
                });

                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: 'personalizationsmartedit.container.manager.toolbar',
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.library.name',
                    priority: 8,
                    section: 'left',
                    component: ManageCustomizationViewMenuComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--bbyd-active-sales se-toolbar-menu-ddlb--button__icon',
                    permissions: [SE_EDIT_PAGE]
                });

                featureService.register({
                    key: 'personalizationsmartedit.context.service',
                    nameI18nKey: 'personalization.context.service.name',
                    descriptionI18nKey: 'personalization.context.service.description',
                    enablingCallback: () => {
                        const personalization =
                            personalizationsmarteditContextService.getPersonalization();
                        personalization.enabled = true;
                        personalizationsmarteditContextService.setPersonalization(personalization);
                    },
                    disablingCallback: () => {
                        const personalization =
                            personalizationsmarteditContextService.getPersonalization();
                        personalization.enabled = false;
                        personalizationsmarteditContextService.setPersonalization(personalization);
                    },
                    permissions: [SE_EDIT_PAGE]
                });

                featureService.addToolbarItem({
                    toolbarId: 'smartEditPerspectiveToolbar',
                    key: COMBINED_VIEW_TOOLBAR_ITEM_KEY,
                    type: 'HYBRID_ACTION',
                    nameI18nKey: 'personalization.toolbar.combinedview.name',
                    priority: 6,
                    section: 'left',
                    component: CombinedViewMenuComponent,
                    contextComponent: CombinedViewToolbarContextComponent,
                    keepAliveOnClose: false,
                    iconClassName: 'sap-icon--switch-views se-toolbar-menu-ddlb--button__icon',
                    permissions: ['se.read.page', 'se.personalization.page']
                });

                perspectiveService.register({
                    key: PERSONALIZATION_PERSPECTIVE_KEY,
                    nameI18nKey: 'personalization.perspective.name',
                    features: [
                        'personalizationsmartedit.context.service',
                        CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY,
                        'personalizationsmartedit.container.manager.toolbar',
                        COMBINED_VIEW_TOOLBAR_ITEM_KEY,
                        'personalizationsmarteditSharedSlot',
                        'personalizationsmarteditComponentLightUp',
                        'personalizationsmarteditCombinedViewComponentLightUp',
                        'personalizationsmartedit.context.add.action',
                        'personalizationsmartedit.context.edit.action',
                        'personalizationsmartedit.context.delete.action',
                        'personalizationsmartedit.context.info.action',
                        'personalizationsmartedit.context.component.edit.action',
                        'personalizationsmartedit.context.show.action.list',
                        'externalcomponentbutton',
                        'se.contextualMenu',
                        'se.slotContextualMenu',
                        'se.emptySlotFix',
                        'se.cms.pageDisplayStatus'
                    ],
                    perspectives: [],
                    permissions: ['se.personalization.open']
                });

                smarteditBootstrapGateway.subscribe('smartEditReady', (eventId: any, data: any) => {
                    crossFrameEventService.publish(
                        SHOW_TOOLBAR_ITEM_CONTEXT,
                        CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY
                    );
                    crossFrameEventService.publish(
                        SHOW_TOOLBAR_ITEM_CONTEXT,
                        COMBINED_VIEW_TOOLBAR_ITEM_KEY
                    );

                    const customize =
                        personalizationsmarteditContextService.getCustomize().selectedCustomization;
                    const combinedView =
                        personalizationsmarteditContextService.getCombinedView().customize
                            .selectedCustomization;
                    const combinedViewCustomize =
                        personalizationsmarteditContextService.getCombinedView().selectedItems;
                    experienceService.getCurrentExperience().then((experience: any) => {
                        if (
                            !experience.variations &&
                            (customize || combinedView || combinedViewCustomize)
                        ) {
                            clearAllContexts();
                        }
                    });

                    personalizationsmarteditContextService
                        .refreshExperienceData()
                        .then(() => {
                            const experience =
                                personalizationsmarteditContextService.getSeData().seExperienceData;
                            const activePerspective: IPerspective =
                                perspectiveService.getActivePerspective() || {
                                    key: '',
                                    nameI18nKey: '',
                                    features: []
                                };
                            if (
                                activePerspective.key === PERSONALIZATION_PERSPECTIVE_KEY &&
                                experience.pageContext.catalogVersionUuid !==
                                    experience.catalogDescriptor.catalogVersionUuid
                            ) {
                                const warningConf = {
                                    message: 'personalization.warning.pagefromparent',
                                    timeout: -1
                                };
                                personalizationsmarteditMessageHandler.sendWarning(warningConf);
                            }
                        })
                        .finally(() => {
                            personalizationsmarteditContextService.applySynchronization();
                        });
                });
            },
            [
                IExperienceService,
                CrossFrameEventService,
                SmarteditBootstrapGateway,
                IPerspectiveService,
                IFeatureService,
                SystemEventService,
                PersonalizationsmarteditUtils,
                PersonalizationsmarteditContextService,
                PersonalizationsmarteditMessageHandler,
                PersonalizationsmarteditContextUtils,
                PersonalizationsmarteditPreviewService,
                IPersonalizationsmarteditContextMenuServiceProxy,
                PersonalizationsmarteditContextServiceReverseProxy
            ]
        )
    ]
})
export class PersonalizationsmarteditContainerModule {}
