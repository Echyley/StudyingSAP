/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ElementRef,
    Inject,
    OnDestroy,
    OnInit
} from '@angular/core';
import { PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING } from 'personalizationcommons';
import {
    AbstractDecorator,
    CrossFrameEventService,
    OVERLAY_RERENDERED_EVENT,
    YJQUERY_TOKEN
} from 'smarteditcommons';
import { PersonalizationsmarteditContextService } from '../service';

@Component({
    selector: 'personalizationsmartedit-combined-view-component-light-up',
    templateUrl: './CombinedViewComponentLightUpDecorator.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PersonalizationsmarteditCombinedViewComponentLightUpDecorator
    extends AbstractDecorator
    implements OnInit, OnDestroy
{
    public letterForElement: string;
    public classForElement: string;

    private $element: JQuery<HTMLElement>;
    private allBorderClassess: string;
    private unSubscribeRegOverlayRerender: () => void;

    constructor(
        private personalizationsmarteditContextService: PersonalizationsmarteditContextService,
        private crossFrameEventService: CrossFrameEventService,
        private cdr: ChangeDetectorRef,
        @Inject(YJQUERY_TOKEN) yjQuery: JQueryStatic,
        element: ElementRef
    ) {
        super();

        this.$element = yjQuery(element.nativeElement);
        this.letterForElement = '';
        this.classForElement = '';
    }

    ngOnInit(): void {
        this.allBorderClassess = Object.values(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING)
            .map(({ borderClass }) => borderClass)
            .join(' ');

        this.unSubscribeRegOverlayRerender = this.crossFrameEventService.subscribe(
            OVERLAY_RERENDERED_EVENT,
            () => {
                this.calculate();
            }
        );
        this.calculate();
    }

    ngOnDestroy(): void {
        this.unSubscribeRegOverlayRerender();
    }

    private calculate(): void {
        const combinedView = this.personalizationsmarteditContextService.getCombinedView();
        if (!combinedView.enabled) {
            return;
        }

        const container = this.$element
            .parent()
            .closest(
                '[class~="smartEditComponentX"][data-smartedit-container-source-id][data-smartedit-container-type="CxCmsComponentContainer"][data-smartedit-personalization-customization-id][data-smartedit-personalization-variation-id]'
            );
        if (container.length === 0) {
            return;
        }

        container.removeClass(this.allBorderClassess);
        // append css classes on customized components
        (combinedView.selectedItems || []).forEach((element, index) => {
            let state =
                container.data().smarteditPersonalizationCustomizationId ===
                element.customization.code;
            state =
                state &&
                container.data().smarteditPersonalizationVariationId === element.variation.code;
            if (state) {
                const wrappedIndex =
                    index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
                container.addClass(
                    PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].borderClass
                );

                this.letterForElement = String.fromCharCode(
                    'a'.charCodeAt(0) + wrappedIndex
                ).toUpperCase();
                this.classForElement =
                    PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
                this.cdr.detectChanges();
            }
        });
    }
}
