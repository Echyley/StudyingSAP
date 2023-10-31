/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    ComponentFactoryResolver,
    ComponentRef,
    Inject,
    Input,
    OnChanges,
    OnInit,
    Optional,
    SimpleChanges,
    ViewChild
} from '@angular/core';
import {
    IMediaAdvancedPropertiesCustomComponent,
    MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN,
    Media,
    MediaAdvancedPropertiesCustom
} from 'smarteditcommons';
import { MediaAdvancedPropertiesCustomDirective } from '../../directives/MediaAdvancedPropertiesCustomDirective';

@Component({
    selector: 'se-media-advanced-properties',
    templateUrl: './MediaAdvancedPropertiesComponent.html',
    styleUrls: ['./MediaAdvancedPropertiesComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaAdvancedPropertiesComponent implements OnInit, OnChanges {
    @Input() media: Media;

    @ViewChild(MediaAdvancedPropertiesCustomDirective, { static: true })
    mediaAdvancedPropertiesCustomDirective: MediaAdvancedPropertiesCustomDirective;

    public i18nKeys = {
        DESCRIPTION: 'se.media.advanced.information.description',
        CODE: 'se.media.advanced.information.code',
        ALT_TEXT: 'se.media.advanced.information.alt.text',
        ADVANCED_INFORMATION: 'se.media.advanced.information',
        INFORMATION: 'se.media.information'
    };

    private customComponentRef: ComponentRef<IMediaAdvancedPropertiesCustomComponent>;

    constructor(
        private resolver: ComponentFactoryResolver,
        @Optional()
        @Inject(MEDIA_ADVANCED_PROPERTIES_CUSTOM_TOKEN)
        private mediaAdvancedPropertiesCustom: MediaAdvancedPropertiesCustom
    ) {}

    ngOnChanges(changes: SimpleChanges): void {
        const mediaChange = changes.media;
        if (mediaChange && this.customComponentRef) {
            this.customComponentRef.instance.media = mediaChange.currentValue;
        }
    }

    ngOnInit(): void {
        this.loadMediaAdvancedPropertiesCustomComponent();
    }

    public onClick($event: Event): void {
        $event.stopPropagation();
    }

    private loadMediaAdvancedPropertiesCustomComponent(): void {
        if (!this.mediaAdvancedPropertiesCustom) {
            return;
        }
        const componentFactory =
            this.resolver.resolveComponentFactory<IMediaAdvancedPropertiesCustomComponent>(
                this.mediaAdvancedPropertiesCustom.component
            );

        const viewContainerRef = this.mediaAdvancedPropertiesCustomDirective.viewContainerRef;
        viewContainerRef.clear();

        this.customComponentRef =
            viewContainerRef.createComponent<IMediaAdvancedPropertiesCustomComponent>(
                componentFactory
            );
        this.customComponentRef.instance.media = this.media;
    }
}
