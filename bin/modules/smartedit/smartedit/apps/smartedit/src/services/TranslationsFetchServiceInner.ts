/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { GatewayProxied, ITranslationsFetchService, TypedMap } from 'smarteditcommons';

/* @internal */
@GatewayProxied()
@Injectable()
export class TranslationsFetchService extends ITranslationsFetchService {
    get(lang: string): Promise<TypedMap<string>> {
        'proxyFunction';
        return null;
    }
    isReady(): Promise<boolean> {
        'proxyFunction';
        return null;
    }
    waitToBeReady(): Promise<void> {
        'proxyFunction';
        return null;
    }
}
