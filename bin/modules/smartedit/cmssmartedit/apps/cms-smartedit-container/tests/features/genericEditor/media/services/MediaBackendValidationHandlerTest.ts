/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { MediaBackendValidationHandler } from 'cmssmarteditcontainer/components/genericEditor/media/services';

describe('MediaBackendValidationHandler', () => {
    const RESPONSE = {
        error: {
            errors: [
                {
                    type: 'ModelError',
                    message: ''
                },
                {
                    type: 'ValidationError',
                    message: 'no subject provided'
                },
                {
                    type: 'ValidationError',
                    subject: 'someSubject',
                    message: 'some message'
                }
            ]
        }
    };

    let service: MediaBackendValidationHandler;
    beforeEach(() => {
        service = new MediaBackendValidationHandler();
    });

    describe('handleResponse', () => {
        it('should transform a response into a list of validation errors filtering on type ValidationError', () => {
            expect(service.handleResponse(RESPONSE)).toEqual([
                {
                    subject: 'someSubject',
                    message: 'some message'
                }
            ]);
        });

        it('should append the validation errors to an errors context list if one is provided', () => {
            const errorsContext = [];
            service.handleResponse(RESPONSE, errorsContext);

            expect(errorsContext).toEqual([
                {
                    subject: 'someSubject',
                    message: 'some message'
                }
            ]);
        });
    });
});
