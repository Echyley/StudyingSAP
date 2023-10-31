import { Injector, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Placement, ToolbarItemInternal, ToolbarItemType } from 'smarteditcommons';
import { ToolbarComponent } from './ToolbarComponent';
export declare class ToolbarActionComponent implements OnInit, OnChanges {
    toolbar: ToolbarComponent;
    item: ToolbarItemInternal;
    actionInjector: Injector;
    type: typeof ToolbarItemType;
    constructor(toolbar: ToolbarComponent);
    ngOnInit(): void;
    ngOnChanges(changes: SimpleChanges): void;
    get isCompact(): boolean;
    get placement(): Placement;
    private setup;
}
