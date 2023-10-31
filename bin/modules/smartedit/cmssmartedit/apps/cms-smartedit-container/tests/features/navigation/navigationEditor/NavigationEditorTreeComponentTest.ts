/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationEditorTreeActions } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorTreeActionsService';
import { NavigationEditorTreeComponent } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorTreeComponent';
import { NavigationEditorTreeDragOptions } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorTreeDragOptionsService';
import { NavigationTreeActions } from 'cmssmarteditcontainer/components/navigation/navigationEditor/types';
import { NavigationNodeItem, TreeDragAndDropOptions } from 'smarteditcommons';

describe('NavigationEditorTreeComponent', () => {
    let component: NavigationEditorTreeComponent;
    let dragOptions: jasmine.SpyObj<NavigationEditorTreeDragOptions>;
    let actions: jasmine.SpyObj<NavigationEditorTreeActions>;

    const mockActions = {
        dragAndDrop: jasmine.createSpy()
    } as unknown as NavigationTreeActions;
    const mockDragOptions = {
        onDropCallback: jasmine.createSpy()
    } as unknown as TreeDragAndDropOptions<NavigationNodeItem>;

    beforeEach(() => {
        dragOptions = jasmine.createSpyObj<NavigationEditorTreeDragOptions>('dragOptionsService', [
            'setup',
            'getDragOptions'
        ]);
        actions = jasmine.createSpyObj<NavigationEditorTreeActions>('actionsService', [
            'setup',
            'getActions'
        ]);

        actions.getActions.and.returnValue(mockActions);
        dragOptions.getDragOptions.and.returnValue(mockDragOptions);

        component = new NavigationEditorTreeComponent(dragOptions, actions);
    });

    describe('GIVEN component is initialized', () => {
        it('WHEN is read only THEN it should set component properties and actions but no drag options', () => {
            component.uriContext = { context: 'context' };
            component.readOnly = true;
            component.rootNodeUid = 'someNode';

            component.ngOnInit();

            expect(component.nodeURI).toEqual(
                '/cmswebservices/v1/sites/:CURRENT_CONTEXT_SITE_ID/catalogs/:CURRENT_CONTEXT_CATALOG/versions/:CURRENT_CONTEXT_CATALOG_VERSION/navigations'
            );
            expect(component.rootNodeUid).toEqual('someNode');
            expect(component.actions).toEqual(mockActions);
            expect(component.dragOptions).toEqual(undefined);

            expect(actions.setup).toHaveBeenCalledWith(true, 'someNode', { context: 'context' });
        });

        it('WHEN is NOT read only THEN it should initialize component properties, actions and drag options', () => {
            component.uriContext = { context: 'context' };
            component.readOnly = false;
            component.rootNodeUid = 'someNode';

            component.ngOnInit();

            expect(component.nodeURI).toEqual(
                '/cmswebservices/v1/sites/:CURRENT_CONTEXT_SITE_ID/catalogs/:CURRENT_CONTEXT_CATALOG/versions/:CURRENT_CONTEXT_CATALOG_VERSION/navigations'
            );
            expect(component.rootNodeUid).toEqual('someNode');
            expect(component.actions).toEqual(mockActions);
            expect(component.dragOptions).toEqual(mockDragOptions);
            expect(actions.setup).toHaveBeenCalledWith(false, 'someNode', { context: 'context' });
            expect(dragOptions.setup).toHaveBeenCalledWith(jasmine.any(Function));
        });

        it('WHEN is NOT readonly it should use dragAndDrop handler from actions in dragOptions', () => {
            component.uriContext = { context: 'context' };
            component.readOnly = false;
            component.rootNodeUid = 'someNode';

            component.ngOnInit();

            const dragOptionsCallback = dragOptions.setup.calls.argsFor(0)[0];

            dragOptionsCallback({ foo: 'bar' } as any);

            expect(mockActions.dragAndDrop).toHaveBeenCalledWith({ foo: 'bar' });
        });
    });
});
