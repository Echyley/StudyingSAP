/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input, NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule } from '@ngx-translate/core';

import { IAnnouncementService } from 'smarteditcommons';
import { AnnouncementBoardComponent } from 'smarteditcontainer/components';
import { AnnouncementService, IAnnouncement } from 'smarteditcontainer/services';

import { asyncData } from 'testhelpers';

@Pipe({ name: 'seReverse' })
class ReversePipeMock implements PipeTransform {
    transform<T>(value: T[]): T[] | undefined {
        return value;
    }
}

@Component({
    selector: 'se-announcement',
    template: ` <div class="test-announcement">{{ announcement.message }}</div> `
})
class AnnouncementComponentMock {
    @Input() announcement: IAnnouncement;
}

const announcementMocks: IAnnouncement[] = [
    {
        id: 'announcement-0',
        messageTitle: 'This is the message title',
        message: 'This is a simple announcement',
        timeout: 5000
    },
    {
        id: 'announcement-1',
        message: 'This is a non closeable announcement',
        closeable: false,
        timeout: 5000
    }
];

describe('AnnouncementBoardComponent', () => {
    let component: AnnouncementBoardComponent;
    let fixture: ComponentFixture<AnnouncementBoardComponent>;
    let nativeElement: HTMLElement;
    let announcementService: jasmine.SpyObj<AnnouncementService>;

    beforeEach(waitForAsync(() => {
        announcementService = jasmine.createSpyObj('announcementService', ['getAnnouncements']);
        announcementService.getAnnouncements.and.returnValue(asyncData(announcementMocks));
        TestBed.configureTestingModule({
            imports: [BrowserAnimationsModule, TranslateModule.forRoot()],
            declarations: [AnnouncementBoardComponent, ReversePipeMock, AnnouncementComponentMock],
            providers: [{ provide: IAnnouncementService, useValue: announcementService }],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(AnnouncementBoardComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/announcements/AnnouncementBoardComponent.html'
                }
            })
            .compileComponents()
            .then(() => {
                fixture = TestBed.createComponent(AnnouncementBoardComponent);
                component = fixture.componentInstance;
                nativeElement = fixture.debugElement.nativeElement;
                fixture.detectChanges();
            });
    }));

    it('Should create Announcement Board Component correctly', () => {
        expect(component).toBeTruthy();
    });

    it('WHEN multiple announcements are published THEN all are displayed', fakeAsync(() => {
        expect(nativeElement.querySelectorAll('.test-announcement').length).toBe(0);
        tick();
        fixture.detectChanges();
        expect(nativeElement.querySelectorAll('.test-announcement').length).toBe(
            announcementMocks.length
        );
    }));
});
