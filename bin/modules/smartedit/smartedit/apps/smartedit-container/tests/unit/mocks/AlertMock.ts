/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

export class AlertMock {
    shown = false;

    show(): void {
        this.shown = true;
    }

    hide(): void {
        this.shown = false;
    }
}

export function getSpyMock(): AlertMock {
    const am = new AlertMock();
    spyOn(am, 'show').and.callThrough();
    spyOn(am, 'hide').and.callThrough();
    return am;
}
