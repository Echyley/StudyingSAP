import { TranslateService } from '@ngx-translate/core';
/**
 * Service for time management functionality.
 */
export declare class CMSTimeService {
    private translate;
    constructor(translate: TranslateService);
    /**
     * Give a time difference in milliseconds, this method returns a string that determines time in ago.
     *
     * Examples:
     * If the diff is less then 24 hours, the result is in hours eg: 17 hour(s) ago.
     * If the diff is more than a day, the result is in days, eg: 2 day(s) ago.
     *
     * @param timeDiff The time difference in milliseconds.
     */
    getTimeAgo(timeDiff: number): string;
}
