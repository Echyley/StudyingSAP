/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { LinearRetry } from './linear-retry';

describe('linear retry policy service', () => {
    let linearRetry: LinearRetry;

    beforeEach(() => {
        linearRetry = new LinearRetry();
    });

    it('the calculateNextDelay should return a proper delay based on the given arguments', () => {
        // attemptCount, retryInterval, maxBackoff, minBackoff
        // 2000+, 4000+, 6000+, 8000+, 10000+, 12000+
        const firstDelay = linearRetry.calculateNextDelay(2, 2000, 65000, 5);
        expect(firstDelay < 5000 && firstDelay > 4000).toBeTruthy();
        const secondDelay = linearRetry.calculateNextDelay(4, 2000, 65000, 5);
        expect(secondDelay < 9000 && secondDelay > 8000).toBeTruthy();
    });

    it('the calculateNextDelay should fall back to maxBackoff if the generated delay is more than the maxBackoff', () => {
        // attemptCount, retryInterval, maxBackoff, minBackoff
        // 2000+, 4000+, 6000+, 8000+, 10000+, 12000+
        const delay = linearRetry.calculateNextDelay(12, 2000, 1234);
        expect(delay).toBe(1234);
    });

    it('the calculateNextDelay should work given only an attemptCount', () => {
        // attemptCount, retryInterval, maxBackoff, minBackoff
        // 500+, 1000+, 1500+, 2000+, 2500+, 3000+
        const delay = linearRetry.calculateNextDelay(5);
        expect(delay < 3000 && delay >= 2500).toBeTruthy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = linearRetry.canRetry(3, 2);
        expect(delay).toBeFalsy();
    });

    it('the canRetry should return false the attemptCount is larger than the max', () => {
        // attemptCount, maxAttempt
        const delay = linearRetry.canRetry(6);
        expect(delay).toBeFalsy();
    });
});
