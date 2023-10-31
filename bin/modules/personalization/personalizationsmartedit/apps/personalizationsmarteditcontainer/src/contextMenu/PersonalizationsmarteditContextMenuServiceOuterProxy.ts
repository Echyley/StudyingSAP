/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    CombinedViewSelectItem,
    ContextMenuDto,
    CustomizationAction,
    IPersonalizationsmarteditContextMenuServiceProxy,
    PersonalizationsmarteditMessageHandler
} from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import {
    GatewayProxied,
    IEditorModalService,
    IModalService,
    IRenderService,
    ModalButtonOptions,
    ModalButtonAction,
    ModalButtonStyle,
    LogService
} from 'smarteditcommons';
import { ContextMenuDeleteActionComponent } from './ContextMenuDeleteActionComponent';
import { PersonalizationsmarteditContextMenuAddEditActionComponent } from './PersonalizationsmarteditContextMenuAddEditActionComponent';

const MODAL_BUTTONS: ModalButtonOptions[] = [
    {
        id: 'confirmOk',
        label: 'personalization.modal.deleteaction.button.ok',
        style: ModalButtonStyle.Primary,
        action: ModalButtonAction.Close
    },
    {
        id: 'confirmCancel',
        label: 'personalization.modal.deleteaction.button.cancel',
        style: ModalButtonStyle.Default,
        action: ModalButtonAction.Dismiss
    }
];

@GatewayProxied('openDeleteAction', 'openAddAction', 'openEditAction', 'openEditComponentAction')
@Injectable()
export class PersonalizationsmarteditContextMenuServiceProxy extends IPersonalizationsmarteditContextMenuServiceProxy {
    constructor(
        private modalService: IModalService,
        private renderService: IRenderService,
        private editorModalService: IEditorModalService,
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
        private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler,
        private translateService: TranslateService,
        private logService: LogService
    ) {
        super();
    }

    public async openDeleteAction(config: ContextMenuDto): Promise<void> {
        if (!(await this.confirmDelete())) {
            return;
        }

        await this.deleteContext(config);

        const combinedView = this.personalizationsmarteditContextService.getCombinedView();

        if (combinedView.enabled) {
            try {
                const response = await this.personalizationsmarteditRestService.getActions(
                    config.selectedCustomizationCode,
                    config.selectedVariationCode,
                    config
                );

                if (combinedView.customize.selectedComponents) {
                    combinedView.customize.selectedComponents = this.removeItemFromCollection(
                        combinedView.customize.selectedComponents,
                        config.containerSourceId
                    );
                }

                combinedView.selectedItems = this.addActionsToVariation(
                    combinedView.selectedItems,
                    config,
                    response.actions
                );
                this.personalizationsmarteditContextService.setCombinedView(combinedView);
            } catch {
                this.personalizationsmarteditMessageHandler.sendError(
                    this.translateService.instant('personalization.error.gettingactions')
                );
            }
        } else {
            const customize = this.personalizationsmarteditContextService.getCustomize();

            customize.selectedComponents = this.removeItemFromCollection(
                customize.selectedComponents,
                config.containerSourceId
            );
            this.personalizationsmarteditContextService.setCustomize(customize);
        }
        this.renderService.renderSlots(config.slotsToRefresh);
    }

    public async openAddAction(config: ContextMenuDto): Promise<void> {
        const resultContainer = await this.modalService
            .open({
                component: PersonalizationsmarteditContextMenuAddEditActionComponent,
                data: config,
                templateConfig: {
                    title: 'personalization.modal.addaction.title',
                    isDismissButtonVisible: true
                },
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'yPersonalizationContextModal'
                }
            })
            .afterClosed.toPromise();

        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        if (combinedView.enabled) {
            combinedView.customize.selectedComponents.push(resultContainer);
            this.personalizationsmarteditContextService.setCombinedView(combinedView);
        } else {
            const customize = this.personalizationsmarteditContextService.getCustomize();
            customize.selectedComponents.push(resultContainer);
            this.personalizationsmarteditContextService.setCustomize(customize);
        }
        this.renderService.renderSlots(config.slotsToRefresh);
    }

    public async openEditAction(config: ContextMenuDto): Promise<void> {
        config.editEnabled = true;
        await this.modalService
            .open({
                component: PersonalizationsmarteditContextMenuAddEditActionComponent,
                data: config,
                templateConfig: {
                    title: 'personalization.modal.editaction.title',
                    isDismissButtonVisible: true
                },
                config: {
                    focusTrapped: false,
                    backdropClickCloseable: false,
                    dialogPanelClass: 'yPersonalizationContextModal'
                }
            })
            .afterClosed.toPromise();
        this.renderService.renderSlots(config.slotsToRefresh);
    }

    public openEditComponentAction(config: ContextMenuDto): void {
        this.editorModalService.open(config);
    }

    private async deleteContext(config: ContextMenuDto): Promise<void> {
        const filter = {
            catalog: config.catalog,
            catalogVersion: config.catalogVersion
        };
        try {
            await this.personalizationsmarteditRestService.deleteAction(
                config.selectedCustomizationCode,
                config.selectedVariationCode,
                config.actionId,
                filter
            );
        } catch (error) {
            this.logService.error('An error occurred while deleting context', error);
        }
    }

    private removeItemFromCollection(collection: string[], itemToRemove: string): string[] {
        return collection.filter((item) => item !== itemToRemove);
    }

    private addActionsToVariation(
        items: CombinedViewSelectItem[],
        config: ContextMenuDto,
        actions: CustomizationAction[]
    ): CombinedViewSelectItem[] {
        return items.map((item) => {
            if (
                item.customization.code === config.selectedCustomizationCode &&
                item.variation.code === config.selectedVariationCode
            ) {
                item.variation.actions = actions;
            }
            return item;
        });
    }

    private async confirmDelete(): Promise<boolean> {
        try {
            await this.modalService
                .open({
                    component: ContextMenuDeleteActionComponent,
                    config: {
                        dialogPanelClass: 'yFrontModal modal-md'
                    },
                    templateConfig: {
                        buttons: MODAL_BUTTONS,
                        title: 'personalization.modal.deleteaction.title'
                    }
                })
                .afterClosed.toPromise();
            return true;
        } catch {
            return false;
        }
    }
}
