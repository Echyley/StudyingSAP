import { ComponentFactoryResolver, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Media, MediaAdvancedPropertiesCustom } from 'smarteditcommons';
import { MediaAdvancedPropertiesCustomDirective } from '../../directives/MediaAdvancedPropertiesCustomDirective';
export declare class MediaAdvancedPropertiesComponent implements OnInit, OnChanges {
    private resolver;
    private mediaAdvancedPropertiesCustom;
    media: Media;
    mediaAdvancedPropertiesCustomDirective: MediaAdvancedPropertiesCustomDirective;
    i18nKeys: {
        DESCRIPTION: string;
        CODE: string;
        ALT_TEXT: string;
        ADVANCED_INFORMATION: string;
        INFORMATION: string;
    };
    private customComponentRef;
    constructor(resolver: ComponentFactoryResolver, mediaAdvancedPropertiesCustom: MediaAdvancedPropertiesCustom);
    ngOnChanges(changes: SimpleChanges): void;
    ngOnInit(): void;
    onClick($event: Event): void;
    private loadMediaAdvancedPropertiesCustomComponent;
}
