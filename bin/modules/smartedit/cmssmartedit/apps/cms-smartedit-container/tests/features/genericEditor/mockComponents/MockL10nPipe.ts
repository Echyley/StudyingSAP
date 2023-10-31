/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pipe, PipeTransform } from '@angular/core';
import { Observable, of } from 'rxjs';

@Pipe({ name: 'seL10n' })
export class MockL10nPipe implements PipeTransform {
    transform(value: any): Observable<string> {
        return of(value.en as string);
    }
}
