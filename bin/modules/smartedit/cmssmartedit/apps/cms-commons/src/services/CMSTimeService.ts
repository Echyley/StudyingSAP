/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';

/**
 * Service for time management functionality.
 */
@Injectable()
export class CMSTimeService {
    constructor(private translate: TranslateService) {}

    /**
     * Give a time difference in milliseconds, this method returns a string that determines time in ago.
     *
     * Examples:
     * If the diff is less then 24 hours, the result is in hours eg: 17 hour(s) ago.
     * If the diff is more than a day, the result is in days, eg: 2 day(s) ago.
     *
     * @param timeDiff The time difference in milliseconds.
     */
    public getTimeAgo(timeDiff: number): string {
        const timeAgoInDays: number = Math.floor(moment.duration(timeDiff).asDays());
        const timeAgoInHours: number = Math.floor(moment.duration(timeDiff).asHours());

        if (timeAgoInDays >= 1) {
            return (
                timeAgoInDays +
                ' ' +
                this.translate.instant('se.cms.actionitem.page.workflow.action.started.days.ago')
            );
        } else if (timeAgoInHours >= 1) {
            return (
                timeAgoInHours +
                ' ' +
                this.translate.instant('se.cms.actionitem.page.workflow.action.started.hours.ago')
            );
        }
        return (
            '<1 ' +
            this.translate.instant('se.cms.actionitem.page.workflow.action.started.hours.ago')
        );
    }
}
