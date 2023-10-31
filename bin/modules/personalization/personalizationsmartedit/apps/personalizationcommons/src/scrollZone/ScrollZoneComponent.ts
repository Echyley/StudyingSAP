/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { DOCUMENT } from '@angular/common';
import {
    Component,
    AfterViewInit,
    Input,
    Inject,
    OnDestroy,
    OnChanges,
    SimpleChanges,
    ViewChild,
    TemplateRef,
    ViewContainerRef,
    EmbeddedViewRef
} from '@angular/core';
@Component({
    selector: 'personalizationsmartedit-scroll-zone',
    templateUrl: './ScrollZoneComponent.html'
})
export class ScrollZoneComponent implements AfterViewInit, OnChanges, OnDestroy {
    @Input() scrollZoneVisible: boolean | undefined;
    @Input() isTransparent: boolean;
    @Input() elementToScroll: JQuery<HTMLElement>;

    @ViewChild('scrollZoneBody', { static: true }) scrollZoneBody: TemplateRef<any>;

    public scrollZoneTop: boolean;
    public scrollZoneBottom: boolean;

    private viewRef: EmbeddedViewRef<any> | null;

    private isScrolling: boolean;
    private scrollZoneTopTimeout: NodeJS.Timeout;
    private scrollZoneBottomTimeout: NodeJS.Timeout;

    constructor(
        private viewContainerRef: ViewContainerRef,
        @Inject(DOCUMENT) private document: Document
    ) {
        this.scrollZoneTop = false;
        this.scrollZoneBottom = false;

        this.scrollZoneVisible = false;
        this.isTransparent = false;
    }

    ngAfterViewInit(): void {
        if (this.scrollZoneVisible) {
            this.createScrollZoneBody();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const scrollZoneVisibleChange = changes.scrollZoneVisible;
        if (scrollZoneVisibleChange && !scrollZoneVisibleChange.firstChange) {
            if (scrollZoneVisibleChange.currentValue) {
                this.createScrollZoneBody();
            } else {
                this.destroyScrollZoneBody();
            }
        }
    }

    ngOnDestroy(): void {
        this.destroyScrollZoneBody();
    }

    public onScrollTopMouseenter(): void {
        this.isScrolling = true;

        this.scrollTop();
    }

    public onScrollBottomMouseenter(): void {
        this.isScrolling = true;

        this.scrollBottom();
    }

    public stopScroll(): void {
        this.isScrolling = false;

        clearTimeout(this.scrollZoneTopTimeout);
        clearTimeout(this.scrollZoneBottomTimeout);
    }

    private scrollTop(): void {
        if (!this.isScrolling) {
            clearTimeout(this.scrollZoneTopTimeout);
            return;
        }

        this.scrollZoneTop = this.elementToScroll.scrollTop() <= 2 ? false : true;
        this.scrollZoneBottom = true;

        this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() - 15);

        this.scrollZoneTopTimeout = setTimeout(() => {
            this.scrollTop();
        }, 100);
    }

    private scrollBottom(): void {
        if (!this.isScrolling) {
            clearTimeout(this.scrollZoneBottomTimeout);
            return;
        }

        this.scrollZoneTop = true;
        const heightVisibleFromTop =
            this.elementToScroll.get(0).scrollHeight - this.elementToScroll.scrollTop();
        this.scrollZoneBottom =
            Math.abs(heightVisibleFromTop - this.elementToScroll.outerHeight()) < 2 ? false : true;

        this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() + 15);

        this.scrollZoneBottomTimeout = setTimeout(() => {
            this.scrollBottom();
        }, 100);
    }

    private createScrollZoneBody(): void {
        if (this.viewRef) {
            return;
        }
        // Appending the scrollZoneBody to its container (this component) and then immediately re-appending to document.body
        //
        // TODO: Since @angular/cdk version 9.2.4 it can be refactored to Portal
        // https://material.angular.io/cdk/portal/overview
        this.viewRef = this.viewContainerRef.createEmbeddedView(this.scrollZoneBody);
        const scrollZoneBodyDom = this.viewRef.rootNodes[0]; // personalizationsmartedit-scroll-zone-body-container
        this.document.body.appendChild(scrollZoneBodyDom);

        this.scrollZoneTop = true;
        this.scrollZoneBottom = true;
    }

    private destroyScrollZoneBody(): void {
        if (!this.viewRef) {
            return;
        }

        this.viewRef.destroy();
        this.viewRef = null;
    }
}
