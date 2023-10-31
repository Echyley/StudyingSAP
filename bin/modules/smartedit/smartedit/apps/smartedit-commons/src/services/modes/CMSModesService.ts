/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IPerspectiveService } from '../perspectives/IPerspectiveService';

@Injectable()
export class CMSModesService {
    public static readonly BASIC_PERSPECTIVE_KEY = 'se.cms.perspective.basic';
    public static readonly ADVANCED_PERSPECTIVE_KEY = 'se.cms.perspective.advanced';
    public static readonly VERSIONING_PERSPECTIVE_KEY = 'se.cms.perspective.versioning';

    constructor(private readonly perspectiveService: IPerspectiveService) {}

    /**
     * Returns a promise that resolves to a boolean which is true if the current perspective loaded is versioning, false otherwise.
     */
    async isVersioningPerspectiveActive(): Promise<boolean> {
        const activePerspectiveKey = await this.perspectiveService.getActivePerspectiveKey();
        return activePerspectiveKey === CMSModesService.VERSIONING_PERSPECTIVE_KEY;
    }
}
