/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    OnInit,
    Inject,
    OnDestroy,
    ViewEncapsulation,
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import {
    ICatalogService,
    SystemEventService,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IUriContext,
    GenericEditorAttribute,
    CmsitemsRestService,
    CMSItem
} from 'smarteditcommons';
import { NavigationNodeUidsDTO } from '../../types';
import { SELECTED_NODE } from '../navigationNodeConstants';

@Component({
    selector: 'se-navigation-node-selector',
    templateUrl: './NavigationNodeSelectorComponent.html',
    styleUrls: ['./NavigationNodeSelectorComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None
})
export class NavigationNodeSelectorComponent implements OnInit, OnDestroy {
    public field: GenericEditorAttribute;
    public cmsItem: CMSItem;
    public qualifier: string;

    public isReady: boolean;
    public nodeUid: string;
    public uriContext: IUriContext;

    private unregisterSubscription: () => void;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) private data: GenericEditorWidgetData<CMSItem>,
        private catalogService: ICatalogService,
        private cmsitemsRestService: CmsitemsRestService,
        private systemEventService: SystemEventService,
        private cdr: ChangeDetectorRef
    ) {
        this.isReady = false;
        this.nodeUid = null;
        this.uriContext = null;

        ({ field: this.field, model: this.cmsItem, qualifier: this.qualifier } = this.data);
    }

    async ngOnInit(): Promise<void> {
        const uriContext = await this.catalogService.retrieveUriContext();
        this.uriContext = uriContext;

        if (this.cmsItem[this.qualifier]) {
            const cmsItem = await this.cmsitemsRestService.getById(
                this.cmsItem[this.qualifier] as string
            );
            this.nodeUid = cmsItem.uid;
            this.isReady = true;
        } else {
            this.isReady = true;
        }

        this.unregisterSubscription = this.systemEventService.subscribe(
            SELECTED_NODE,
            (_eventId, { nodeUid, nodeUuid }: NavigationNodeUidsDTO) => {
                this.nodeUid = nodeUid;
                this.cmsItem[this.qualifier] = nodeUuid;
                if (!(this.cdr as ViewRef).destroyed) {
                    this.cdr.detectChanges();
                }
            }
        );

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        this.unregisterSubscription();
    }

    public remove(): void {
        delete this.cmsItem[this.qualifier];
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}
