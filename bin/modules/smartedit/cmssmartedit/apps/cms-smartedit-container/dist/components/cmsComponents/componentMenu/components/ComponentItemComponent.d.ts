import { OnInit, OnChanges, SimpleChanges, ChangeDetectorRef } from '@angular/core';
import { ICMSComponent, IComponentSharedService } from 'cmscommons';
export declare class ComponentItemComponent implements OnInit, OnChanges {
    private componentSharedService;
    private cdr;
    componentInfo: ICMSComponent;
    cloneOnDrop: boolean;
    isComponentDisabled: boolean;
    isSharedComponent: boolean;
    constructor(componentSharedService: IComponentSharedService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    ngOnChanges(changes: SimpleChanges): void;
}
