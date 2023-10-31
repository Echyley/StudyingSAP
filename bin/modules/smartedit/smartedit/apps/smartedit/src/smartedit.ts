/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useClass:false */
import './modules/system/features/contextualMenu.scss';

import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA, ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import * as lo from 'lodash';
import {
    AuthenticationService,
    ConfirmationModalService,
    ContextualMenuService,
    DecoratorService,
    IframeClickDetectionService,
    ResizeComponentService,
    SeNamespaceService,
    SharedDataService,
    SmarteditServicesModule,
    StorageService,
    TranslationsFetchService,
    UrlService
} from 'smartedit/services';
import { SakExecutorService } from 'smartedit/services/sakExecutor/SakExecutorService';
import {
    moduleUtils,
    AuthenticationManager,
    BootstrapPayload,
    CrossFrameEventService,
    EVENTS,
    GatewayFactory,
    HttpInterceptorModule,
    IAuthenticationManagerService,
    IAuthenticationService,
    ICatalogService,
    IConfirmationModalService,
    IContextualMenuService,
    IDecoratorService,
    IExperienceService,
    IFeatureService,
    IIframeClickDetectionService,
    IPageInfoService,
    IPerspectiveService,
    IRenderService,
    ISessionService,
    ISharedDataService,
    ISmartEditContractChangeListener,
    IPageTreeNodeService,
    IStorageService,
    IToolbarServiceFactory,
    IUrlService,
    LanguageService,
    OVERLAY_RERENDERED_EVENT,
    PolyfillService,
    PopupOverlayModule,
    RetryInterceptor,
    SeTranslationModule,
    SmarteditErrorHandler,
    SmarteditRoutingService,
    SystemEventService,
    SMARTEDIT_DRAG_AND_DROP_EVENTS,
    TypedMap,
    LogService,
    IUserTrackingService
} from 'smarteditcommons';
import { ContextualMenuItemComponent } from './components/contextualMenuItem/ContextualMenuItemComponent';
import { SmarteditComponent } from './components/SmarteditComponent';
import {
    ContextualMenuDecoratorComponent,
    ContextualMenuItemOverlayComponent,
    MoreItemsComponent
} from './modules/system/features/contextualMenu/ContextualMenuDecoratorComponent';
import { SlotContextualMenuDecoratorComponent } from './modules/system/features/slotContextualMenu/SlotContextualMenuDecoratorComponent';
import { SlotContextualMenuItemComponent } from './modules/system/features/slotContextualMenu/SlotContextualMenuItemComponent';
import { CatalogService, ExperienceService, SessionService, UserTrackingService } from './services';
import { SmarteditElementComponent } from './services/sakExecutor/SmarteditElementComponent';
import { StorageModule } from './services/storage/StorageModuleInner';
import { ToolbarServiceFactory } from './services/ToolbarServiceFactory';

export const smarteditFactory = (payload: BootstrapPayload): any => {
    @NgModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [
            BrowserModule,
            HttpClientModule,
            StorageModule,
            SmarteditServicesModule,
            PopupOverlayModule,
            HttpInterceptorModule.forRoot(RetryInterceptor),
            SeTranslationModule.forRoot(TranslationsFetchService),
            ...payload.modules

            /* TODO: create a function and dynamic add of extensions NgModule(s) */
        ],
        declarations: [
            SmarteditComponent,
            SmarteditElementComponent,
            ContextualMenuDecoratorComponent,
            ContextualMenuItemComponent,
            MoreItemsComponent,
            ContextualMenuItemOverlayComponent,
            SlotContextualMenuDecoratorComponent,
            SlotContextualMenuItemComponent
        ],
        entryComponents: [
            SmarteditComponent,
            SmarteditElementComponent,
            ContextualMenuDecoratorComponent,
            ContextualMenuItemComponent,
            MoreItemsComponent,
            ContextualMenuItemOverlayComponent,
            SlotContextualMenuDecoratorComponent,
            SlotContextualMenuItemComponent
        ],
        providers: [
            { provide: IAuthenticationManagerService, useClass: AuthenticationManager },
            {
                provide: IConfirmationModalService,
                useClass: ConfirmationModalService
            },
            {
                provide: IAuthenticationService,
                useClass: AuthenticationService
            },
            {
                provide: ErrorHandler,
                useClass: SmarteditErrorHandler
            },
            {
                provide: IDecoratorService,
                useClass: DecoratorService
            },
            { provide: IContextualMenuService, useClass: ContextualMenuService },
            SakExecutorService,
            {
                provide: ISessionService,
                useClass: SessionService
            },
            {
                provide: IToolbarServiceFactory,
                useClass: ToolbarServiceFactory
            },
            {
                provide: ISharedDataService,
                useClass: SharedDataService
            },
            {
                provide: IStorageService,
                useClass: StorageService
            },
            {
                provide: IUrlService,
                useClass: UrlService
            },
            {
                provide: IIframeClickDetectionService,
                useClass: IframeClickDetectionService
            },
            {
                provide: ICatalogService,
                useClass: CatalogService
            },
            {
                provide: IExperienceService,
                useClass: ExperienceService
            },
            {
                provide: IUserTrackingService,
                useClass: UserTrackingService
            },
            SmarteditRoutingService,
            moduleUtils.provideValues(payload.constants),
            moduleUtils.bootstrap(smarteditBootstrap, [
                GatewayFactory,
                ISmartEditContractChangeListener,
                SeNamespaceService,
                ResizeComponentService,
                IRenderService,
                SystemEventService,
                IPageInfoService,
                IExperienceService,
                PolyfillService,
                CrossFrameEventService,
                IPerspectiveService,
                LanguageService,
                IFeatureService,
                IPageTreeNodeService,
                LogService,
                IUserTrackingService
            ])
        ],
        bootstrap: [SmarteditComponent]
    })
    class Smartedit {}
    return Smartedit;
};
/* forbiddenNameSpaces window._:false */
window.__smartedit__.SmarteditFactory = smarteditFactory;

