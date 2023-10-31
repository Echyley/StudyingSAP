/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    OnChanges,
    Input,
    SimpleChanges,
    ViewEncapsulation,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { ICMSComponent, IComponentSharedService } from 'cmscommons';

@Component({
    selector: 'se-component-item',
    templateUrl: './ComponentItemComponent.html',
    styleUrls: ['./ComponentItemComponent.scss', './component.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentItemComponent implements OnInit, OnChanges {
    @Input() componentInfo: ICMSComponent;
    @Input() cloneOnDrop: boolean;

    public isComponentDisabled: boolean;
    public isSharedComponent: boolean;

    constructor(
        private componentSharedService: IComponentSharedService,
        private cdr: ChangeDetectorRef
    ) {}

    async ngOnInit(): Promise<void> {
        this.isSharedComponent = await this.componentSharedService.isComponentShared(
            this.componentInfo
        );

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.cloneOnDrop) {
            this.isComponentDisabled = this.cloneOnDrop && !this.componentInfo.cloneable;
        }
    }
}
