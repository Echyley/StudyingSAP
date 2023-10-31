/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Directive, HostListener, Inject, Input } from '@angular/core';
import {
    IStorageService,
    YJQUERY_TOKEN,
    PAGE_TREE_PANEL_WIDTH_COOKIE_NAME
} from 'smarteditcommons';

/** @internal */
interface Position {
    x: number;
    width: number;
}

/**
 * Used to adjust the width of page tree continer in storefrontPage
 */
@Directive({
    selector: '[resizer]'
})
export class ResizerDirective {
    @Input() resizer: string;
    private readonly instanceId: string;

    constructor(
        private readonly storageService: IStorageService,
        @Inject(YJQUERY_TOKEN) private readonly yjQuery: JQueryStatic
    ) {
        this.instanceId = 'rsz_' + new Date().getTime();
    }

    @HostListener('mousedown', ['$event'])
    onMouseDown($event: MouseEvent): void {
        $event.preventDefault();
        const resizeElement = this.yjQuery(this.resizer);
        const startTransition = resizeElement.css('transition');
        const startPos: Position = {
            x: $event.clientX,
            width: resizeElement.width()
        };
        resizeElement.css('transition', 'none');

        const mousemove = `mousemove.${this.instanceId}`;
        const mouseup = `mouseup.${this.instanceId}`;
        const selectstart = `selectstart.${this.instanceId}`;

        this.yjQuery(document).on(mousemove, (event: Event) => {
            const e = event as MouseEvent;
            const newWidth = startPos.width + e.clientX - startPos.x;
            resizeElement.width(newWidth);
        });

        this.yjQuery(document).on(mouseup, (event: Event) => {
            event.stopPropagation();
            event.preventDefault();

            this.yjQuery(document).off(mousemove);
            this.yjQuery(document).off(mouseup);
            this.yjQuery(document).off(selectstart);

            resizeElement.css('transition', startTransition);
            this.yjQuery('iframe').css('pointer-events', 'auto');
            this.storageService.setValueInLocalStorage(
                PAGE_TREE_PANEL_WIDTH_COOKIE_NAME,
                resizeElement.outerWidth(),
                true
            );
        });

        this.yjQuery(document).on(selectstart, (event: Event) => {
            event.stopPropagation();
            event.preventDefault();
        });

        this.yjQuery('iframe').css('pointer-events', 'none');
    }
}
