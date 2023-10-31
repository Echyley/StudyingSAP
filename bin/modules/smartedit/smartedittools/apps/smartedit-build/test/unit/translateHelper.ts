/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { TypedMap } from 'smarteditcommons';

export class TranslateHelper {
    private translations: TypedMap<any>;
    private mock: jasmine.SpyObj<TranslateService>;

    public getMock(): jasmine.SpyObj<TranslateService> {
        return this.setup();
    }

    public prepare(mockTranslations: TypedMap<string>) {
        this.translations = mockTranslations;

        this.setup();

        return this;
    }

    private setup(): jasmine.SpyObj<TranslateService> {
        this.mock = jasmine.createSpyObj('translate', ['get', 'instant']);

        this.mock.get.and.callFake((key: string) => of(this.translations[key]));
        this.mock.instant.and.callFake((key: string) => this.translations[key]);

        return this.mock;
    }
}
