/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import './toolbar.scss';
import {
    ChangeDetectorRef,
    Component,
    Injector,
    Input,
    OnDestroy,
    OnInit,
    ViewRef
} from '@angular/core';

import {
    EVENTS,
    IIframeClickDetectionService,
    IToolbarServiceFactory,
    SmarteditRoutingService,
    SystemEventService,
    STORE_FRONT_CONTEXT,
    ToolbarItemInternal,
    TOOLBAR_ITEM,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';
import { ToolbarService } from '../services/ToolbarService';

/**
 *
 * Toolbar HTML mark-up that compiles into a configurable toolbar with an assigned {@link
 * smarteditCommonsModule.IToolbarService ToolbarService} for functionality. The toolbar listens for
 * EVENTS.PAGE_SELECTED event and invokes the callback that closes all action items.
 *
 * @param imageRoot Root folder path for images
 * @param cssClass Space-separated string of CSS classes for toolbar item styling
 * @param toolbarName Toolbar name used by the gateway proxy service
 */

/** @internal  */

@Component({
    selector: 'se-toolbar',
    templateUrl: './ToolbarComponent.html'
})
export class ToolbarComponent implements OnInit, OnDestroy {
    static readonly CLOSE_ALL_ACTION_ITEMS = 'closeAllActionItems';

    @Input() cssClass: string;
    @Input() toolbarName: string;
    @Input() imageRoot = '';

    public aliases: ToolbarItemInternal[] = [];

    private unregCloseActions: () => void = null;
    private unregCloseAll: () => void = null;
    private unregRecalcPermissions: () => void = null;
    private toolbarService: ToolbarService;

    constructor(
        private toolbarServiceFactory: IToolbarServiceFactory<ToolbarService>,
        private iframeClickDetectionService: IIframeClickDetectionService,
        private systemEventService: SystemEventService,
        private injector: Injector,
        private cdr: ChangeDetectorRef,
        private readonly routingService: SmarteditRoutingService,
        private readonly userTrackingService: IUserTrackingService
    ) {}

    ngOnInit(): void {
        this.setup();
    }

    ngOnDestroy(): void {
        this.closeAllActionItems();
        this.unregCloseActions?.();
        this.unregCloseAll?.();
        this.unregRecalcPermissions?.();
    }

    // used in ToolbarActionComponent
    public triggerAction(action: ToolbarItemInternal, $event: Event): void {
        $event.preventDefault();
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.TOOL_BAR,
            action.key
        );
        this.toolbarService.triggerAction(action);
    }

    // used in ToolbarActionComponent
    public getItemVisibility(item: ToolbarItemInternal): boolean {
        return item.component && (item.isOpen || item.keepAliveOnClose);
    }

    // used in inherited items
    public isOnStorefront(): boolean {
        return this.routingService.absUrl().includes(STORE_FRONT_CONTEXT);
    }

    public createInjector(item: ToolbarItemInternal): Injector {
        return Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: TOOLBAR_ITEM,
                    useValue: item
                }
            ]
        });
    }

    public trackByFn(_: number, item: ToolbarItemInternal): string {
        return item.key;
    }

    private closeAllActionItems(): void {
        this.aliases.forEach((alias) => {
            alias.isOpen = false;
        });
    }

    private async populatePermissions(): Promise<void> {
        const promises = this.aliases.map((alias) =>
            this.toolbarService._populateIsPermissionGranted(alias)
        );

        this.aliases = await Promise.all(promises);
    }

    private setup(): void {
        this.buildAliases();
        this.populatePermissions();
        this.registerCallbacks();
    }

    private buildAliases(): void {
        this.toolbarService = this.toolbarServiceFactory.getToolbarService(this.toolbarName);
        this.toolbarService.setOnAliasesChange((aliases: ToolbarItemInternal[]): void => {
            this.aliases = aliases;
            // When a user logs off it may throw the "ViewDestroyedError".
            // Ensure that view is not destroyed prior to detecting changes.
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        });

        this.aliases = this.toolbarService.getAliases();
    }

    private registerCallbacks(): void {
        this.unregCloseActions = this.iframeClickDetectionService.registerCallback(
            ToolbarComponent.CLOSE_ALL_ACTION_ITEMS + this.toolbarName,
            () => this.closeAllActionItems()
        );

        this.unregCloseAll = this.systemEventService.subscribe(EVENTS.PAGE_SELECTED, () =>
            this.closeAllActionItems()
        );

        this.unregRecalcPermissions = this.systemEventService.subscribe(
            EVENTS.PERMISSION_CACHE_CLEANED,
            () => this.populatePermissions()
        );
    }
}
