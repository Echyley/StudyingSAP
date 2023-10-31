/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Type } from '@angular/core';
export interface TriggerTab {
    id: string;
    title: string;
    templateUrl?: string;
    component?: Type<any>;

    isTriggerDefined(): boolean; // Function returns 'true' if trigger is created and correct, otherwise 'false'
    isValidOrEmpty(): boolean;
    // Function returns 'true' if trigger is correct or is empty, otherwise 'false'
    // In multiple trigger environment all trigger tabs must be valid or empty, and at least one must have defined trigger
}
