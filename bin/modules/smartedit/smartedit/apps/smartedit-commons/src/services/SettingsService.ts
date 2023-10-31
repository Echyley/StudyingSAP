/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { Cached, IRestService, RestServiceFactory, TypedMap } from '@smart/utils';
import { SETTINGS_URI } from '../utils';
import { rarelyChangingContent } from './cache';
type multiType = TypedMap<string | boolean | string[]>;
/*
 * Meant to be a non-protected API
 */
@Injectable()
export class SettingsService {
    private readonly restService: IRestService<multiType>;
    constructor(restServicefactory: RestServiceFactory) {
        this.restService = restServicefactory.get<multiType>(SETTINGS_URI);
    }

    @Cached({ actions: [rarelyChangingContent] })
    load(): Promise<multiType> {
        return this.restService.get();
    }

    get(key: string): Promise<string> {
        return this.load().then((map) => map[key] as string);
    }

    getBoolean(key: string): Promise<boolean> {
        return this.load().then((map) => map[key] === true || map[key] === 'true');
    }

    getStringList(key: string): Promise<string[]> {
        return this.load().then((map) => map[key] as string[]);
    }
}
