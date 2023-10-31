import './CollapsibleContainerComponent.scss';
import { ChangeDetectorRef, ElementRef, EventEmitter, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { CollapsibleContainerApi, CollapsibleContainerConfig } from './interfaces';
export declare class CollapsibleContainerComponent implements OnInit, OnChanges, OnDestroy {
    private cdr;
    configuration: CollapsibleContainerConfig;
    getApi: EventEmitter<CollapsibleContainerApi>;
    set _container(container: ElementRef);
    containerHeight: number;
    headingId: string;
    panelId: string;
    isOpen: boolean;
    isDisabled: boolean;
    private container;
    private mutationObserver;
    private api;
    constructor(cdr: ChangeDetectorRef);
    ngOnDestroy(): void;
    ngOnChanges(changes: SimpleChanges): void;
    ngOnInit(): void;
    toggle(): void;
    handleKeypress(event: KeyboardEvent): void;
    isIconRight(): boolean;
    isIconLeft(): boolean;
    private configure;
}
