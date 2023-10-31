/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { stringUtils, IAuthenticationManagerService } from '@smart/utils';
import { SmarteditRoutingService } from './routing';

@Injectable()
export class AuthenticationManager extends IAuthenticationManagerService {
    constructor(private readonly routing: SmarteditRoutingService) {
        super();
    }

    public onLogout(): void {
        const currentLocation = this.routing.path();
        if (stringUtils.isBlank(currentLocation) || currentLocation === '/') {
            this.routing.reload();
        } else {
            this.routing.go('/');
        }
    }

    public onUserHasChanged(): void {
        this.routing.reload();
    }
}
