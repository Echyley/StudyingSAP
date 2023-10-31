/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
// eslint-disable-next-line max-classes-per-file
import {
    Component,
    ElementRef,
    NO_ERRORS_SCHEMA,
    SimpleChange,
    ViewContainerRef
} from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { DynamicForm, DynamicInput, FormGrouping, Payload } from '@smart/utils';
import { BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';
import {
    GenericEditorAPI,
    GenericEditorComponent,
    GenericEditorFactoryService,
    GenericEditorSchema,
    GenericEditorTab,
    IGenericEditor,
    IGenericEditorConstructor,
    IWaitDialogService
} from 'smarteditcommons';
import { FormBuilderDirective } from './components/formBuilder/FormBuilderDirective';
import { GenericEditorRootTabsComponent } from './components/rootTabs/GenericEditorRootTabsComponent';
import { createApi } from './GenericEditorAPI';
import { GenericEditorState } from './models/GenericEditorState';
import { GenericEditorStateBuilderService } from './services/GenericEditorStateBuilderService';
@Component({
    selector: 'se-ge-root-tabs',
    template: ''
})
class FakeGenericEditorRootTabsComponent implements Partial<GenericEditorRootTabsComponent> {
    @DynamicForm()
    form: FormGrouping;

    @DynamicInput()
    tabs: GenericEditorTab[];
}

describe('GenericEditorComponent and FormBuilderDirective: with fake child component GenericEditorRootTabsComponent', () => {
    // for GenericEditorComponent
    let fixture: ComponentFixture<GenericEditorComponent>;
    let component: GenericEditorComponent;
    let genericEditorFactoryService: jasmine.SpyObj<GenericEditorFactoryService>;
    let waitDialogService: jasmine.SpyObj<IWaitDialogService>;
    let elementRef: ElementRef<HTMLElement>;
    let genericEditor: jasmine.SpyObj<IGenericEditor>;

    // for FormBuilderDirective
    let viewContainer: jasmine.SpyObj<ViewContainerRef>;
    let stateBuilderService: jasmine.SpyObj<GenericEditorStateBuilderService>;

    beforeEach(async () => {
        // for GenericEditorComponent
        genericEditorFactoryService = jasmine.createSpyObj('genericEditorFactoryService', [
            'getGenericEditorConstructor'
        ]);
        waitDialogService = jasmine.createSpyObj('waitDialogService', ['hideWaitModal']);
        elementRef = new ElementRef(document.createElement('se-generic-editor'));

        // for FormBuilderDirective
        viewContainer = jasmine.createSpyObj('viewContainer', ['clear', 'createEmbeddedView']);
        stateBuilderService = jasmine.createSpyObj('stateBuilderService', ['buildState']);

        // to handle editor and IGenericEditorConstructor
        genericEditor = jasmine.createSpyObj(
            'genericEditor',
            [
                '_finalize',
                'init',
                'watchFormErrors',
                '_finalize',
                'isDirty',
                'isValid',
                'isSubmitDisabled',
                'onReset',
                'reset',
                'setForm'
            ],
            {
                api: createApi({}),
                alwaysShowReset: false,
                alwaysShowSubmit: false,
                schema$: new BehaviorSubject<GenericEditorSchema>(null),
                data$: new BehaviorSubject<Payload>(null)
            }
        );
        const genericEditorConstructor: IGenericEditorConstructor = class {
            api: GenericEditorAPI = createApi({});
            constructor() {
                component.editor = genericEditor;
            }
            init(): Promise<void> {
                return Promise.resolve();
            }
        } as unknown as IGenericEditorConstructor;
        genericEditorFactoryService.getGenericEditorConstructor.and.returnValue(
            genericEditorConstructor
        );

        // use TestBed to generate
        await TestBed.configureTestingModule({
            imports: [TranslateModule.forRoot()],
            declarations: [
                GenericEditorComponent,
                FakeGenericEditorRootTabsComponent,
                FormBuilderDirective
            ],
            schemas: [NO_ERRORS_SCHEMA],
            providers: [
                { provide: GenericEditorFactoryService, useValue: genericEditorFactoryService },
                { provide: IWaitDialogService, useValue: waitDialogService },
                { provide: ElementRef, useValue: elementRef },
                { provide: ViewContainerRef, useValue: viewContainer },
                { provide: GenericEditorStateBuilderService, useValue: stateBuilderService }
            ]
        })
            .overrideComponent(GenericEditorComponent, {
                set: {
                    templateUrl: '/base/src/components/genericEditor/GenericEditorComponent.html'
                }
            })
            .compileComponents();

        fixture = TestBed.createComponent(GenericEditorComponent);
        component = fixture.componentInstance;

        // inject data
        component.id = 'e3ddc179-d014-46a7-ae9e-2912103702f2';
        component.smarteditComponentType = 'SimpleResponsiveBannerComponent';
        component.smarteditComponentId = 'component id';
        component.editorStackId = 'editor stack id';
        component.structureApi =
            '/cmswebservices/v1/types?code=:smarteditComponentType&mode=DEFAULT';
        component.contentApi = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';
        component.ngOnChanges({
            id: new SimpleChange(undefined, 'e3ddc179-d014-46a7-ae9e-2912103702f2', false)
        });

        // have to reset editor to adapt mock genericEditorConstructor
        component.editor = genericEditor;
        fixture.detectChanges();
    });

    it('renders GenericEditorComponent and FormBuilderDirective with fake child component GenericEditorRootTabsComponent', () => {
        expect(component).toBeTruthy();
    });

    describe('showCancel and showSubmit: ', () => {
        it('When no data change and data is valid, cancel button is invisible, submit button is invisible', () => {
            genericEditor.isDirty.and.returnValue(false);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showCancel()).toBeFalsy();
            expect(component.showSubmit()).toBeFalsy();
        });

        it('When data changed and data is valid, cancel button is visible, submit button is visible', () => {
            genericEditor.isDirty.and.returnValue(true);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showCancel()).toBeTrue();
            expect(component.showSubmit()).toBeTrue();
        });

        it('When alwaysShowReset is set to true, cancel button is always visible', () => {
            Object.defineProperty(genericEditor, 'alwaysShowReset', { value: true });
            genericEditor.isDirty.and.returnValue(true);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showCancel()).toBeTrue();

            genericEditor.isDirty.and.returnValue(false);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showCancel()).toBeTrue();

            genericEditor.isDirty.and.returnValue(false);
            genericEditor.isValid.and.returnValue(false);
            expect(component.showCancel()).toBeTrue();

            genericEditor.isDirty.and.returnValue(true);
            genericEditor.isValid.and.returnValue(false);
            expect(component.showCancel()).toBeTrue();
        });

        it('When alwaysShowSubmit is set to true, submit button is always visible', () => {
            Object.defineProperty(genericEditor, 'alwaysShowSubmit', { value: true });
            genericEditor.isDirty.and.returnValue(true);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showSubmit()).toBeTrue();

            genericEditor.isDirty.and.returnValue(false);
            genericEditor.isValid.and.returnValue(true);
            expect(component.showSubmit()).toBeTrue();

            genericEditor.isDirty.and.returnValue(false);
            genericEditor.isValid.and.returnValue(false);
            expect(component.showSubmit()).toBeTrue();

            genericEditor.isDirty.and.returnValue(true);
            genericEditor.isValid.and.returnValue(false);
            expect(component.showSubmit()).toBeTrue();
        });
    });

    it('ngOnChanges:  when there are some input changes, getApi will be called to emit', () => {
        let api: GenericEditorAPI;
        component.getApi.pipe(take(1)).subscribe((_api) => {
            api = _api;
        });
        component.ngOnChanges({
            id: new SimpleChange(undefined, '00000000', false)
        });
        component.editor = jasmine.createSpyObj('component.editor', ['_finalize']);
        expect(api).toBeDefined();
    });

    it('setFormState and nativeForm setter: watchFormErrors should be called once', () => {
        component.setFormState({} as GenericEditorState);
        component.nativeForm = new ElementRef(document.createElement('div'));
        expect(genericEditor.watchFormErrors).toHaveBeenCalledTimes(1);
    });
});
