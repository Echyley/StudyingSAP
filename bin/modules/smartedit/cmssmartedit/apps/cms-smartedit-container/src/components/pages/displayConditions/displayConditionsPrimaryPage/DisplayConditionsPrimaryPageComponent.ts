/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnChanges, SimpleChanges, Input, EventEmitter, Output } from '@angular/core';
import { DisplayConditionsFacade } from 'cmssmarteditcontainer/facades';
import {
    FetchStrategy,
    SelectItem,
    Page,
    IdWithLabel,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

@Component({
    selector: 'se-display-conditions-primary-page',
    templateUrl: './DisplayConditionsPrimaryPageComponent.html'
})
export class DisplayConditionsPrimaryPageComponent implements OnChanges {
    @Input() readOnly: boolean;
    @Input() associatedPrimaryPage: ICMSPage;
    @Input() pageType: string;
    @Output() onPrimaryPageSelect: EventEmitter<ICMSPage>;

    public associatedPrimaryPageUid: string;
    public fetchStrategy: FetchStrategy;

    constructor(
        private pageService: IPageService,
        private displayConditionsFacade: DisplayConditionsFacade
    ) {
        this.associatedPrimaryPageUid = null;
        this.onPrimaryPageSelect = new EventEmitter();

        this.fetchStrategy = {
            fetchEntity: (): Promise<IdWithLabel> =>
                Promise.resolve({
                    id: this.associatedPrimaryPage.uid,
                    label: this.associatedPrimaryPage.name
                }),
            fetchPage: (
                search: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<SelectItem>> =>
                this.displayConditionsFacade.getPrimaryPagesForPageType(this.pageType, null, {
                    search,
                    pageSize,
                    currentPage
                })
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        const associatedPrimaryPageChange = changes.associatedPrimaryPage;
        if (associatedPrimaryPageChange && !!associatedPrimaryPageChange.currentValue) {
            this.setAssociatedPrimaryPageSelected(associatedPrimaryPageChange.currentValue);
        }
    }

    public async associatedPrimaryPageUidOnChange(uid: string): Promise<void> {
        const page = await this.pageService.getPageById(uid);
        this.setAssociatedPrimaryPageSelected(page);
        this.onPrimaryPageSelect.emit(this.associatedPrimaryPage);
    }

    private setAssociatedPrimaryPageSelected(page: ICMSPage): void {
        this.associatedPrimaryPage = page;
        this.associatedPrimaryPageUid = page.uid;
    }
}
