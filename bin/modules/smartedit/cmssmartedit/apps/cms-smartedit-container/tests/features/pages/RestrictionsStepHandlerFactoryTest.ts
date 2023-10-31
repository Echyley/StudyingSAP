/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    RestrictionsStepHandler,
    RestrictionsStepHandlerFactory
} from 'cmssmarteditcontainer/services/pages/RestrictionsStepHandlerFactory';
import { RestrictionsEditorFunctionBindings } from 'cmssmarteditcontainer/services/pages/types';
import { WizardService, WizardStep } from 'smarteditcommons';

describe('RestrictionsStepHandlerFactory', () => {
    let factory: RestrictionsStepHandlerFactory;
    let wizardService: jasmine.SpyObj<WizardService>;

    const restrictionsEditorFncs: RestrictionsEditorFunctionBindings = {
        cancel: jasmine.createSpy(),
        isDirty: jasmine.createSpy(),
        reset: jasmine.createSpy()
    };

    class MockWizardStepComponent {}

    const stepDetails: WizardStep = {
        id: 'step-X',
        name: 'step x',
        title: 'step x',
        component: MockWizardStepComponent
    };

    beforeEach(() => {
        wizardService = jasmine.createSpyObj<WizardService>('wizardService', [
            'removeStepById',
            'addStep',
            'goToStepWithId',
            'containsStep',
            'getStepsCount'
        ]);

        factory = new RestrictionsStepHandlerFactory();
    });

    it('should create handler', () => {
        const handler = factory.createRestrictionsStepHandler(
            wizardService,
            restrictionsEditorFncs,
            stepDetails
        );

        expect(handler).toBeDefined();
        expect(handler instanceof RestrictionsStepHandler).toBe(true);
    });

    describe('RestrictionsStepHandler', () => {
        let handler: RestrictionsStepHandler;

        beforeEach(() => {
            handler = factory.createRestrictionsStepHandler(
                wizardService,
                restrictionsEditorFncs,
                stepDetails
            );
        });

        describe('hideStep', () => {
            it('WHEN step is on Wizard THEN it should remove it', () => {
                wizardService.containsStep.and.returnValue(true);

                handler.hideStep();

                expect(wizardService.removeStepById).toHaveBeenCalledWith(stepDetails.id);
            });

            it('WHEN step is not on Wizard THEN it should do nothing', () => {
                wizardService.containsStep.and.returnValue(false);

                handler.hideStep();

                expect(wizardService.removeStepById).not.toHaveBeenCalled();
            });
        });

        describe('showStep', () => {
            it('WHEN step is not on Wizard THEN it should add it', () => {
                wizardService.containsStep.and.returnValue(false);
                wizardService.getStepsCount.and.returnValue(3);

                handler.showStep();

                expect(wizardService.addStep).toHaveBeenCalledWith(stepDetails, 3);
            });

            it('WHEN step is on Wizard THEN it should do nothing', () => {
                wizardService.containsStep.and.returnValue(true);

                handler.showStep();

                expect(wizardService.addStep).not.toHaveBeenCalled();
            });
        });

        describe('isStepValid', () => {
            it('WHEN isDirty method is provided THEN it should be called', () => {
                handler.isStepValid();

                expect(restrictionsEditorFncs.isDirty).toHaveBeenCalled();
            });

            it('WHEN isDirty method is not provided THEN it should not be called', () => {
                handler = factory.createRestrictionsStepHandler(
                    wizardService,
                    { ...restrictionsEditorFncs, isDirty: null },
                    stepDetails
                );

                expect(() => {
                    handler.isStepValid();
                }).not.toThrow();
            });
        });

        it('WHEN getStepId is called THEN it should return id of stepdetails given in constructor', () => {
            const actual = handler.getStepId();

            expect(actual).toEqual('step-X');
        });

        it('WHEN goToStep is called THEN is should use wizard manager to got to step with id', () => {
            handler.goToStep();

            expect(wizardService.goToStepWithId).toHaveBeenCalledWith('step-X');
        });
    });
});
