import { OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { IPermissionService, LogService, SystemEventService } from '../../services';
/** @ignore */
export declare type IsPermissionGrantedHandler = (isPermissionGranted: boolean) => void;
/** @ignore */
export declare abstract class HasOperationPermissionBaseDirective implements OnInit, OnDestroy, OnChanges {
    private systemEventService;
    private permissionService;
    private logService;
    private _isPermissionGrantedHandler;
    private unregisterHandler;
    private permission;
    constructor(systemEventService: SystemEventService, permissionService: IPermissionService, logService: LogService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    ngOnChanges(changes: SimpleChanges): void;
    protected set isPermissionGrantedHandler(handler: IsPermissionGrantedHandler);
    private refreshIsPermissionGranted;
    private isPermissionGranted;
    private validateAndPreparePermissions;
    private toPermissions;
}
