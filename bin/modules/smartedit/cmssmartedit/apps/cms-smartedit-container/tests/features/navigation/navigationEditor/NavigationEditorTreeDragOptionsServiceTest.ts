/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationEditorTreeDragOptions } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationEditorTreeDragOptionsService';
import { NavigationNodeItem, TreeDragAndDropEvent } from 'smarteditcommons';

describe('NavigationEditorTreeDragOptionsService', () => {
    let service: NavigationEditorTreeDragOptions;

    beforeEach(() => {
        service = new NavigationEditorTreeDragOptions();
    });

    it('WHEN setup is called THEN it should assign drag and drop handler', () => {
        const handler = () => {};
        service.setup(handler);

        expect((service as any).dragAndDropHandler).toEqual(handler);
    });

    it('WHEN getDragOptions is called THEN it should return object with methods', () => {
        const options = service.getDragOptions();

        const methodNames = Object.keys(options);
        expect(methodNames).toEqual(['onDropCallback', 'allowDropCallback', 'beforeDropCallback']);
    });

    describe('Service methods', () => {
        let dragHandler: jasmine.Spy;

        beforeEach(() => {
            dragHandler = jasmine.createSpy();
            service.setup(dragHandler);
        });

        describe('onDropCallback', () => {
            it('should call drag and drop handler', () => {
                const { onDropCallback } = service.getDragOptions();
                onDropCallback({
                    foo: 'bar'
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>);

                expect(dragHandler).toHaveBeenCalledWith({ foo: 'bar' });
            });
        });

        describe('allowDropCallback', () => {
            it('should return true when source node parent is equal destination node parent', () => {
                const event = {
                    sourceNode: { parent: { uid: 'parent1' } },
                    destinationNodes: [{ parent: { uid: 'parent1' } }]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;

                const { allowDropCallback } = service.getDragOptions();
                const actual = allowDropCallback(event);

                expect(actual).toEqual(true);
            });

            it('should return false when source node parent is the same as  destination node parent', () => {
                const event = {
                    sourceNode: { parent: { uid: 'parent1' } },
                    destinationNodes: [{ parent: { uid: 'parent2' } }]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;

                const { allowDropCallback } = service.getDragOptions();
                const actual = allowDropCallback(event);

                expect(actual).toEqual(false);
            });
        });

        describe('beforeDropCallback', () => {
            it('should return true when source node parent is the same as destination node parent', async () => {
                const event = {
                    sourceNode: { parent: { uid: 'parent1' } },
                    destinationNodes: [{ parent: { uid: 'parent1' } }]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;

                const { beforeDropCallback } = service.getDragOptions();
                const actual = await beforeDropCallback(event);

                expect(actual).toEqual(true);
            });

            it('should return translation key when source node parent is different than destination node parent', async () => {
                const event = {
                    sourceNode: { parent: { uid: 'parent1' } },
                    destinationNodes: [{ parent: { uid: 'parent2' } }]
                } as unknown as TreeDragAndDropEvent<NavigationNodeItem>;

                const { beforeDropCallback } = service.getDragOptions();
                const actual = await beforeDropCallback(event);

                expect(actual).toEqual({
                    confirmDropI18nKey: 'se.cms.navigationmanagement.navnode.confirmation'
                });
            });
        });
    });
});
