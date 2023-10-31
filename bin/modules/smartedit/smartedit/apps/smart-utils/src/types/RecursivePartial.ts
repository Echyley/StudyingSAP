/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export type RecursivePartial<T> = {
    [P in keyof T]?: RecursivePartial<T[P]>;
};
