/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { Payload } from './payload';
import { Primitive } from './primitive';

export type Cloneable = Primitive | Primitive[] | Payload;
