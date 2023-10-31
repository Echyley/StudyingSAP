/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import { GENERIC_EDITOR_WIDGET_DATA, GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';

@Component({
    selector: 'se-component-duplicate-primary-non-content-page',
    templateUrl: './DuplicatePrimaryNonContentPageComponent.html'
})
export class DuplicatePrimaryNonContentPageComponent implements OnInit {
    public label: string;
    private page: ICMSPage;
    private readonly PRODUCT_PAGE = 'ProductPage';
    private readonly labelI18nKeys = {
        restoreCategory: 'se.cms.page.restore.category.duplicate.primaryforvariation.error',
        restoreProduct: 'se.cms.page.restore.product.duplicate.primaryforvariation.error'
    };

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        public data: GenericEditorWidgetData<ICMSPage>
    ) {
        ({ model: this.page } = data);
    }

    ngOnInit(): void {
        this.page.replace = true;
        this.label =
            this.page.typeCode === this.PRODUCT_PAGE
                ? this.labelI18nKeys.restoreProduct
                : this.labelI18nKeys.restoreCategory;
    }
}
