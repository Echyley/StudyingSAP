/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import { take } from 'rxjs/operators';
import {
    ComponentAttributes,
    ICatalogService,
    L10nPipe,
    LocalizedMap,
    TypedMap,
    CMSModesService,
    SlotSharedService
} from 'smarteditcommons';

interface PopoverMessage {
    translate: string;
    translateParams: { catalogName: string; slotId: string };
}

enum IconClass {
    GLOBE = 'sap-icon--globe',
    HOVERED = 'sap-icon--chain-link'
}

@Component({
    selector: 'slot-disabled-component', // At some point it should be prefixed with "se-". I leave it as it is because I am not sure about the impact of changing the selector.
    templateUrl: './SlotDisabledComponent.html',
    styleUrls: ['./SlotDisabledComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [L10nPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotDisabledComponent implements OnInit, OnChanges {
    @Input() componentAttributes: ComponentAttributes;
    @Input() active: boolean;

    public popoverMessage?: PopoverMessage;
    public iconClass?: string;
    public outerSlotClass?: TypedMap<boolean>;
    public isGlobalSlot: boolean;
    public isVersioningPerspective: boolean;

    private readonly DEFAULT_DECORATOR_MSG = 'se.cms.sharedslot.decorator.label';
    private readonly GLOBAL_SLOT_DECORATOR_MSG = 'se.cms.parentsharedslot.decorator.label';

    private readonly DEFAULT_DECORATOR_MSG_VERSIONING_MODE =
        'se.cms.versioning.shared.slot.from.label';
    private readonly GLOBAL_SLOT_DECORATOR_MSG_VERSIONING_MODE =
        'se.cms.versioning.parent.shared.slot.from.label';

    constructor(
        private catalogService: ICatalogService,
        private cMSModesService: CMSModesService,
        private l10nPipe: L10nPipe,
        private slotSharedService: SlotSharedService,
        private cdr: ChangeDetectorRef
    ) {
        this.isGlobalSlot = false;
        this.isVersioningPerspective = false;
    }

    async ngOnInit(): Promise<void> {
        const [catalogVersion, isVersioningPerspective] = await Promise.all([
            this.catalogService.getCatalogVersionByUuid(
                this.componentAttributes.smarteditCatalogVersionUuid
            ),
            this.cMSModesService.isVersioningPerspectiveActive()
        ]);
        this.isVersioningPerspective = isVersioningPerspective;

        this.isGlobalSlot = await this.slotSharedService.isGlobalSlot(
            this.componentAttributes.smarteditComponentId,
            this.componentAttributes.smarteditComponentType
        );

        this.popoverMessage = await this.getPopoverMessage(catalogVersion.catalogName);

        this.setIconAndOuterSlotClassName();

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const activeChange = changes.active;
        if (activeChange && !activeChange.firstChange) {
            this.setIconAndOuterSlotClassName();
        }
    }

    private async getPopoverMessage(catalogNameL10n: LocalizedMap): Promise<PopoverMessage> {
        let msgToLocalize: string;
        if (this.isVersioningPerspective) {
            msgToLocalize = this.isGlobalSlot
                ? this.GLOBAL_SLOT_DECORATOR_MSG_VERSIONING_MODE
                : this.DEFAULT_DECORATOR_MSG_VERSIONING_MODE;
        } else {
            msgToLocalize = this.isGlobalSlot
                ? this.GLOBAL_SLOT_DECORATOR_MSG
                : this.DEFAULT_DECORATOR_MSG;
        }

        const catalogName = await this.l10nPipe
            .transform(catalogNameL10n)
            .pipe(take(1))
            .toPromise();
        const msgParams = {
            slotId: this.componentAttributes.smarteditComponentId,
            catalogName
        };

        return { translate: msgToLocalize, translateParams: msgParams };
    }

    private setIconAndOuterSlotClassName(): void {
        this.iconClass = this.isGlobalSlot ? IconClass.GLOBE : IconClass.HOVERED;
        this.outerSlotClass = this.getOuterSlotClass(this.active, this.iconClass);
    }

    private getOuterSlotClass(active: boolean, iconClass: string): TypedMap<boolean> {
        return {
            'disabled-shared-slot__icon--outer-hovered': active && !this.isVersioningPerspective,
            'disabled-shared-slot__icon--outer-globe': iconClass === IconClass.GLOBE
        };
    }
}
