/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc object
 * @name @smartutils.object:CacheAction
 * @description
 * A {@link @smartutils.object:@Cached @Cached} annotation is associated to a CacheAction.
 */
export class CacheAction {
    public name: string;

    constructor(name: string) {
        this.name = name;
    }
}
