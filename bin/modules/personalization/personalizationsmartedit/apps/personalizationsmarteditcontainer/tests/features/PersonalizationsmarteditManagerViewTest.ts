import { ManagerViewService } from 'personalizationsmarteditcontainer/management/managerView/ManagerViewService';
import { promiseUtils } from 'smarteditcommons';

describe('PersonalizationsmarteditManagerView', () => {
    let modalService: jasmine.SpyObj<any>;
    let managerView: ManagerViewService;

    // === SETUP ===
    beforeEach(() => {
        modalService = jasmine.createSpyObj('modalService', ['open']);
        modalService.open.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve();
            return deferred.promise;
        });

        managerView = new ManagerViewService(modalService);
    });

    describe('openManagerAction', () => {
        it('should be defined', () => {
            expect(managerView.openManagerAction).toBeDefined();
        });

        it('after called it is calling proper services', () => {
            managerView.openManagerAction();
            //TODO: private modalService, use any to fix temporarily
            expect((managerView as any).modalService.open).toHaveBeenCalled();
        });
    });
});
