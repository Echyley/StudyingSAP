/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export abstract class IAuthenticationManagerService {
    abstract onLogout(): void;
    abstract onUserHasChanged(): void;
}
