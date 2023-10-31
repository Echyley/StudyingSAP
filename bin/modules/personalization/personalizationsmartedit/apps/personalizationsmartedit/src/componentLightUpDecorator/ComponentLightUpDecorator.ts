/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnDestroy,
    ElementRef,
    Inject,
    AfterViewInit,
    ChangeDetectionStrategy
} from '@angular/core';
import { CONTAINER_SOURCE_ID_ATTR } from 'personalizationcommons';
import {
    AbstractDecorator,
    CATALOG_VERSION_UUID_ATTRIBUTE,
    CONTAINER_TYPE_ATTRIBUTE,
    CrossFrameEventService,
    ID_ATTRIBUTE,
    OVERLAY_COMPONENT_CLASS,
    TYPE_ATTRIBUTE,
    YJQUERY_TOKEN
} from 'smarteditcommons';
import {
    PersonalizationsmarteditContextService,
    PersonalizationsmarteditComponentHandlerService
} from '../service';

@Component({
    selector: 'personalizationsmartedit-component-light-up',
    template: `<div><ng-content></ng-content></div>`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PersonalizationsmarteditComponentLightUpDecorator
    extends AbstractDecorator
    implements OnDestroy, AfterViewInit
{
    private $element: JQuery<HTMLElement>;
    private CONTAINER_TYPE = 'CxCmsComponentContainer';
    private ACTION_ID_ATTR = 'data-smartedit-personalization-action-id';
    private PARENT_CONTAINER_SELECTOR: string;
    private PARENT_CONTAINER_WITH_ACTION_SELECTOR: string;
    private COMPONENT_SELECTOR: string;

    /** Event when selecting a customization.  */
    private unRegisterCustomizeContextSynchronized: () => void;

    constructor(
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService,
        private crossFrameEventService: CrossFrameEventService,
        element: ElementRef,
        @Inject(YJQUERY_TOKEN) yjQuery: JQueryStatic
    ) {
        super();

        this.$element = yjQuery(element.nativeElement);

        this.PARENT_CONTAINER_SELECTOR = `[class~="${OVERLAY_COMPONENT_CLASS}"][${CONTAINER_SOURCE_ID_ATTR}][${CONTAINER_TYPE_ATTRIBUTE}="${this.CONTAINER_TYPE}"]`;
        this.PARENT_CONTAINER_WITH_ACTION_SELECTOR = `[class~="${OVERLAY_COMPONENT_CLASS}"][${CONTAINER_TYPE_ATTRIBUTE}="${this.CONTAINER_TYPE}"][${this.ACTION_ID_ATTR}]`;
        this.COMPONENT_SELECTOR = `[${ID_ATTRIBUTE}][${CATALOG_VERSION_UUID_ATTRIBUTE}][${TYPE_ATTRIBUTE}]`;
    }

    ngAfterViewInit(): void {
        this.unRegisterCustomizeContextSynchronized = this.crossFrameEventService.subscribe(
            'PERSONALIZATION_CUSTOMIZE_CONTEXT_SYNCHRONIZED',
            () => this.delayToggleCssClasses()
        );
        this.delayToggleCssClasses();
    }

    ngOnDestroy(): void {
        this.unRegisterCustomizeContextSynchronized();
    }

    /**
     * There is an issue that the DOM instance of $element has been created but it has not been appended to the DOM yet and hence it is not possible to access its parent element.
     * By using timeout we ensure that the element has been already appended to DOM.
     */
    private delayToggleCssClasses(): void {
        setTimeout(() => {
            this.toggleCssClasses();
        }, 500);
    }

    private toggleCssClasses(): void {
        const component = this.$element.parent().closest(this.COMPONENT_SELECTOR); // personalizationsmartedit-combined-view-component-light-up
        const container = component.closest(this.PARENT_CONTAINER_SELECTOR); // smartedit-element

        const isVariationComponentSelected = this.isVariationComponentSelected(component);
        // when Customization has been selected, a border displays on customized components
        container.toggleClass('perso__component-decorator', isVariationComponentSelected);
        // when Customization has been selected, checked icon is displayed in top left corner of the decorator
        container.toggleClass(
            'hyicon hyicon-checkedlg perso__component-decorator-icon',
            isVariationComponentSelected
        );
        // when Customization is clicked in, a border displays on customized components
        container.toggleClass(
            'personalizationsmarteditComponentSelected',
            this.isComponentSelected()
        );
    }

    private isVariationComponentSelected(component: JQuery<HTMLElement>): boolean {
        let isSelected = false;
        const customize = this.personalizationsmarteditContextService.getCustomize();
        if (customize.selectedCustomization && customize.selectedVariations) {
            const container = component.closest(this.PARENT_CONTAINER_WITH_ACTION_SELECTOR);
            isSelected = container.length > 0;
        }
        return isSelected;
    }

    private isComponentSelected(): boolean {
        if (
            !Array.isArray(
                this.personalizationsmarteditContextService.getCustomize().selectedVariations
            )
        ) {
            return false;
        }

        const containerId =
            this.personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(
                this.$element
            );
        const selectedComponents =
            this.personalizationsmarteditContextService.getCustomize().selectedComponents;
        return !!selectedComponents?.includes(containerId);
    }
}
