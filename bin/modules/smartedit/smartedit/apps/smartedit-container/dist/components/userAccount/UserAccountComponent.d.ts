import './UserAccount.scss';
import { OnDestroy, OnInit } from '@angular/core';
import { CrossFrameEventService, IAuthenticationService, ISessionService, IModalService, ThemesService } from 'smarteditcommons';
import { IframeManagerService } from '../../services/iframe/IframeManagerService';
export declare class UserAccountComponent implements OnInit, OnDestroy {
    private authenticationService;
    private iframeManagerService;
    private crossFrameEventService;
    private readonly sessionService;
    private readonly modalService;
    private readonly themesService;
    username: string;
    private unregUserChanged;
    constructor(authenticationService: IAuthenticationService, iframeManagerService: IframeManagerService, crossFrameEventService: CrossFrameEventService, sessionService: ISessionService, modalService: IModalService, themesService: ThemesService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    signOut(): void;
    themeSwitch(): void;
    getUsername(): void;
}
