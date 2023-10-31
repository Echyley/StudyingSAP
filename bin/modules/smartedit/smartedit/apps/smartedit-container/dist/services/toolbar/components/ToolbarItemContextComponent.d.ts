import { OnDestroy, OnInit, Type } from '@angular/core';
import { CrossFrameEventService } from 'smarteditcommons';
/** @internal  */
export declare class ToolbarItemContextComponent implements OnInit, OnDestroy {
    private crossFrameEventService;
    itemKey: string;
    isOpen: boolean;
    contextComponent: Type<any>;
    displayContext: boolean;
    private unregShowContext;
    private unregHideContext;
    constructor(crossFrameEventService: CrossFrameEventService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    showContext(show: boolean): void;
    private registerCallbacks;
}
