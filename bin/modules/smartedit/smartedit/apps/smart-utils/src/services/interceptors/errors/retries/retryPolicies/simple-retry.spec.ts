/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { SimpleRetry } from './simple-retry';

describe('simple retry policy service', () => {
    let simpleRetry: SimpleRetry;

    beforeEach(() => {
        simpleRetry = new SimpleRetry();
    });

    it('the calculateNextDelay should return a proper delay based on the given arguments', () => {
        // retryInterval, minBackoff
        // 2000+
        const delay = simpleRetry.calculateNextDelay(2000, 50);
        expect(delay < 3050 && delay > 2050).toBeTruthy();
    });

    it('the calculateNextDelay should work given no argument', () => {
        // retryInterval, minBackoff
        // 500+
        const delay = simpleRetry.calculateNextDelay();
        expect(delay < 1500 && delay >= 500).toBeTruthy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = simpleRetry.canRetry(3, 2);
        expect(delay).toBeFalsy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = simpleRetry.canRetry(6);
        expect(delay).toBeFalsy();
    });
});
