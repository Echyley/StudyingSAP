import { OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { SystemEventService } from '../../../services';
import { EventMessageData } from './EventMessageData';
/**
 * Wrapper around se-message, used to display or hide the message based on events sent through the {@link SystemEventService}.
 */
export declare class EventMessageComponent implements OnInit, OnChanges, OnDestroy {
    private systemEventService;
    type: string;
    title: string;
    description: string;
    showEvent: string;
    hideEvent: string;
    showToStart: string | boolean;
    show: boolean;
    private unregisterShowEventHandler;
    private unregisterHideEventHandler;
    constructor(systemEventService: SystemEventService);
    ngOnInit(): void;
    ngOnDestroy(): void;
    ngOnChanges(changes: SimpleChanges): void;
    showDescription(): boolean;
    showTitle(): boolean;
    showEventHandler(eventId: string, eventData: EventMessageData): void;
    private removeHideEventHandler;
    private removeShowEventHandler;
    private watchShowEventChange;
    private watchHideEventChange;
}
