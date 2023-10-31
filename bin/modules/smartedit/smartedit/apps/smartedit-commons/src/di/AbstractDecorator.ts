/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Input, OnDestroy } from '@angular/core';
import { nodeUtils } from '../utils/NodeUtils';

export interface ComponentAttributes {
    [index: string]: string;
    smarteditCatalogVersionUuid: string;
    smarteditComponentId: string;
    smarteditComponentType: string;
    smarteditComponentUuid: string;
    smarteditElementUuid: string;
}

const scopes: string[] = [];

export abstract class AbstractDecorator implements OnDestroy {
    @Input()
    set active(val: string | any) {
        this._active = val === 'true';
    }
    get active(): boolean | any {
        return this._active;
    }

    @Input('data-smartedit-element-uuid')
    public smarteditElementuuid: string;

    @Input('data-smartedit-component-id')
    public smarteditComponentId: string;

    @Input('data-smartedit-component-uuid')
    public smarteditComponentUuid: string;

    @Input('data-smartedit-component-type')
    public smarteditComponentType: string;

    @Input('data-smartedit-catalog-version-uuid')
    public smarteditCatalogVersionUuid: string;

    @Input('data-smartedit-container-id')
    public smarteditContainerId: string;

    @Input('data-smartedit-container-uuid')
    public smarteditContainerUuid: string;

    @Input('data-smartedit-container-type')
    public smarteditContainerType: string;

    public _active: boolean;

    private _cachedCcomponentAttributes: ComponentAttributes;

    // for e2e purposes
    static getScopes(): string[] {
        return scopes;
    }

    constructor() {
        scopes.push(this.smarteditElementuuid);

        // only way to ensure inheritence
        const f = this.ngOnDestroy.bind(this);

        this.ngOnDestroy = (): void => {
            f();
            scopes.splice(scopes.indexOf(this.smarteditElementuuid), 1);
        };
    }

    get componentAttributes(): ComponentAttributes {
        if (!this._cachedCcomponentAttributes) {
            try {
                this._cachedCcomponentAttributes =
                    nodeUtils.collectSmarteditAttributesByElementUuid(this.smarteditElementuuid);
            } catch (e) {
                // the original element may have been removed
                console.log(
                    `AbstratcDecorator failed to find original element with uuid ${this.smarteditElementuuid}`
                );
            }
        }
        return this._cachedCcomponentAttributes;
    }

    ngOnDestroy(): void {
        // no-op
    }
}
