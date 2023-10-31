/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent, HttpResponse } from '@angular/common/http';

export class HttpPaginationResponseAdapter {
    public static transform(ev: HttpEvent<any>): HttpEvent<any> {
        const event = ev as HttpResponse<any>;
        const isAdaptable: boolean = event && event.body && event.body.pagination;

        if (!isAdaptable) {
            return event;
        }

        const foundKey: string = Object.keys(event.body).find(
            (key: string) => key !== 'pagination' && Array.isArray(event.body[key])
        );

        return foundKey
            ? event.clone({ body: { ...event.body, results: event.body[foundKey] } })
            : event;
    }
}
