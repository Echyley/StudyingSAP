/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    HostBinding,
    Input,
    OnInit,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import {
    ICMSComponent,
    IComponentSharedService,
    IComponentVisibilityAlertService
} from 'cmscommons';
import {
    ICatalogService,
    ICatalogVersionPermissionService,
    LogService,
    IEditorModalService,
    IUserTrackingService,
    USER_TRACKING_FUNCTIONALITY
} from 'smarteditcommons';

/**
 * Used to display information about a specified hidden component.
 */
@Component({
    selector: 'se-slot-visibility-component',
    templateUrl: './SlotVisibilityComponent.html',
    styleUrls: ['./SlotVisibilityComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SlotVisibilityComponent implements OnInit {
    @Input() component: ICMSComponent;

    @HostBinding('attr.data-component-id')
    public componentId: string;

    @Input() slotId: string;

    public isReady: boolean;
    public isSharedComponent: boolean;
    public readOnly: boolean;
    public componentVisibilitySwitch: string;
    public componentRestrictionsCount: string;

    constructor(
        private catalogService: ICatalogService,
        private componentSharedService: IComponentSharedService,
        private editorModalService: IEditorModalService,
        private componentVisibilityAlertService: IComponentVisibilityAlertService,
        private catalogVersionPermissionService: ICatalogVersionPermissionService,
        private logService: LogService,
        private cdr: ChangeDetectorRef,
        private userTrackingService: IUserTrackingService
    ) {
        this.isReady = false;
        this.isSharedComponent = false;
        this.readOnly = false;
    }

    async ngOnInit(): Promise<void> {
        this.isSharedComponent = await this.componentSharedService.isComponentShared(
            this.component
        );

        const { catalogId, version } = await this.catalogService.getCatalogVersionByUuid(
            this.component.catalogVersion
        );

        const isWritable = await this.catalogVersionPermissionService.hasWritePermission(
            catalogId,
            version
        );
        this.readOnly = !isWritable || (this.component.isExternal as boolean);

        this.componentVisibilitySwitch = this.component.visible
            ? 'se.cms.component.visibility.status.on'
            : 'se.cms.component.visibility.status.off';

        const count = (this.component.restrictions as string[])?.length || 0;
        this.componentRestrictionsCount = `(${count})`;

        this.isReady = true;

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const componentChange = changes.component;

        if (componentChange) {
            this.componentId = this.component.uid;
        }
    }

    public async openEditorModal(): Promise<void> {
        this.userTrackingService.trackingUserAction(
            USER_TRACKING_FUNCTIONALITY.CONTEXT_MENU,
            'Slot Visibility'
        );
        try {
            const item = await this.editorModalService.openAndRerenderSlot(
                this.component.typeCode,
                this.component.uuid,
                'visible'
            );
            this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
                itemId: item.uuid,
                itemType: item.itemtype,
                catalogVersion: item.catalogVersion,
                restricted: item.restricted,
                slotId: this.slotId,
                visible: item.visible
            });
        } catch (error) {
            if (error !== undefined) {
                this.logService.warn(
                    'Something went wrong with openAndRerenderSlot method.',
                    error
                );
            }
        }
    }
}
