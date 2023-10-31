import { Type } from '@angular/core';
export interface TriggerTab {
    id: string;
    title: string;
    templateUrl?: string;
    component?: Type<any>;
    isTriggerDefined(): boolean;
    isValidOrEmpty(): boolean;
}
