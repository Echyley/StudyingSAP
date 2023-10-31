/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnInit,
    ViewRef
} from '@angular/core';
import { take } from 'rxjs/operators';
import {
    ICatalogService,
    L10nPipe,
    ContextualMenuItemData,
    CONTEXTUAL_MENU_ITEM_DATA
} from 'smarteditcommons';

@Component({
    selector: 'se-external-component-button',
    templateUrl: './ExternalComponentButtonComponent.html',
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExternalComponentButtonComponent implements OnInit {
    public isReady = false;
    public catalogVersion: string;

    private catalogVersionUuid: string;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) item: ContextualMenuItemData,
        private catalogService: ICatalogService,
        private l10nPipe: L10nPipe,
        private cdr: ChangeDetectorRef
    ) {
        ({
            componentAttributes: { smarteditCatalogVersionUuid: this.catalogVersionUuid }
        } = item);
    }

    async ngOnInit(): Promise<void> {
        const catalogVersion = await this.catalogService.getCatalogVersionByUuid(
            this.catalogVersionUuid
        );
        const catalogName = await this.l10nPipe
            .transform(catalogVersion.catalogName)
            .pipe(take(1))
            .toPromise();

        this.catalogVersion = `${catalogName} (${catalogVersion.version})`;

        this.isReady = true;

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
