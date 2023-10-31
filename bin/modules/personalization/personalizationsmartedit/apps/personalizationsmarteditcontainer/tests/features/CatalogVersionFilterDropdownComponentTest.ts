import { CatalogVersionFilterDropdownComponent } from 'personalizationsmarteditcontainer/commonComponents/CatalogVersionFilterDropdownComponent';
import { Observable } from 'rxjs';
import { L10nPipe, promiseUtils } from 'smarteditcommons';

describe('CatalogVersionFilterDropdownComponent', () => {
    const mockCatalog1 = {
        catalogId: 'electronicsContentCatalog',
        catalogName: {
            en: 'Electronics Content Catalog',
            de: 'Elektronikkatalog'
        },
        catalogVersionId: 'Online',
        id: 'electronicsContentCatalog/Online',
        isCurrentCatalog: false
    };
    const mockCatalog2 = {
        catalogId: 'electronics-euContentCatalog',
        catalogName: {
            en: 'Electronics Content Catalog EU',
            de: 'Elektronikkatalog EU'
        },
        catalogVersionId: 'Online',
        id: 'electronics-euContentCatalog/Online',
        isCurrentCatalog: false
    };
    const mockCatalog3 = {
        catalogId: 'electronics-ukContentCatalog',
        catalogName: {
            en: 'Electronics Content Catalog UK',
            de: 'Elektronikkatalog UK'
        },
        catalogVersionId: 'Staged',
        id: 'electronics-ukContentCatalog/Staged',
        isCurrentCatalog: true
    };

    const mockExperienceData = {
        seExperienceData: {
            catalogDescriptor: {
                catalogVersionUuid: 'mockUuid'
            }
        }
    };

    let componentMenuService: jasmine.SpyObj<any>;
    let personalizationsmarteditContextService: jasmine.SpyObj<any>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let catalogVersionFilterDropdownComponent: CatalogVersionFilterDropdownComponent;

    beforeEach(() => {
        componentMenuService = jasmine.createSpyObj('componentMenuService', [
            'getValidContentCatalogVersions',
            'getInitialCatalogVersion'
        ]);
        componentMenuService.getValidContentCatalogVersions.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve([mockCatalog1, mockCatalog2, mockCatalog3]);
            return deferred.promise;
        });
        componentMenuService.getInitialCatalogVersion.and.callFake(() => {
            const deferred = promiseUtils.defer();
            deferred.resolve(mockCatalog3);
            return deferred.promise;
        });
        personalizationsmarteditContextService = jasmine.createSpyObj(
            'personalizationsmarteditContextService',
            ['getSeData']
        );
        personalizationsmarteditContextService.getSeData.and.callFake(() => {
            return mockExperienceData;
        });
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);
        l10nPipe.transform.and.callFake(
            (localizedMap) =>
                new Observable((subscriber) => {
                    // eslint-disable-next-line @typescript-eslint/dot-notation
                    subscriber.next(localizedMap['en']);
                })
        );

        catalogVersionFilterDropdownComponent = new CatalogVersionFilterDropdownComponent(
            componentMenuService,
            personalizationsmarteditContextService,
            l10nPipe
        );
    });

    describe('Component API', () => {
        it('should have proper api before initialized', () => {
            expect(catalogVersionFilterDropdownComponent.items).not.toBeDefined();
            expect(catalogVersionFilterDropdownComponent.selectedId).not.toBeDefined();
            expect(catalogVersionFilterDropdownComponent.onSelectCallback).toBeDefined();
            expect(catalogVersionFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(catalogVersionFilterDropdownComponent.onChange).toBeDefined();
            expect(catalogVersionFilterDropdownComponent.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', async () => {
            await catalogVersionFilterDropdownComponent.ngOnInit();
            expect(catalogVersionFilterDropdownComponent.items.length).toBe(3);
            expect(catalogVersionFilterDropdownComponent.selectedId).toBe(
                catalogVersionFilterDropdownComponent.items[2].id
            );
            expect(catalogVersionFilterDropdownComponent.items[2].catalogName).toBe(
                'Electronics Content Catalog UK'
            );
            expect(catalogVersionFilterDropdownComponent.ngOnInit).toBeDefined();
            expect(catalogVersionFilterDropdownComponent.onChange).toBeDefined();
            expect(catalogVersionFilterDropdownComponent.fetchStrategy).toBeDefined();
        });
    });
});
