/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Injectable } from '@angular/core';
import { TranslateLoader } from '@ngx-translate/core';
import { from, Observable } from 'rxjs';
import { TypedMap } from '../../dtos';
import { ITranslationsFetchService } from './i-translations-fetch.service';

/* @internal */
@Injectable()
export class TranslateHttpLoader implements TranslateLoader {
    constructor(private translationsFetchService: ITranslationsFetchService) {}

    /**
     * Gets the translations from the server
     */
    public getTranslation(lang: string): Observable<TypedMap<string>> {
        return from(this.translationsFetchService.get(lang));
    }
}
