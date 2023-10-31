/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { ExponentialRetry } from './exponential-retry';

describe('exponential retry policy service', () => {
    let exponentialRetry: ExponentialRetry;
    beforeEach(() => {
        exponentialRetry = new ExponentialRetry();
    });

    it('the calculateNextDelay should return a proper delay based on the given arguments', () => {
        // attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        const delay = exponentialRetry.calculateNextDelay(2, 65000, 5);
        expect(delay < 5000 && delay >= 4000).toBeTruthy();
    });

    it('the calculateNextDelay should fall back to maxBackoff if the generated delay is more than the maxBackoff', () => {
        // attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        const delay = exponentialRetry.calculateNextDelay(2, 3000, 5);
        expect(delay).toBe(3000);
    });

    it('the calculateNextDelay should work given only an attemptCount', () => {
        // attemptCount, maxBackoff, minBackoff
        // 2+, 4+, 8+, 16+, 32+, 64+
        const delay = exponentialRetry.calculateNextDelay(2);
        expect(delay < 5000 && delay >= 4000).toBeTruthy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = exponentialRetry.canRetry(3, 2);
        expect(delay).toBeFalsy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = exponentialRetry.canRetry(6);
        expect(delay).toBeFalsy();
    });
});
