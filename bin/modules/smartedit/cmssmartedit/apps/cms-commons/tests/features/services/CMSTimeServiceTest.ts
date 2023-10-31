/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { TranslateService } from '@ngx-translate/core';
import { CMSTimeService } from 'cmscommons';

describe('CMS Time Service', () => {
    const HOUR_IN_MS = 60 * 60 * 1000;
    const DAY_IN_MS = 24 * HOUR_IN_MS;
    let service: CMSTimeService;
    let translate: jasmine.SpyObj<TranslateService>;

    beforeEach(() => {
        translate = jasmine.createSpyObj('translate', ['instant']);
        service = new CMSTimeService(translate);
    });

    it('GIVEN time diff equal one day WHEN method is called THEN it outputs translated diff in days ago', () => {
        translate.instant.and.returnValue('days');

        expect(service.getTimeAgo(DAY_IN_MS)).toEqual('1 days');
        expect(translate.instant).toHaveBeenCalledWith(
            'se.cms.actionitem.page.workflow.action.started.days.ago'
        );
    });

    it('GIVEN time diff greater than one day WHEN method is called THEN it outputs translated diff in days ago', () => {
        translate.instant.and.returnValue('days');

        expect(service.getTimeAgo(DAY_IN_MS * 3)).toEqual('3 days');
        expect(translate.instant).toHaveBeenCalledWith(
            'se.cms.actionitem.page.workflow.action.started.days.ago'
        );
    });

    it('GIVEN time diff equal one hour WHEN method is called THEN it outputs translated diff in days ago', () => {
        translate.instant.and.returnValue('hours');

        expect(service.getTimeAgo(HOUR_IN_MS)).toEqual('1 hours');
        expect(translate.instant).toHaveBeenCalledWith(
            'se.cms.actionitem.page.workflow.action.started.hours.ago'
        );
    });

    it('GIVEN time diff greater than one hour WHEN method is called THEN it outputs translated diff in days ago', () => {
        translate.instant.and.returnValue('hours');

        expect(service.getTimeAgo(HOUR_IN_MS * 5)).toEqual('5 hours');
        expect(translate.instant).toHaveBeenCalledWith(
            'se.cms.actionitem.page.workflow.action.started.hours.ago'
        );
    });

    it('GIVEN time diff lower then one day WHEN method is called THEN it outputs translated diff in days ago', () => {
        translate.instant.and.returnValue('hours');

        expect(service.getTimeAgo(HOUR_IN_MS / 5)).toEqual('<1 hours');
        expect(translate.instant).toHaveBeenCalledWith(
            'se.cms.actionitem.page.workflow.action.started.hours.ago'
        );
    });
});
