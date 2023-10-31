import { EventEmitter } from '@angular/core';
import { SystemEventService } from '../../../services';
import './ActionableSearchItemComponent.scss';
export interface ActionableSearchItem {
    eventId?: string;
    actionText?: string;
}
export declare class ActionableSearchItemComponent {
    private systemEventService;
    search: string;
    eventId?: ActionableSearchItem['eventId'];
    actionText?: ActionableSearchItem['actionText'];
    actionButtonClick: EventEmitter<void>;
    private readonly defaultEventId;
    private readonly defaultActionText;
    constructor(systemEventService: SystemEventService);
    getActionText(): string;
    onButtonClick(): void;
}
