/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { debounce } from 'lodash';
import { BootstrapPayload, LogService } from 'smarteditcommons';

declare global {
    /* @internal */
    interface InternalSmartedit {
        SmarteditFactory: (payload: BootstrapPayload) => any;
    }
    interface Window {
        smartedit: SmarteditNamespace;
    }
}

export interface SmarteditNamespace {
    addOnReprocessPageListener: (callback: () => void) => void;
    reprocessPage: () => void;
    applications: string[];
    domain: string;
    smarteditroot: string;
    renderComponent?: (componentId: string, componentType?: string, parentId?: string) => any;
}

/* @internal */
@Injectable()
export class SeNamespaceService {
    reprocessPage = debounce(this._reprocessPage.bind(this), 50);

    constructor(private logService: LogService) {}

    // explain slot for multiple instances of component scenario
    renderComponent(componentId: string, componentType?: string, parentId?: string): boolean {
        return this.namespace && typeof this.namespace.renderComponent === 'function'
            ? this.namespace.renderComponent(componentId, componentType, parentId)
            : false;
    }

    private _reprocessPage(): void {
        if (this.namespace && typeof this.namespace.reprocessPage === 'function') {
            this.namespace.reprocessPage();
            return;
        }
        this.logService.warn('No reprocessPage function defined on smartediFt namespace');
    }

    private get namespace(): SmarteditNamespace {
        window.smartedit = window.smartedit || ({} as SmarteditNamespace);
        return window.smartedit;
    }
}
