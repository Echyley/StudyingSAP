import { Renderer2, RendererFactory2 } from '@angular/core';
import { ManageCustomizationViewManager } from 'personalizationsmarteditcontainer/management/manageCustomizationView';
import { promiseUtils } from 'smarteditcommons';

describe('ManageCustomizationViewManager', () => {
    let modalService: jasmine.SpyObj<any>;
    let personalizationsmarteditManager: ManageCustomizationViewManager;
    let rendererFactory: jasmine.SpyObj<RendererFactory2>;
    let renderer: jasmine.SpyObj<Renderer2>;

    beforeEach(() => {
        modalService = jasmine.createSpyObj('modalService', ['open']);

        modalService.open.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve();
            return deferred.promise;
        });

        renderer = jasmine.createSpyObj<Renderer2>('renderer', ['addClass', 'removeClass']);

        rendererFactory = jasmine.createSpyObj<RendererFactory2>('rendererFactory', [
            'createRenderer'
        ]);

        rendererFactory.createRenderer.and.returnValue(renderer);

        personalizationsmarteditManager = new ManageCustomizationViewManager(modalService);
    });

    describe('openCreateCustomizationModal', () => {
        it('GIVEN that modal for creating customization is open, proper functions should be called', () => {
            personalizationsmarteditManager.openCreateCustomizationModal();
            expect(modalService.open).toHaveBeenCalled();
        });
    });

    describe('openEditCustomizationModal', () => {
        it('GIVEN that modal for editing customization is open, proper functions should be called', () => {
            personalizationsmarteditManager.openEditCustomizationModal('', '');
            expect(modalService.open).toHaveBeenCalled();
        });
    });
});
