/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { GenericEditorFieldMessage } from '../types';
import { SeValidationMessageParser } from './SeValidationMessageParser';

describe('seValidationMessageParser', () => {
    let seValidationMessageParser: SeValidationMessageParser;

    beforeEach(() => {
        seValidationMessageParser = new SeValidationMessageParser();
    });
    describe('parse', () => {
        const MESSAGE =
            'Some validation error occurred. FirstKey: [SomeValue]. SecondKey: [SomeOtherValue].';
        let parsedError: GenericEditorFieldMessage;

        beforeEach(() => {
            parsedError = seValidationMessageParser.parse(MESSAGE);
        });

        it('should parse the details from the message and strip the message', () => {
            expect(parsedError.message).toBe('Some validation error occurred.');
            expect(parsedError.firstkey).toBe('SomeValue');
            expect(parsedError.secondkey).toBe('SomeOtherValue');
        });
    });
});
