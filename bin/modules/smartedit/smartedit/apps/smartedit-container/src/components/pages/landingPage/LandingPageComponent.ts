/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './LandingPageComponent.scss';
import { Location } from '@angular/common';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Subscription } from 'rxjs';
import {
    GenericEditorField,
    IAlertService,
    IBaseCatalog,
    ICatalogService,
    IGenericEditorDropdownSelectedOptionEventData,
    ISite,
    IStorageService,
    LINKED_DROPDOWN,
    SystemEventService,
    YJQUERY_TOKEN,
    EVENTS
} from 'smarteditcommons';

import { SiteService } from '../../../services';

import { ISelectedSite, SITES_ID } from './types';

/**
 * Component responsible of displaying the SmartEdit landing page.
 *
 * @internal
 * @ignore
 */
@Component({
    selector: 'se-landing-page',
    templateUrl: './LandingPageComponent.html'
})
export class LandingPageComponent implements OnInit, OnDestroy {
    public readonly sitesId = SITES_ID;
    public readonly qualifier = 'site';
    public catalogs: IBaseCatalog[] = [];
    public field: Partial<GenericEditorField>;
    public model: {
        site: string;
    };
    private paramMapSubscription: Subscription;
    private unregisterSitesDropdownEventHandler: () => void;
    private unregisterUserChangesEventHandler: () => void;
    private readonly SELECTED_SITE_COOKIE_NAME = 'seselectedsite';

    constructor(
        private siteService: SiteService,
        private catalogService: ICatalogService,
        private systemEventService: SystemEventService,
        private storageService: IStorageService,
        private alertService: IAlertService,
        private route: ActivatedRoute,
        private location: Location,
        @Inject(YJQUERY_TOKEN) private yjQuery: JQueryStatic
    ) {
        this.unregisterUserChangesEventHandler = this.systemEventService.subscribe(
            EVENTS.USER_HAS_CHANGED,
            () => {
                this.init();
            }
        );
    }

    ngOnInit(): void {
        this.init();
    }

    ngOnDestroy(): void {
        this.unregisterSitesDropdownEventHandler?.();
        this.unregisterUserChangesEventHandler?.();
    }

    private init(): void {
        if (this.paramMapSubscription) {
            this.paramMapSubscription.unsubscribe();
        } else {
            this.removeStorefrontCssClass();
        }
        this.paramMapSubscription = this.route.paramMap.subscribe((params: ParamMap) => {
            this.getCurrentSiteId(params.get('siteId')).then((siteId) => {
                this.setModel(siteId);
            });
        });

        this.siteService.getAccessibleSites().then((sites: ISite[]) => {
            this.field = {
                idAttribute: 'uid',
                labelAttributes: ['name'],
                editable: true,
                paged: false,
                options: sites
            };
        });
        this.unregisterSitesDropdownEventHandler?.();
        this.unregisterSitesDropdownEventHandler = this.systemEventService.subscribe(
            this.sitesId + LINKED_DROPDOWN,
            this.selectedSiteDropdownEventHandler.bind(this)
        );
    }

    private getCurrentSiteId(siteIdFromUrl: string): Promise<string> {
        return this.storageService
            .getValueFromLocalStorage(this.SELECTED_SITE_COOKIE_NAME, false)
            .then((siteIdFromCookie: string) =>
                this.siteService.getAccessibleSites().then((sites: ISite[]) => {
                    const isSiteAvailableFromUrl = sites.some(
                        (site: ISite) => site.uid === siteIdFromUrl
                    );
                    if (isSiteAvailableFromUrl) {
                        this.setSelectedSite(siteIdFromUrl);
                        this.updateRouteToRemoveSite();
                        return siteIdFromUrl;
                    } else {
                        if (siteIdFromUrl) {
                            this.alertService.showInfo('se.landingpage.site.url.error');
                            this.updateRouteToRemoveSite();
                        }

                        const isSelectedSiteAvailableFromCookie = sites.some(
                            (site: ISite) => site.uid === siteIdFromCookie
                        );
                        if (!isSelectedSiteAvailableFromCookie) {
                            const firstSiteId = sites.length > 0 ? sites[0].uid : null;
                            return firstSiteId;
                        } else {
                            return siteIdFromCookie;
                        }
                    }
                })
            );
    }

    private updateRouteToRemoveSite(): void {
        this.location.replaceState('');
    }

    private removeStorefrontCssClass(): void {
        const bodyTag = this.yjQuery(document.querySelector('body'));

        if (bodyTag.hasClass('is-storefront')) {
            bodyTag.removeClass('is-storefront');
        }

        if (bodyTag.hasClass('is-safari')) {
            bodyTag.removeClass('is-safari');
        }
    }

    private selectedSiteDropdownEventHandler(
        _eventId: string,
        data: IGenericEditorDropdownSelectedOptionEventData<ISelectedSite>
    ): void {
        if (data.optionObject) {
            const siteId = data.optionObject.id;
            this.setSelectedSite(siteId);
            this.loadCatalogsBySite(siteId);
            this.setModel(siteId);
        } else {
            this.catalogs = [];
        }
    }

    private setSelectedSite(siteId: string): void {
        this.storageService.setValueInLocalStorage(this.SELECTED_SITE_COOKIE_NAME, siteId, false);
    }

    private loadCatalogsBySite(siteId: string): void {
        this.catalogService
            .getContentCatalogsForSite(siteId)
            .then((catalogs: IBaseCatalog[]) => (this.catalogs = catalogs));
    }

    private setModel(siteId: string): void {
        if (this.model) {
            this.model[this.qualifier] = siteId;
        } else {
            this.model = {
                [this.qualifier]: siteId
            };
        }
    }
}
