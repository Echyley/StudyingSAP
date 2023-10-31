/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// TODO:: Below IPageService shall also move to smarteditcommons
import { Injectable } from '@angular/core';
import {
    IPageService,
    IPermissionService,
    ICatalogVersionPermissionService
} from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from './PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from './PersonalizationsmarteditRestService';

@Injectable()
export class PersonalizationsmarteditRulesAndPermissionsRegistrationService {
    constructor(
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private permissionService: IPermissionService,
        private catalogVersionPermissionService: ICatalogVersionPermissionService,
        private pageService: IPageService
    ) {}

    public registerRules(): void {
        this.permissionService.registerRule({
            names: ['se.access.personalization'],
            verify: async () => {
                await this.catalogVersionPermissionService.hasReadPermissionOnCurrent();
                await this.personalizationsmarteditContextService.refreshExperienceData();
                try {
                    await this.personalizationsmarteditRestService.getCustomizations(
                        this.getCustomizationFilter()
                    );
                    return true;
                } catch (errorResp) {
                    if (errorResp.status === 403) {
                        // Forbidden status on GET /customizations - user doesn't have permission to personalization perspective
                        return false;
                    } else {
                        // other errors will be handled with personalization perspective turned on
                        return true;
                    }
                }
            }
        });

        this.permissionService.registerRule({
            names: ['se.access.personalization.page'],
            verify: async () => {
                const info: any = await this.pageService.getCurrentPageInfo();
                return info.typeCode !== 'EmailPage';
            }
        });

        // Permissions
        this.permissionService.registerPermission({
            aliases: ['se.personalization.open'],
            rules: ['se.access.personalization']
        });

        this.permissionService.registerPermission({
            aliases: ['se.personalization.page'],
            rules: ['se.access.personalization.page']
        });
    }

    private getCustomizationFilter(): any {
        return {
            currentPage: 0,
            currentSize: 1
        };
    }
}
