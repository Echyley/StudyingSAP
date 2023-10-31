/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NavigationNodeComponent } from 'cmssmarteditcontainer/components/navigation/navigationEditor/NavigationNodeComponent';
import {
    IDropdownMenuItem,
    ITreeNodeItem,
    NavigationNode,
    NavigationNodeItem,
    NavigationNodeItemDTO,
    TreeComponent
} from 'smarteditcommons';

describe('NavigationNodeComponent', () => {
    let component: NavigationNodeComponent;
    let mockNode: ITreeNodeItem<NavigationNode>;
    let mockTreeComponent: TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>;

    const dropdownItems: IDropdownMenuItem[] = [
        {
            key: 'se.cms.navigationmanagement.navnode.edit',
            callback: jasmine.createSpy()
        }
    ];

    beforeEach(() => {
        mockTreeComponent = {
            nodeActions: {
                getDropdownItems: jasmine.createSpy().and.returnValue(dropdownItems),
                isReadOnly: jasmine.createSpy().and.returnValue(true),
                getEntryString: jasmine.createSpy().and.returnValue('some entry string')
            }
        } as unknown as TreeComponent<NavigationNodeItem, NavigationNodeItemDTO>;
        mockNode = {
            title: { en: 'title' },
            entries: []
        } as unknown as ITreeNodeItem<NavigationNode>;

        component = new NavigationNodeComponent(mockTreeComponent, mockNode);
    });

    it('GIVEN component is initialized THEN it should set data from parent tree component', () => {
        component.ngOnInit();

        expect(component.dropdownItems).toEqual(dropdownItems);
        expect(component.isReadOnly).toEqual(true);
        expect(component.entryString).toEqual('some entry string');

        expect(mockTreeComponent.nodeActions.getEntryString).toHaveBeenCalledWith(mockNode);
    });
});
