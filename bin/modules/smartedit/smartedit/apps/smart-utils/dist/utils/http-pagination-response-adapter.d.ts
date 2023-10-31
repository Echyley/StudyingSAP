/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { HttpEvent } from '@angular/common/http';
export declare class HttpPaginationResponseAdapter {
    static transform(ev: HttpEvent<any>): HttpEvent<any>;
}
