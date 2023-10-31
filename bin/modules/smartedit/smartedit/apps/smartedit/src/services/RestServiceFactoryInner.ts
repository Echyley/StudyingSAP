/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { IRestService, IRestServiceFactory } from 'smarteditcommons';
import { DelegateRestService } from './DelegateRestServiceInner';
import { RestService } from './RestService';

/** @internal */
@Injectable()
export class RestServiceFactory implements IRestServiceFactory {
    constructor(private delegateRestService: DelegateRestService) {}

    get<T>(uri: string, identifier?: string): IRestService<T> {
        return new RestService<T>(this.delegateRestService, uri, identifier);
    }
}
