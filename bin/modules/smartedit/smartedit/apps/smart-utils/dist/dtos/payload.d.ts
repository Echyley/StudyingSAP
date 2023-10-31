/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Primitive } from './primitive';
export interface Payload {
    [index: string]: Primitive | Primitive[] | Payload | Payload[];
}
