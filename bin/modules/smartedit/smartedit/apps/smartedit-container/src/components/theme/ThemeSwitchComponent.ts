/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, ChangeDetectionStrategy, ViewEncapsulation, OnInit } from '@angular/core';
import { GridListItemOutputEvent } from '@fundamental-ngx/core/grid-list';
import { from, Observable } from 'rxjs';
import {
    ThemesService,
    ModalButtonAction,
    ModalButtonStyle,
    ModalManagerService,
    ConfirmationModalConfig,
    IConfirmationModalService
} from 'smarteditcommons';
import { ThemeSwitchService } from '../../services/theme/ThemeSwitchService';
const imgUrl = 'static-resources/images/';
interface GridListItem {
    id: string;
    url: string;
    type?: string;
    toolbarText?: string;
    selected?: boolean;
    counter?: number;
}

@Component({
    selector: 'fd-theme-switch',
    templateUrl: './themeSwitchComponent.html',
    styleUrls: ['./ThemeSwitch.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None
})
export class ThemeSwitchComponent implements OnInit {
    public themes: any;
    public layoutPattern: string;
    private readonly list: GridListItem[];

    constructor(
        private readonly _themesService: ThemesService,
        private readonly _themeSwitchService: ThemeSwitchService,
        public modalManager: ModalManagerService,
        private readonly confirmationModalService: IConfirmationModalService
    ) {
        this.themes = this._themesService.getThemes();
        this.list = this.themes.themes.map((theme) => ({
            id: theme.code,
            toolbarText: theme.name,
            url: `${imgUrl}${theme.code}.png`,
            selected: this._themeSwitchService.selectedTheme === theme.code
        }));
        this.layoutPattern = 'XL3-L3-M2-S1';
    }

    ngOnInit(): void {
        this.modalManager.addButtons([
            {
                id: 'save',
                style: ModalButtonStyle.Primary,
                label: 'se.cms.component.confirmation.modal.save',
                callback: (): Observable<void> => from(this.onSave()),
                disabled: true
            },
            {
                id: 'cancel',
                label: 'se.cms.component.confirmation.modal.cancel',
                style: ModalButtonStyle.Default,
                action: ModalButtonAction.Dismiss,
                callback: (): Observable<void> => from(this.onCancel())
            }
        ]);
    }

    onSelectionChange(event: GridListItemOutputEvent<number>): void {
        if (!(event as any).removed.length) {
            return;
        }
        if ((event as any).selection[0] !== this._themeSwitchService.getThemeSession()) {
            this.modalManager.enableButton('save');
        } else {
            this.modalManager.disableButton('save');
        }
        this._themeSwitchService.selectTheme((event as any).selection[0]);
    }

    private onCancel(): Promise<void> {
        const buttonsSource = this.modalManager.getButtons();
        const saveBtnIndex = 0;
        let dirty: boolean;
        buttonsSource.forEach((buttons) => {
            dirty = !buttons[saveBtnIndex].disabled;
        });

        const confirmationData: ConfirmationModalConfig = {
            description: 'se.editor.cancel.confirm'
        };
        if (!dirty) {
            this.modalManager.close(null);
            return Promise.resolve();
        }
        return this.confirmationModalService.confirm(confirmationData).then(() => {
            if (
                this._themeSwitchService.getThemeSession() !==
                this._themeSwitchService.selectedTheme
            ) {
                this._themeSwitchService.selectTheme(this._themeSwitchService.getThemeSession());
            }
            this.modalManager.close(null);
        });
    }

    private onSave(): Promise<void> {
        if (this._themeSwitchService.getThemeSession() !== this._themeSwitchService.selectedTheme) {
            this._themeSwitchService.saveTheme();
        }
        this.modalManager.close(null);
        return Promise.resolve();
    }
}
