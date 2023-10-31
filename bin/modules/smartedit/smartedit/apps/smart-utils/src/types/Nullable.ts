/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export type Nullable<T> = { [P in keyof T]: T[P] | null };
