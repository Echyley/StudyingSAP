/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable, NgZone } from '@angular/core';
import { Timer } from './Timer';

@Injectable()
export class TimerService {
    constructor(private readonly ngZone: NgZone) {}

    createTimer(callback: () => void, duration: number): Timer {
        return new Timer(this.ngZone, callback, duration);
    }
}
