/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import {
    CONTENT_SLOT_TYPE,
    ICatalogService,
    L10nPipe,
    LogService,
    SeCustomComponent,
    CMSModesService,
    IComponentHandlerService
} from 'smarteditcommons';

@SeCustomComponent()
@Component({
    selector: 'external-component-decorator',
    templateUrl: './ExternalComponentDecoratorComponent.html',
    styleUrls: ['./ExternalComponentDecoratorComponent.scss'],
    providers: [L10nPipe]
})
export class ExternalComponentDecoratorComponent implements OnInit {
    @Input('data-smartedit-catalog-version-uuid') public smarteditCatalogVersionUuid: string;
    @Input()
    set active(val: string) {
        this.isActive = val === 'true';
    }

    public catalogVersionText: string;
    public isActive: boolean;
    public isExternalSlot: boolean;
    public isReady: boolean;
    public isVersioningPerspective: boolean;

    constructor(
        private catalogService: ICatalogService,
        private cMSModesService: CMSModesService,
        private componentHandlerService: IComponentHandlerService,
        private l10nPipe: L10nPipe,
        private logService: LogService,
        private element: ElementRef
    ) {
        this.isActive = false;
        this.isExternalSlot = false;
        this.isReady = false;
        this.isVersioningPerspective = false;
    }

    async ngOnInit(): Promise<void> {
        const parentSlotIdForComponent = this.componentHandlerService.getParentSlotForComponent(
            this.element.nativeElement
        );
        this.isExternalSlot = await this.componentHandlerService.isExternalComponent(
            parentSlotIdForComponent,
            CONTENT_SLOT_TYPE
        );
        this.isReady = false;

        this.isVersioningPerspective = await this.cMSModesService.isVersioningPerspectiveActive();

        try {
            this.catalogVersionText = await this.getCatalogVersionText(
                this.smarteditCatalogVersionUuid
            );

            this.isReady = true;
        } catch {
            this.logService.error(
                `${this.constructor.name} - cannot find catalog version for uuid`,
                this.smarteditCatalogVersionUuid
            );
        }
    }

    private async getCatalogVersionText(catalogVersionUuid: string): Promise<string> {
        const catalogVersion = await this.catalogService.getCatalogVersionByUuid(
            catalogVersionUuid
        );
        const catalogName = await this.l10nPipe
            .transform(catalogVersion.catalogName)
            .pipe(take(1))
            .toPromise();
        return `${catalogName} (${catalogVersion.version})`;
    }
}
