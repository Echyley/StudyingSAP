/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { MessageComponent, MessageType } from 'smarteditcommons';

describe('MessageComponent', () => {
    let component: MessageComponent;
    beforeEach(() => {
        component = new MessageComponent();
    });

    it('GIVEN message id was not provided THEN the it sets the default message id', () => {
        component.messageId = undefined;

        component.ngOnInit();

        expect(component.messageId).toBe('y-message-default-id');
    });

    it('GIVEN messageId was provided THEN it sets it properly', () => {
        const messageId = 'test-message-id';
        component.messageId = messageId;

        component.ngOnInit();

        expect(component.messageId).toBe('test-message-id');
    });

    it('GIVEN info type was provided THEN it sets a proper css class', () => {
        component.type = MessageType.success;

        component.ngOnInit();

        expect(component.classes).toBe('fd-message-strip--success');
    });

    it('GIVEN info type was not provided THEN it sets the default css class', () => {
        component.type = undefined;

        component.ngOnInit();

        expect(component.classes).toBe('fd-message-strip--information');
    });
});
