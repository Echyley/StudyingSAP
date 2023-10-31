/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './CollapsibleContainerComponent.scss';
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
    ViewRef
} from '@angular/core';
import { stringUtils } from '../../utils';
import { COLLAPSIBLE_DEFAULT_CONFIGURATION } from './constants';
import { CollapsibleContainerApi, CollapsibleContainerConfig } from './interfaces';

/**
 * Component that allows for the dynamic display of any HTML content on a collapsible container.
 *
 * ### Example
 *      <se-collapsible-container>
 *          <se-collapsible-container-header>
 *              Your title here
 *          </se-collapsible-container-header>
 *          <se-collapsible-container-content>
 *              Your content here
 *          </se-collapsible-container-content>
 *      </se-collapsible-container>
 */
@Component({
    selector: 'se-collapsible-container',
    templateUrl: './CollapsibleContainerComponent.html',
    styleUrls: ['./CollapsibleContainerComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CollapsibleContainerComponent implements OnInit, OnChanges, OnDestroy {
    @Input() configuration: CollapsibleContainerConfig;
    /**
     * Event emitted when the component is initialized.
     */
    @Output() getApi: EventEmitter<CollapsibleContainerApi> = new EventEmitter();

    @ViewChild('container', { static: true }) set _container(container: ElementRef) {
        this.container = container;
        this.containerHeight = container.nativeElement.scrollHeight;

        // Watch DOM change in case of asynchronous collapsible population
        if (!this.mutationObserver && this.container && this.container.nativeElement) {
            this.mutationObserver = new MutationObserver(() => {
                this.containerHeight = this.container.nativeElement.scrollHeight;
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            });
            this.mutationObserver.observe(this.container.nativeElement, {
                childList: true,
                subtree: true,
                attributes: true
            });
        }
    }

    public containerHeight = 0;
    public headingId: string = stringUtils.generateIdentifier();
    public panelId: string = stringUtils.generateIdentifier();
    public isOpen: boolean;
    public isDisabled: boolean;

    private container: ElementRef;
    private mutationObserver: MutationObserver;
    private api: CollapsibleContainerApi = {
        isExpanded: () => this.isOpen
    };

    constructor(private cdr: ChangeDetectorRef) {}

    ngOnDestroy(): void {
        this.mutationObserver.disconnect();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.configuration) {
            this.configure();
        }
    }

    ngOnInit(): void {
        this.configure();

        this.isOpen = this.configuration.expandedByDefault;
        this.getApi.emit(this.api);
    }

    public toggle(): void {
        this.isOpen = !this.isOpen;
    }

    public handleKeypress(event: KeyboardEvent): void {
        if (event.code === 'Enter') {
            this.toggle();
        }
    }

    public isIconRight(): boolean {
        return this.configuration.iconAlignment === 'right';
    }

    public isIconLeft(): boolean {
        return this.configuration.iconAlignment === 'left';
    }

    private configure(): void {
        this.configuration = {
            ...COLLAPSIBLE_DEFAULT_CONFIGURATION,
            ...(this.configuration || {})
        };
    }
}
