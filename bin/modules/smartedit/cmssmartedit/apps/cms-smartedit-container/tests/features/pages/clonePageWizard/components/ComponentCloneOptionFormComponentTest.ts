/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ComponentCloneOptionFormComponent } from 'cmssmarteditcontainer/components/pages/clonePageWizard/components/clonePageOptions/ComponentCloneOptionFormComponent';

describe('ComponentCloneOptionFormComponent', () => {
    let component: ComponentCloneOptionFormComponent;

    beforeEach(() => {
        //GIVEN
        component = new ComponentCloneOptionFormComponent();
    });

    it('should expose a "componentInSlotOption" object when onInit is called', () => {
        //WHEN
        component.ngOnInit();

        //THEN
        expect(component.componentInSlotOption).toBeDefined();
    });

    it('should set default "componentInSlotOption" value to "CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.REFERENCE_EXISTING" when onInit is called', () => {
        //WHEN
        component.ngOnInit();

        //THEN
        expect(component.componentInSlotOption).toBe(
            component.CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.REFERENCE_EXISTING
        );
    });

    it('should emit "CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.CLONE" when updateComponentInSlotOption is called with "CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.CLONE"', () => {
        //WHEN
        spyOn(component.onSelectionChange, 'emit');
        component.updateComponentInSlotOption(
            component.CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.CLONE
        );

        //THEN
        expect(component.onSelectionChange.emit).toHaveBeenCalledWith(
            component.CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION.CLONE
        );
    });
});
