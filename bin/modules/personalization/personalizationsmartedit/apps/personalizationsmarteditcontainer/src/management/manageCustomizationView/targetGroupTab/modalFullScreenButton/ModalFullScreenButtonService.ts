/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';

@Injectable()
export class ModalFullScreenButtonService {
    private readonly className = 'sliderPanelParentModal';
    private readonly renderer: Renderer2;

    constructor(rendererFactory: RendererFactory2) {
        this.renderer = rendererFactory.createRenderer(null, null);
    }

    public toggleClassOnCustomizationModal(
        childElement: Element,
        classNameToToggle: string,
        toggleState: boolean
    ): void {
        const modal = this.findModal(childElement);
        if (!modal) {
            return;
        }

        if (toggleState) {
            this.renderer.addClass(modal, classNameToToggle);
        } else {
            this.renderer.removeClass(modal, classNameToToggle);
        }
    }

    private findModal(element: Element): Element {
        return element.closest(`.${this.className}`);
    }
}
