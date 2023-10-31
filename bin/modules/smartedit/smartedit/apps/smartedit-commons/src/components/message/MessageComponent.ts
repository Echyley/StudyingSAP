/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Input } from '@angular/core';

export enum MessageType {
    danger = 'danger',
    info = 'info',
    success = 'success',
    warning = 'warning'
}
/**
 *  This component provides contextual feedback messages for the user actions. To provide title and description for the se-essage
 *  use transcluded elements: se-message-title and se-message-description.
 *
 *  ### Example
 *
 *      <se-message>
 *          <div se-message-title>Title</div>
 *          <div se-message-description>Description</div>
 *      </se-message>
 */
@Component({
    selector: 'se-message',
    template: `
        <div
            [attr.message-id]="messageId"
            class="fd-message-strip se-y-message"
            role="alert"
            [ngClass]="classes">
            <div class="y-message-text">
                <div class="y-message-info-title">
                    <ng-content select="[se-message-title]"></ng-content>
                </div>
                <div class="y-message-info-description">
                    <ng-content select="[se-message-description]"></ng-content>
                </div>
            </div>
        </div>
    `
})
export class MessageComponent {
    /**
     * Id for the component
     */
    @Input() messageId: string;

    /**
     * The type of the component (danger, info, success, warning). Default: info
     */
    @Input() type: MessageType;

    public classes: string;

    ngOnInit(): void {
        this.messageId = this.messageId || 'y-message-default-id';
        switch (this.type) {
            case MessageType.danger:
                this.classes = 'fd-message-strip--error';
                break;
            case MessageType.info:
                this.classes = 'fd-message-strip--information';
                break;
            case MessageType.success:
                this.classes = 'fd-message-strip--success';
                break;
            case MessageType.warning:
                this.classes = 'fd-message-strip--warning';
                break;
            default:
                this.classes = 'fd-message-strip--information';
        }
    }
}
