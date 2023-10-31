/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    PersonalizationsmarteditContextUtils,
    PersonalizationsmarteditUtils
} from 'personalizationcommons';
import { IModalService, LogService } from 'smarteditcommons';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditPreviewService,
    PersonalizationsmarteditRestService
} from '../service';
import { CombinedViewConfigureComponent } from './combinedViewConfigure';

@Injectable()
export class CombinedViewCommonsService {
    constructor(
        protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils,
        protected personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        protected personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService,
        protected personalizationsmarteditUtils: PersonalizationsmarteditUtils,
        protected personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private modalService: IModalService,
        private logService: LogService
    ) {}

    public async openManagerAction(): Promise<void> {
        try {
            await this.modalService
                .open({
                    component: CombinedViewConfigureComponent,
                    templateConfig: {
                        title: 'personalization.modal.combinedview.title',
                        isDismissButtonVisible: true
                    },
                    config: {
                        focusTrapped: false,
                        backdropClickCloseable: false
                    }
                })
                .afterClosed.toPromise();

            this.personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext(
                this.personalizationsmarteditContextService
            );
            const combinedView = this.personalizationsmarteditContextService.getCombinedView();
            combinedView.enabled =
                combinedView.selectedItems && combinedView.selectedItems.length > 0;
            this.personalizationsmarteditContextService.setCombinedView(combinedView);
            this.updatePreview(this.getVariationsForPreviewTicket());
        } catch (e) {
            this.logService.debug('Combined View Items Select Modal dismissed', e);
        }
    }

    public updatePreview(previewTicketVariations: any): void {
        this.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(
            previewTicketVariations
        );
        this.updateActionsOnSelectedVariations();
    }

    public getVariationsForPreviewTicket(): any[] {
        const previewTicketVariations: any[] = [];
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        (combinedView.selectedItems || []).forEach((item: any) => {
            previewTicketVariations.push({
                customizationCode: item.customization.code,
                variationCode: item.variation.code,
                catalog: item.variation.catalog,
                catalogVersion: item.variation.catalogVersion
            });
        });
        return previewTicketVariations;
    }

    public combinedViewEnabledEvent(isEnabled: boolean): void {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        combinedView.enabled = isEnabled;
        this.personalizationsmarteditContextService.setCombinedView(combinedView);
        const customize = this.personalizationsmarteditContextService.getCustomize();
        customize.selectedCustomization = null;
        customize.selectedVariations = null;
        customize.selectedComponents = null;
        this.personalizationsmarteditContextService.setCustomize(customize);
        if (isEnabled) {
            this.updatePreview(this.getVariationsForPreviewTicket());
        } else {
            this.updatePreview([]);
        }
    }

    public isItemFromCurrentCatalog(item: any): boolean {
        return this.personalizationsmarteditUtils.isItemFromCurrentCatalog(
            item,
            this.personalizationsmarteditContextService.getSeData()
        );
    }

    private async updateActionsOnSelectedVariations(): Promise<void> {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        const promisesArray: any[] = (combinedView.selectedItems || []).map(async (item: any) => {
            const response = await this.personalizationsmarteditRestService.getActions(
                item.customization.code,
                item.variation.code,
                item.variation
            );
            item.variation.actions = response.actions;
        });
        await Promise.all(promisesArray);
        this.personalizationsmarteditContextService.setCombinedView(combinedView);
    }
}
