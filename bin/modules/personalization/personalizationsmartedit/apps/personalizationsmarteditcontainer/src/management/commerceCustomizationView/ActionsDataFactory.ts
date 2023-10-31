/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES } from 'personalizationcommons';
import { Action, BaseAction } from 'personalizationsmarteditcontainer/interfaces';

@Injectable()
export class ActionsDataFactory {
    public actions: Action[] = [];
    public removedActions: Action[] = [];

    public getActions(): Action[] {
        return this.actions;
    }

    public getRemovedActions(): Action[] {
        return this.removedActions;
    }

    public resetActions(): void {
        this.actions.length = 0;
    }

    public resetRemovedActions(): void {
        this.removedActions.length = 0;
    }

    public addAction(action: BaseAction, comparer: (p1, p2) => boolean): void {
        let exist = false;
        this.actions.forEach((wrapper: any) => {
            exist = exist || comparer(action, wrapper.action);
        });
        if (!exist) {
            let status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW;
            let removedIndex = -1;
            this.removedActions.forEach((wrapper: any, index: any) => {
                if (comparer(action, wrapper.action)) {
                    removedIndex = index;
                }
            });
            if (removedIndex >= 0) {
                // we found or action in delete queue
                status = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.OLD;
                this.removedActions.splice(removedIndex, 1);
            }
            this.actions.push({
                action,
                status
            });
        }
    }

    public isItemInSelectedActions(action: BaseAction, comparer: (p1, p2) => boolean): Action {
        return this.actions.find((wrapper: any) => comparer(action, wrapper.action));
    }
}
