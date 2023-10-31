/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectorRef, SimpleChange } from '@angular/core';
import { PageStatusComponent } from 'cmssmarteditcontainer/components/workflow/components/pageDisplayStatus/pageStatus/PageStatusComponent';
import { WorkflowService } from 'smarteditcommons';
import { createSimulateNgOnChanges } from 'testhelpers';

describe('PageStatusComponent', () => {
    let workflowService: jasmine.SpyObj<WorkflowService>;
    const cdr = jasmine.createSpyObj<ChangeDetectorRef>('changeDetectorRef', ['detectChanges']);

    let component: PageStatusComponent;
    type Input = Partial<Pick<typeof component, 'uuid' | 'displayStatus'>>;
    let simulateNgOnChanges: ReturnType<typeof createSimulateNgOnChanges>;
    beforeEach(() => {
        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'getActiveWorkflowForPageUuid'
        ]);

        component = new PageStatusComponent(workflowService, cdr);

        simulateNgOnChanges = createSimulateNgOnChanges<Input>(component);
    });

    it('WHEN there is a new displayStatus value THEN it should set Status Label correctly', () => {
        simulateNgOnChanges({
            displayStatus: new SimpleChange(undefined, 'IN_PROGRESS', false)
        });

        expect(component.statusLabel.toLocaleLowerCase()).toContain('in_progress');
    });

    describe('WHEN there is a new displayStatus AND uuid THEN it should set Status Icon css class correctly', () => {
        it('GIVEN page is locked THEN it sets the class correctly', async () => {
            workflowService.getActiveWorkflowForPageUuid.and.returnValue({
                isAvailableForCurrentPrincipal: false
            } as any);

            await simulateNgOnChanges({
                uuid: new SimpleChange(undefined, 'eyJpdGV', false),
                displayStatus: new SimpleChange(undefined, 'IN_PROGRESS', false)
            });

            expect(component.statusIconCssClass).toContain('sap-icon--locked');
        });

        it('GIVEN page is not locked for THEN it sets the class correctly', async () => {
            workflowService.getActiveWorkflowForPageUuid.and.returnValue({
                isAvailableForCurrentPrincipal: false
            } as any);

            await simulateNgOnChanges({
                uuid: new SimpleChange(undefined, 'eyJpdGV', false),
                displayStatus: new SimpleChange(undefined, 'IN_PROGRESS', false)
            });

            expect(component.statusIconCssClass).toContain('sap-icon--locked');
        });
    });
});
