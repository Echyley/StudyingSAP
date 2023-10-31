/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, OnInit } from '@angular/core';
@Component({
    selector: 'personalization-infinite-scrolling',
    templateUrl: './PersonalizationsmarteditInfiniteScrollingComponent.html'
})
export class PersonalizationsmarteditInfiniteScrollingComponent implements OnInit {
    @Input() dropDownContainerClass: string;
    @Input() dropDownClass: string;
    @Input() distance = 80;
    @Input() fetchPage: () => Promise<any>;

    /** @internal */
    public initiated = false;

    ngOnInit(): void {
        this.init();
    }

    ngOnChanges(): void {
        this.init();
    }

    public nextPage(): void {
        this.fetchPage();
    }

    private init(): void {
        const wasInitiated = this.initiated;
        this.initiated = true;
        if (wasInitiated) {
            this.nextPage();
        }
    }
}
