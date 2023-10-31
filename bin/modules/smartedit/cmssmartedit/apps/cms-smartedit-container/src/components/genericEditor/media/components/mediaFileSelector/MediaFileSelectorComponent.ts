/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    Optional,
    Inject,
    Type,
    Injector,
    ViewEncapsulation
} from '@angular/core';
import {
    MEDIA_FILE_SELECTOR_CUSTOM_TOKEN,
    MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN,
    MediaFileSelectorCustomInjector,
    MediaFileSelectorCustom
} from 'smarteditcommons';

enum MediaFileSelectionMode {
    'replace' = 'replace',
    'upload' = 'upload'
}

interface CustomComponent {
    component: Type<any>;
    injector: Injector;
}

@Component({
    selector: 'se-media-file-selector',
    templateUrl: './MediaFileSelectorComponent.html',
    styleUrls: ['./MediaFileSelectorComponent.scss'],
    encapsulation: ViewEncapsulation.None, // to style custom selector component
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MediaFileSelectorComponent implements OnInit {
    @Input() selectionMode?: MediaFileSelectionMode;
    @Input() labelI18nKey: string;
    @Input() acceptedFileTypes: string[];
    @Input() customClass?: string;
    @Input() disabled?: boolean;
    @Output() onFileSelect: EventEmitter<FileList | string> = new EventEmitter();

    public customComponent: CustomComponent | undefined;

    constructor(
        private injector: Injector,
        @Optional()
        @Inject(MEDIA_FILE_SELECTOR_CUSTOM_TOKEN)
        customSelector: MediaFileSelectorCustom
    ) {
        if (customSelector) {
            this.initCustomComponent(customSelector);
        }
    }

    ngOnInit(): void {
        this.disabled = this.disabled || false;
        this.customClass = this.customClass || '';
        this.selectionMode = this.selectionMode || MediaFileSelectionMode.replace;
    }

    public buildAcceptedFileTypesList(): string {
        return this.acceptedFileTypes.map((fileType) => `.${fileType}`).join(',');
    }

    public isReplaceMode(): boolean {
        return this.selectionMode === MediaFileSelectionMode.replace;
    }

    public onSelect(file: FileList | string): void {
        this.onFileSelect.emit(file);
    }

    private initCustomComponent({ component }: MediaFileSelectorCustom): void {
        this.customComponent = {
            component,
            injector: Injector.create({
                parent: this.injector,
                providers: [
                    {
                        provide: MEDIA_FILE_SELECTOR_CUSTOM_INJECTOR_TOKEN,
                        useValue: {
                            onSelect: (file: FileList | string): void => this.onSelect(file)
                        } as MediaFileSelectorCustomInjector
                    }
                ]
            })
        };
    }
}
