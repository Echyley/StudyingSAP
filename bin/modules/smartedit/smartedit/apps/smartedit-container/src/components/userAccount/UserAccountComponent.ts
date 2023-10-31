/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './UserAccount.scss';
import { Component, OnDestroy, OnInit } from '@angular/core';

import {
    CrossFrameEventService,
    EVENTS,
    IAuthenticationService,
    ISessionService,
    IModalService,
    ThemesService,
    SWITCH_LANGUAGE_EVENT
} from 'smarteditcommons';
import { IframeManagerService } from '../../services/iframe/IframeManagerService';
import { ThemeSwitchComponent } from '../theme/ThemeSwitchComponent';

@Component({
    selector: 'se-user-account',
    templateUrl: './UserAccountComponent.html'
})
export class UserAccountComponent implements OnInit, OnDestroy {
    public username: string;

    private unregUserChanged: () => void;
    constructor(
        private authenticationService: IAuthenticationService,
        private iframeManagerService: IframeManagerService,
        private crossFrameEventService: CrossFrameEventService,
        private readonly sessionService: ISessionService,
        private readonly modalService: IModalService,
        private readonly themesService: ThemesService
    ) {}

    ngOnInit(): void {
        this.unregUserChanged = this.crossFrameEventService.subscribe(EVENTS.USER_HAS_CHANGED, () =>
            this.getUsername()
        );
        this.getUsername();
        this.crossFrameEventService.subscribe(SWITCH_LANGUAGE_EVENT, () =>
            this.themesService.initThemes()
        );
    }

    ngOnDestroy(): void {
        this.unregUserChanged();
    }

    public signOut(): void {
        this.authenticationService.logout();
        this.iframeManagerService.setCurrentLocation(null);
    }

    public themeSwitch(): void {
        this.modalService.open({
            templateConfig: {
                title: 'se.modal.administration.theme.switch.title'
            },
            component: ThemeSwitchComponent,
            config: {
                focusTrapped: false,
                backdropClickCloseable: false,
                width: '650px',
                height: '380px'
            }
        });
    }

    public getUsername(): void {
        this.sessionService.getCurrentUserDisplayName().then((displayName: string) => {
            this.username = displayName;
        });
    }
}
