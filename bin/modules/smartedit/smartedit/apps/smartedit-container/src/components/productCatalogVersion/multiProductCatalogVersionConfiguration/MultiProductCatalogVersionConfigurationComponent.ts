/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { isEqual } from 'lodash';
import { from, Observable } from 'rxjs';
import { take } from 'rxjs/operators';

import {
    ModalButtonAction,
    ModalButtonStyle,
    ModalManagerService,
    SelectOnChange,
    SystemEventService
} from 'smarteditcommons';
import {
    MultiProductCatalogVersionSelectorData,
    MULTI_PRODUCT_CATALOGS_UPDATED,
    SelectAdaptedCatalog,
    SelectAdaptedCatalogVersion
} from '../types';

import './MultiProductCatalogVersionConfigurationComponent.scss';

/** Represents a modal where user can select a version for each Product Catalog. */
@Component({
    selector: 'se-multi-product-catalog-version-configuration',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-multi-product-catalog-version-configuration]': 'true'
    },
    templateUrl: './MultiProductCatalogVersionConfigurationComponent.html'
})
export class MultiProductCatalogVersionConfigurationComponent implements OnInit {
    public productCatalogs: SelectAdaptedCatalog[];
    public selectedCatalogVersions: string[];
    public updatedCatalogVersions: string[];

    private readonly doneButtonId = 'done';

    constructor(
        private modalManager: ModalManagerService,
        private systemEventService: SystemEventService
    ) {}

    ngOnInit(): void {
        this.modalManager
            .getModalData()
            .pipe(take(1))
            .subscribe((data: MultiProductCatalogVersionSelectorData) => {
                this.selectedCatalogVersions = data.selectedCatalogVersions;

                this.productCatalogs = data.productCatalogs.map(
                    (productCatalog: SelectAdaptedCatalog) => {
                        const versions = productCatalog.versions.map((version) => ({
                            ...version,
                            id: version.uuid,
                            label: version.version
                        }));
                        return {
                            ...productCatalog,
                            versions,
                            fetchStrategy: {
                                fetchAll: (): Promise<SelectAdaptedCatalogVersion[]> =>
                                    Promise.resolve(versions)
                            },
                            selectedItem: productCatalog.versions.find((version) =>
                                this.selectedCatalogVersions.includes(version.uuid)
                            ).uuid
                        };
                    }
                );
            });

        this.modalManager.setDismissCallback(() => this.onCancel());

        this.modalManager.addButtons([
            {
                id: this.doneButtonId,
                label: 'se.confirmation.modal.done',
                style: ModalButtonStyle.Primary,
                action: ModalButtonAction.None,
                disabled: true, // initial state is disabled. See #updateSelection.
                callback: (): Observable<void> => from(this.onSave())
            },
            {
                id: 'cancel',
                label: 'se.confirmation.modal.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss,
                callback: (): Observable<void> => from(this.onCancel())
            }
        ]);
    }

    /**
     * Returns a callback that is called when the dropdown option is selected.
     *
     * `productCatalog.selectedItem` is a model.
     */
    public updateModel(): SelectOnChange {
        return (): void => {
            const selectedVersions = this.productCatalogs.map(
                (productCatalog: SelectAdaptedCatalog) => productCatalog.selectedItem
            );
            this.updateSelection(selectedVersions);
        };
    }

    private updateSelection(updatedSelectedVersions: string[]): void {
        // Enable "Done" button only when other versions than the pristine versions have been selected.
        if (!isEqual(updatedSelectedVersions, this.selectedCatalogVersions)) {
            this.updatedCatalogVersions = updatedSelectedVersions;
            this.modalManager.enableButton(this.doneButtonId);
        } else {
            this.modalManager.disableButton(this.doneButtonId);
        }
    }

    private onCancel(): Promise<void> {
        this.modalManager.close(null);

        return Promise.resolve();
    }

    private onSave(): Promise<void> {
        this.systemEventService.publishAsync(
            MULTI_PRODUCT_CATALOGS_UPDATED,
            this.updatedCatalogVersions
        );
        this.modalManager.close(null);

        return Promise.resolve();
    }
}
