/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Location } from '@angular/common';
import {
    Component,
    ComponentFactoryResolver,
    OnDestroy,
    OnInit,
    TemplateRef,
    ViewChild,
    ViewContainerRef
} from '@angular/core';
import { Router } from '@angular/router';
import {
    IExperience,
    IExperienceService,
    PriorityService,
    SeRouteService,
    SeRouteShortcutConfig,
    SystemEventService
} from '../../services';
import { IUserTrackingService } from '../../services/interfaces';
import { EVENTS, USER_TRACKING_FUNCTIONALITY } from '../../utils';
import './ShortcutLink.scss';
/**
 * @description
 * The shortcut link configuration.
 */
export interface SeShortcutLinkConfig extends SeRouteShortcutConfig {
    active?: boolean;
}
@Component({
    selector: 'shortcut-link',
    templateUrl: './ShortcutLinkTemplate.html'
})
export class ShortcutLinkComponent implements OnInit, OnDestroy {
    @ViewChild('container', { read: ViewContainerRef, static: false })
    containerEntry: ViewContainerRef;

    @ViewChild('defaultTemplate', { read: TemplateRef, static: false })
    defaultTemplate: TemplateRef<any>;

    private unregFn: () => void;

    constructor(
        private router: Router,
        private location: Location,
        private resolver: ComponentFactoryResolver,
        private experienceService: IExperienceService,
        private priorityService: PriorityService,
        private readonly systemEventService: SystemEventService,
        private readonly userTrackingService: IUserTrackingService
    ) {}

    ngOnInit(): void {
        this.unregFn = this.systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, () =>
            this._createShortcutLink()
        );
    }

    ngOnDestroy(): void {
        this.unregFn();
    }

    ngAfterViewInit(): void {
        this._createShortcutLink();
    }

    onClick(shortcutLink: SeShortcutLinkConfig): void {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.NAVIGATION,
            shortcutLink.titleI18nKey
        );
        this.location.go(shortcutLink.fullPath);
        this.router.navigateByUrl(shortcutLink.fullPath);
    }

    private _createShortcutLink(): void {
        const currentExperience = this.experienceService.getCurrentExperience();
        currentExperience.then((experience) => {
            const shortcutLinks = this._getShortcutLinks(experience);
            const orderedShortcutLinks = this._orderByPriority(shortcutLinks);
            this._createShortcutLinkDynamicComponents(orderedShortcutLinks);
        });
    }

    /**
     * Creates embedded template if the shortcut link contains titleI18nKey property otherwise
     * if the shortcut link contains shortcutComponent instance it creates a new component.
     */
    private _createShortcutLinkDynamicComponents(shortcutLinks: SeShortcutLinkConfig[]): void {
        this.containerEntry.clear();
        shortcutLinks.forEach((shortcutLink) => {
            if (shortcutLink && shortcutLink.titleI18nKey) {
                this.containerEntry.createEmbeddedView(this.defaultTemplate, {
                    $implicit: shortcutLink
                });
            } else if (shortcutLink && shortcutLink.shortcutComponent) {
                const factory = this.resolver.resolveComponentFactory(
                    shortcutLink.shortcutComponent
                );
                const component = this.containerEntry.createComponent(factory);
                (component.instance as any).shortcutLink = shortcutLink;
            }
        });
    }

    /**
     * Retrieves the route shortcut configs using the SeRouteService. It then generates full path replacing
     * all placeholders with values from current experience. It also verifies whether the path is active or not.
     */
    private _getShortcutLinks(experience: IExperience): SeShortcutLinkConfig[] {
        const url = this.location.path();
        return SeRouteService.routeShortcutConfigs.map((routeShortcut) => {
            let active: boolean;
            const path = routeShortcut.fullPath
                .replace(':siteId', experience.catalogDescriptor.siteId)
                .replace(':catalogId', experience.catalogDescriptor.catalogId)
                .replace(':catalogVersion', experience.catalogDescriptor.catalogVersion);

            // Angular routes does not seem to have "/" prefix
            // That's why we optionally add "/" prefix
            if (path.startsWith('/')) {
                active = path === url;
            } else {
                active = `/${path}` === url;
            }

            const shortcutLink: SeShortcutLinkConfig = { ...routeShortcut };
            shortcutLink.fullPath = path;
            shortcutLink.active = active;
            return shortcutLink;
        });
    }

    /**
     * Orders all shortcut links by priority. If the priority is not provided it uses the DEFAULT_PRIORITY value.
     */
    private _orderByPriority(shortcutLinks: SeShortcutLinkConfig[]): SeShortcutLinkConfig[] {
        shortcutLinks.forEach(
            (shortcutLink) => (shortcutLink.priority = shortcutLink.priority || 500)
        );
        return this.priorityService.sort(shortcutLinks);
    }
}
