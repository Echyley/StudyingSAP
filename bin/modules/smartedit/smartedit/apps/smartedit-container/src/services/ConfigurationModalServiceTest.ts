/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IModalService } from 'smarteditcommons';

import { ConfigurationModalComponent } from '../components/generalConfiguration/ConfigurationModalComponent';
import { ConfigurationModalService } from './ConfigurationModalService';

describe('Configuration Modal Service', () => {
    let modalService: jasmine.SpyObj<IModalService>;
    let service: ConfigurationModalService;

    beforeEach(() => {
        modalService = jasmine.createSpyObj('modalService', ['open']);

        service = new ConfigurationModalService(modalService);
    });

    it('calls ModalService.open with correct data', () => {
        const expected = {
            templateConfig: {
                title: 'se.modal.administration.configuration.edit.title'
            },
            component: ConfigurationModalComponent,
            config: {
                focusTrapped: false,
                backdropClickCloseable: false
            }
        };

        service.editConfiguration();

        expect(modalService.open).toHaveBeenCalledWith(expected);
    });
});
