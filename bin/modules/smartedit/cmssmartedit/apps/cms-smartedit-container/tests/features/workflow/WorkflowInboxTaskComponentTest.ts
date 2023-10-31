/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CMSTimeService } from 'cmscommons';
import { WorkflowInboxTaskComponent } from 'cmssmarteditcontainer/components/workflow/components/workflowInboxTask/WorkflowInboxTaskComponent';
import { Observable } from 'rxjs';
import { L10nPipe, WorkflowService, WorkflowTask } from 'smarteditcommons';

describe('WorkflowInboxTaskComponent', () => {
    const getMockWorkflowTask = () =>
        ({
            action: {
                name: { en: 'Translate to German' },
                startedAgoInMillis: 1000
            },
            attachments: [
                {
                    catalogName: { en: 'Electronics Content' },
                    catalogVersion: 'Catalog Staged',
                    pageName: 'Homepage'
                }
            ]
        } as unknown as WorkflowTask);

    let cMSTimeService: jasmine.SpyObj<CMSTimeService>;
    let workflowService: jasmine.SpyObj<WorkflowService>;
    let l10nPipe: jasmine.SpyObj<L10nPipe>;
    let component: WorkflowInboxTaskComponent;
    beforeEach(() => {
        cMSTimeService = jasmine.createSpyObj<CMSTimeService>('cMSTimeService', ['getTimeAgo']);
        workflowService = jasmine.createSpyObj<WorkflowService>('workflowService', [
            'loadExperienceAndOpenPageWorkflowMenu'
        ]);
        l10nPipe = jasmine.createSpyObj<L10nPipe>('l10nPipe', ['transform']);
        component = new WorkflowInboxTaskComponent(cMSTimeService, workflowService, l10nPipe);
        l10nPipe.transform.and.callFake(
            (localiezdMap: any) =>
                new Observable((subscriver) => {
                    subscriver.next(localiezdMap.en);
                })
        );
    });

    beforeEach(() => {
        component.task = getMockWorkflowTask();
    });

    it('getTaskName should return task name', async () => {
        const taskName = await component.getTaskName();

        expect(taskName).toBe('Translate to German');
    });

    it('getTaskDescription should return task description', async () => {
        const taskDescription = await component.getTaskDescription();

        expect(taskDescription).toContain('Electronics Content');
    });

    it('getTaskCreatedAgo should return string that determines how much time ago the task was created', () => {
        component.getTaskCreatedAgo();

        expect(cMSTimeService.getTimeAgo).toHaveBeenCalledWith(
            component.task.action.startedAgoInMillis
        );
    });

    it('WHEN task header has been clicked THEN it should load experience and open workflow menu', () => {
        component.onClick(new Event('click'));

        expect(workflowService.loadExperienceAndOpenPageWorkflowMenu).toHaveBeenCalledWith(
            component.task
        );
    });
});
