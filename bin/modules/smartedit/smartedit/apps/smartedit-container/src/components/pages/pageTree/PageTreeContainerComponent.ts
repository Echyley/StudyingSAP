/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit, Type } from '@angular/core';
import { IPageTreeService } from 'smarteditcommons';

@Component({
    selector: 'se-page-tree-container',
    templateUrl: './PageTreeContainerComponent.html',
    styleUrls: ['./PageTreeContainerComponent.scss']
})
export class PageTreeContainerComponent implements OnInit {
    public component: Type<any>;
    constructor(private readonly pageTreeService: IPageTreeService) {}

    async ngOnInit(): Promise<void> {
        const config = await this.pageTreeService.getTreeComponent();
        this.component = config.component;
    }
}
