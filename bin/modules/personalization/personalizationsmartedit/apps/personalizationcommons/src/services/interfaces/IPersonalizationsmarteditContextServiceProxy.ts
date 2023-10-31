/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { CombinedView, Customize, Personalization, SeData } from 'personalizationcommons';

export abstract class IPersonalizationsmarteditContextServiceProxy {
    public setPersonalization(personalization: Personalization): void {
        'proxyFunction';
        return undefined;
    }

    public setCustomize(customize: Customize): void {
        'proxyFunction';
        return undefined;
    }

    public setCombinedView(combinedView: CombinedView): void {
        'proxyFunction';
        return undefined;
    }

    public setSeData(seData: SeData): void {
        'proxyFunction';
        return undefined;
    }
}
