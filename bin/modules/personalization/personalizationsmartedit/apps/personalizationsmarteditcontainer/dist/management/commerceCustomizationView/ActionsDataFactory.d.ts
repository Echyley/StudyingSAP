import { Action, BaseAction } from 'personalizationsmarteditcontainer/interfaces';
export declare class ActionsDataFactory {
    actions: Action[];
    removedActions: Action[];
    getActions(): Action[];
    getRemovedActions(): Action[];
    resetActions(): void;
    resetRemovedActions(): void;
    addAction(action: BaseAction, comparer: (p1: any, p2: any) => boolean): void;
    isItemInSelectedActions(action: BaseAction, comparer: (p1: any, p2: any) => boolean): Action;
}
