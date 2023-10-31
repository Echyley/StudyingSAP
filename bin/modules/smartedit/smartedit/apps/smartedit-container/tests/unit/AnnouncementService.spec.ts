/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { LogService } from 'smarteditcommons';
import { AnnouncementService } from 'smarteditcontainer/services';

describe('AnnouncementService', () => {
    let announcementService: AnnouncementService;
    let logService: jasmine.SpyObj<LogService>;

    beforeEach(() => {
        logService = jasmine.createSpyObj('logServiceMock', ['warn']);
        TestBed.configureTestingModule({
            providers: [{ provide: LogService, useValue: logService }, AnnouncementService]
        });
        announcementService = TestBed.inject(AnnouncementService);
    });

    it('Should AnnouncementService initialize with empty announcements', (done: DoneFn) => {
        announcementService.getAnnouncements().subscribe((announcements) => {
            expect(announcements).toEqual([]);
            done();
        });
    });

    it('WHEN an announcement is displayed THEN it should disappear after a specified timeout', fakeAsync(() => {
        const timeout = 5000;
        const simpleAnnouncementConfig = {
            messageTitle: 'This is the message title',
            message: 'This is a simple announcement',
            timeout
        };
        announcementService.showAnnouncement(simpleAnnouncementConfig);
        let announcementsLength = null;
        announcementService.getAnnouncements().subscribe((announcements) => {
            announcementsLength = announcements.length;
        });
        expect(announcementsLength).toBe(1);
        tick(timeout);
        expect(announcementsLength).toBe(0);
    }));

    it('WHEN an announcement is displayed THEN it can be closed', fakeAsync(() => {
        const timeout = 5000;
        const simpleAnnouncementConfig = {
            messageTitle: 'This is the message title',
            message: 'This is a simple announcement',
            timeout
        };
        let announcementsLength = null;
        announcementService.getAnnouncements().subscribe((announcements) => {
            announcementsLength = announcements.length;
        });
        let announcementId = null;
        announcementService
            .showAnnouncement(simpleAnnouncementConfig)
            .then((id) => (announcementId = id));
        tick();
        expect(announcementsLength).toBe(1);
        announcementService.closeAnnouncement(announcementId);
        tick();
        expect(announcementsLength).toBe(0);
    }));
});
