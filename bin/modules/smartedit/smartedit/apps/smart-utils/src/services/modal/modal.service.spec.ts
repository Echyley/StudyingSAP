/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { Type } from '@angular/core';
import { DialogService as FundamentalModalService } from '@fundamental-ngx/core';
import { ModalTemplateComponent } from '../../components';
import { ModalOpenConfig } from './i-modal.service';
import { ModalService } from './modal.service';

describe('Modal Service', () => {
    let modalService: ModalService;
    let fundamentalModalService: jasmine.SpyObj<FundamentalModalService>;

    beforeEach(() => {
        fundamentalModalService = jasmine.createSpyObj('fundamentalModalService', ['open']);
        modalService = new ModalService(fundamentalModalService);
    });

    it('opens standalone when no templateConfig is passed', () => {
        spyOn(modalService as any, 'openStandalone');

        modalService.open<null>({ component: {} as Type<any> });

        expect((modalService as any).openStandalone).toHaveBeenCalled();
    });

    it('opens with template when templateConfig is passed', () => {
        spyOn(modalService as any, 'openWithTemplate');

        modalService.open<null>({
            component: {} as Type<any>,
            templateConfig: { title: 'My title' }
        });

        expect((modalService as any).openWithTemplate).toHaveBeenCalled();
    });

    it('calls fundamentalModalService.open with correct params', () => {
        const config: ModalOpenConfig<{ myData: string }> = {
            component: {} as Type<any>,
            data: { myData: 'My Data' },
            templateConfig: { title: 'My title' }
        };
        modalService.open<{ myData: string }>(config);

        expect(fundamentalModalService.open).toHaveBeenCalledWith(ModalTemplateComponent, {
            data: {
                modalData: config.data,
                templateConfig: config.templateConfig,
                component: config.component
            }
        });
    });
});