function smarteditBootstrap(
    gatewayFactory: GatewayFactory,
    smartEditContractChangeListener: ISmartEditContractChangeListener,
    seNamespaceService: SeNamespaceService,
    resizeComponentService: ResizeComponentService,
    renderService: IRenderService,
    systemEventService: SystemEventService,
    pageInfoService: IPageInfoService,
    experienceService: IExperienceService,
    polyfillService: PolyfillService,
    crossFrameEventService: CrossFrameEventService,
    perspectiveService: IPerspectiveService,
    languageService: LanguageService,
    featureService: IFeatureService,
    pageTreeNodeService: IPageTreeNodeService,
    logSerivce: LogService,
    userTrackingService: IUserTrackingService
): void {
    gatewayFactory.initListener();

    smartEditContractChangeListener.onComponentsAdded(
        (components: HTMLElement[], isEconomyMode: boolean) => {
            if (!isEconomyMode) {
                seNamespaceService.reprocessPage();
                resizeComponentService.resizeComponents(true);
            }
            components.forEach((component) => renderService.createComponent(component));
            systemEventService.publishAsync(OVERLAY_RERENDERED_EVENT, {
                addedComponents: components
            });
        }
    );

    smartEditContractChangeListener.onComponentsRemoved(
        (
            components: {
                component: HTMLElement;
                parent: HTMLElement;
                oldAttributes: TypedMap<string>;
            }[],
            isEconomyMode: boolean
        ) => {
            if (!isEconomyMode) {
                seNamespaceService.reprocessPage();
            }
            components.forEach((entry) =>
                renderService.destroyComponent(entry.component, entry.parent, entry.oldAttributes)
            );
            systemEventService.publishAsync(OVERLAY_RERENDERED_EVENT, {
                removedComponents: lo.map(components, 'component')
            });
        }
    );

    smartEditContractChangeListener.onComponentResized((component: HTMLElement) => {
        seNamespaceService.reprocessPage();
        renderService.updateComponentSizeAndPosition(component);
    });

    smartEditContractChangeListener.onComponentRepositioned((component: HTMLElement) => {
        renderService.updateComponentSizeAndPosition(component);
    });

    smartEditContractChangeListener.onComponentChanged(
        (component: any, oldAttributes: TypedMap<string>) => {
            seNamespaceService.reprocessPage();

            renderService.destroyComponent(component, component.parent, oldAttributes);
            renderService.createComponent(component);
        }
    );

    smartEditContractChangeListener.onPageChanged((pageUUID: string) => {
        pageInfoService.getCatalogVersionUUIDFromPage().then((catalogVersionUUID: string) => {
            pageInfoService.getPageUID().then((pageUID: string) => {
                experienceService
                    .updateExperiencePageContext(catalogVersionUUID, pageUID)
                    .catch((reason) => {
                        logSerivce.error(
                            `smartedit.ts - can't updateExperiencePageContext: ${reason}`
                        );
                    });
            });
        });
        pageTreeNodeService.buildSlotNodes();
    });

    if (polyfillService.isEligibleForEconomyMode()) {
        systemEventService.subscribe(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, function () {
            smartEditContractChangeListener.setEconomyMode(true);
        });

        systemEventService.subscribe(SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, function () {
            seNamespaceService.reprocessPage();
            resizeComponentService.resizeComponents(true);
            smartEditContractChangeListener.setEconomyMode(false);
        });
    }

    crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, () => {
        perspectiveService.refreshPerspective();
        languageService.registerSwitchLanguage();
    });

    smartEditContractChangeListener.initListener();

    pageTreeNodeService.buildSlotNodes();

    userTrackingService.initConfiguration();

    // Feature registration
    featureService.register({
        key: 'se.emptySlotFix',
        nameI18nKey: 'se.emptyslotfix',
        enablingCallback: () => {
            resizeComponentService.resizeComponents(true);
        },
        disablingCallback: () => {
            resizeComponentService.resizeComponents(false);
        }
    });

    featureService.addDecorator({
        key: 'se.contextualMenu',
        nameI18nKey: 'contextualMenu'
    });

    featureService.addDecorator({
        key: 'se.slotContextualMenu',
        nameI18nKey: 'se.slot.contextual.menu'
    });
}
