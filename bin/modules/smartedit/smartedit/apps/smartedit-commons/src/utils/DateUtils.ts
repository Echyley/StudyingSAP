/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import moment from 'moment';

import { DATE_CONSTANTS } from './smarteditconstants';

/**
 * Provides a list of useful methods used for date manipulation.
 */
export class DateUtils {
    /**
     * Formats provided dateTime as utc.
     *
     * @param dateTime Date Time to format in UTC.
     * @returns Formatted string.
     */
    formatDateAsUtc(dateTime: any): string {
        return moment(dateTime).utc().format(DATE_CONSTANTS.MOMENT_ISO);
    }
}

export const dateUtils = new DateUtils();
