/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import {
    CMSLinkItem,
    SingleActiveCatalogAwareItemSelectorComponent
} from 'cmssmarteditcontainer/components/genericEditor/singleActiveCatalogAwareSelector';
import {
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    IBaseCatalog,
    ICatalogService,
    GenericEditorField
} from 'smarteditcommons';
import { FakeGenericEditorDropdownComponent } from '../mockComponents/FakeGenericEditorDropdownComponent';
import { MockL10nPipe } from '../mockComponents/MockL10nPipe';

describe('SingleActiveCatalogAwareItemSelectorComponent', () => {
    let component: SingleActiveCatalogAwareItemSelectorComponent;
    let fixture: ComponentFixture<SingleActiveCatalogAwareItemSelectorComponent>;
    let nativeElement: HTMLElement;

    let catalogServiceStub: jasmine.SpyObj<ICatalogService>;

    const injectedData = {
        field: {
            i18nKey: 'catalogName',
            paged: false,
            editable: false,
            idAttribute: '',
            labelAttributes: [],
            dependsOn: '',
            propertyType: '',
            cmsStructureType: 'SingleOnlineProductSelector'
        },
        model: {
            productCatalog: ''
        },
        qualifier: '',
        editor: {
            form: {
                pristine: {
                    productCatalog: ''
                }
            }
        }
    } as GenericEditorWidgetData<CMSLinkItem>;
    const catalogs = [
        { catalogId: 'catalogId1', name: { en: 'catalog 1' } },
        { catalogId: 'catalogId2', name: { en: 'catalog 2' } },
        { catalogId: 'catalogId3', name: { en: 'catalog 3' } }
    ] as unknown as IBaseCatalog[];

    async function createSingleActiveCatalogAwareItemSelectorComponentContext() {
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [
                SingleActiveCatalogAwareItemSelectorComponent,
                FakeGenericEditorDropdownComponent,
                MockL10nPipe
            ],
            providers: [
                { provide: GENERIC_EDITOR_WIDGET_DATA, useValue: injectedData },
                { provide: ICatalogService, useValue: catalogServiceStub }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .overrideComponent(SingleActiveCatalogAwareItemSelectorComponent, {
                set: {
                    templateUrl:
                        '/base/src/components/genericEditor/singleActiveCatalogAwareSelector/SingleActiveCatalogAwareItemSelectorComponent.html',
                    styleUrls: [
                        '/base/src/components/genericEditor/singleActiveCatalogAwareSelector/SingleActiveCatalogAwareItemSelectorComponent.scss'
                    ]
                }
            })
            .compileComponents();
        fixture = TestBed.createComponent(SingleActiveCatalogAwareItemSelectorComponent);
        component = fixture.componentInstance;

        fixture.detectChanges();
        nativeElement = fixture.debugElement.nativeElement;
    }

    describe('SingleActiveCatalogAwareItemSelectorComponent For only one Product Catalog', () => {
        beforeEach(() => {
            catalogServiceStub = jasmine.createSpyObj<ICatalogService>('ICatalogService', [
                'getProductCatalogsBySiteKey'
            ]);
            catalogServiceStub.getProductCatalogsBySiteKey.and.returnValue(
                Promise.resolve(catalogs.slice(0, 1))
            );
        });

        it('Should create SingleActiveCatalogAwareItemSelector Component correctly', async () => {
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            expect(component).toBeDefined();
        });

        it('WHEN initialized THEN it should set dropdown attributes', async () => {
            injectedData.field.cmsStructureType = 'SingleOnlineProductSelector';
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(component.propertyType).toEqual('product');
            expect(component.productCatalogField).toEqual({
                idAttribute: 'catalogId',
                labelAttributes: ['name'],
                editable: false,
                propertyType: 'productCatalog'
            } as GenericEditorField);
            expect(component.mainDropDownI18nKey).toEqual('catalogName');

            expect(component.field).toEqual({
                paged: true,
                editable: false,
                idAttribute: 'uid',
                labelAttributes: ['name'],
                dependsOn: 'productCatalog',
                propertyType: 'product',
                cmsStructureType: 'SingleOnlineProductSelector'
            } as GenericEditorField);
        });

        it('WHEN initialized and there is only one catalog found THEN it should set catalogs, model - product catalog and catalog name', async () => {
            catalogServiceStub.getProductCatalogsBySiteKey.and.returnValue(
                Promise.resolve(catalogs.slice(0, 1))
            );
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(component.catalogs).toEqual([catalogs[0]]);
            expect(component.model.productCatalog).toEqual('catalogId1');
            expect(component.editor.form.pristine.productCatalog).toEqual('catalogId1');
            expect(component.catalogName).toEqual({ en: 'catalog 1' });
        });

        it('WHEN there is only one product catalog THEN Product Catalog selector is not present AND Product catalog name is displayed AND Product field is populated', async () => {
            injectedData.field.cmsStructureType = 'SingleOnlineProductSelector';
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(nativeElement.querySelector('#se-catalog-selector-dropdown')).toBeNull();
            expect(nativeElement.querySelector('#labelForProductCatalog').innerHTML).toEqual(
                'catalog 1'
            );
            expect(nativeElement.querySelector('#labelForItem').innerHTML).toEqual('catalogName');
            expect(component.propertyType).toEqual('product');
        });

        it('WHEN there is only one product catalog THEN Product Catalog selector is not present AND Product catalog name is displayed AND Category field is populated', async () => {
            injectedData.field.cmsStructureType = 'SingleOnlineCategorySelector';
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(nativeElement.querySelector('#se-catalog-selector-dropdown')).toBeNull();
            expect(nativeElement.querySelector('#labelForProductCatalog').innerHTML).toEqual(
                'catalog 1'
            );
            expect(nativeElement.querySelector('#labelForItem').innerHTML).toEqual('catalogName');
            expect(component.propertyType).toEqual('category');
        });
    });

    describe('SingleActiveCatalogAwareItemSelectorComponent For multi Product Catalog', () => {
        beforeEach(() => {
            catalogServiceStub = jasmine.createSpyObj<ICatalogService>('ICatalogService', [
                'getProductCatalogsBySiteKey'
            ]);
            catalogServiceStub.getProductCatalogsBySiteKey.and.returnValue(
                Promise.resolve(catalogs)
            );
        });

        it('Should create SingleActiveCatalogAwareItemSelector Component for multi Product Catalog correctly', async () => {
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            expect(component).toBeDefined();
        });

        it('WHEN initialized and there is more than one catalog found THEN it should set catalogs', async () => {
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            await component.ngOnInit();

            expect(component.catalogs).toEqual(catalogs);
            expect(component.model.productCatalog).toEqual('');
            expect(component.editor.form.pristine.productCatalog).toEqual('');
            expect(component.catalogName).toEqual({});
        });

        it('WHEN there are more than one product catalog THEN Product Catalog AND Product fields are visible AND Product Catalog is not selected AND Product is not populated', async () => {
            injectedData.field.cmsStructureType = 'SingleOnlineProductSelector';
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(nativeElement.querySelector('#se-catalog-selector-dropdown')).toBeDefined();
            expect(nativeElement.querySelector('#labelForProductCatalog')).toBeNull();
            expect(nativeElement.querySelector('#labelForItem').innerHTML).toEqual('catalogName');
            expect(component.propertyType).toEqual('product');
        });

        it('WHEN there are more than one product catalog THEN Product Catalog AND Category fields are visible AND Product Catalog is not selected AND Category is not populated', async () => {
            injectedData.field.cmsStructureType = 'SingleOnlineCategorySelector';
            await createSingleActiveCatalogAwareItemSelectorComponentContext();
            fixture.detectChanges();

            expect(nativeElement.querySelector('#se-catalog-selector-dropdown')).toBeDefined();
            expect(nativeElement.querySelector('#labelForProductCatalog')).toBeNull();
            expect(nativeElement.querySelector('#labelForItem').innerHTML).toEqual('catalogName');
            expect(component.propertyType).toEqual('category');
        });
    });
});
