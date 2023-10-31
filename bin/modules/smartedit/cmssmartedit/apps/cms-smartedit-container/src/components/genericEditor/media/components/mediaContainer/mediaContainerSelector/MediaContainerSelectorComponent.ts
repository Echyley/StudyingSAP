/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import {
    ActionableSearchItem,
    GenericEditorField,
    IGenericEditorDropdownSelectedOptionEventData,
    LINKED_DROPDOWN,
    SelectReset,
    SystemEventService
} from 'smarteditcommons';
import { CMS_CLEAR_MEDIA_CONTAINER_SELECTED_OPTION } from '../../../../../../constants';
import { MediaContainer } from '../MediaContainerComponent';
import { MediaContainerSelectorItemComponent } from '../mediaContainerSelectorItem';

type DropdownItem = IGenericEditorDropdownSelectedOptionEventData<MediaContainer>;

const MediaContainersUri =
    '/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/mediacontainers';
const FieldQualifier = 'mediaContainer';

@Component({
    selector: 'se-media-container-selector',
    templateUrl: './MediaContainerSelectorComponent.html',
    styleUrls: ['./MediaContainerSelectorComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None
})
export class MediaContainerSelectorComponent implements OnInit, OnDestroy {
    /** Affix used to create a unique event name of dropdown events such as Select or Create. */
    @Input() eventNameAffix: string;
    @Input() isAdvancedCloning: boolean;
    @Input() name: string;
    @Input() initialName: string;
    @Input() isEditable: boolean;

    @Output() nameChange: EventEmitter<string>;
    @Output() onCreate: EventEmitter<string>;
    @Output() onRemove: EventEmitter<void>;
    @Output() onSelect: EventEmitter<MediaContainer>;
    @Output() onCreationInProgressChange: EventEmitter<boolean>;

    public id: string;
    /** dropdown field. */
    public field: GenericEditorField;
    public mediaContainerNameModel: {
        [FieldQualifier]: string;
    };
    public reset: SelectReset;

    public actionableSearchItem: ActionableSearchItem;
    public itemComponent: typeof MediaContainerSelectorItemComponent;
    public creationInProgress: boolean;

    private unRegSelectMediaContainer: () => void;
    private unRegClearSelectMediaContainer: () => void;
    private unRegCreateMediaContainer: () => void;

    constructor(
        private readonly cdr: ChangeDetectorRef,
        private readonly systemEventService: SystemEventService
    ) {
        this.nameChange = new EventEmitter();
        this.onCreate = new EventEmitter();
        this.onRemove = new EventEmitter();
        this.onSelect = new EventEmitter();
        this.onCreationInProgressChange = new EventEmitter();

        this.field = {
            cmsStructureType: 'EditableDropdown',
            qualifier: FieldQualifier,
            required: true,
            uri: MediaContainersUri,
            idAttribute: 'qualifier',
            editable: true,
            paged: true
        };

        this.creationInProgress = false;
        this.itemComponent = MediaContainerSelectorItemComponent;
    }

    ngOnInit(): void {
        this.id = `${FieldQualifier}_${this.eventNameAffix}`;
        this.field.editable = this.isEditable;

        this.mediaContainerNameModel = {
            [FieldQualifier]: this.initialName
        };

        // name of the event that is triggered when user selects existing media container
        const selectMediaContainerEventName = `${this.id}${LINKED_DROPDOWN}`;
        this.unRegSelectMediaContainer = this.systemEventService.subscribe(
            selectMediaContainerEventName,
            (_eventId, selectedItem: DropdownItem) => this.onSelectItem(selectedItem)
        );

        // name of the event that is clear selected media container
        const clearSelectMediaContainerEventName = `${this.id}_${CMS_CLEAR_MEDIA_CONTAINER_SELECTED_OPTION}`;
        this.unRegClearSelectMediaContainer = this.systemEventService.subscribe(
            clearSelectMediaContainerEventName,
            (_eventId, date) => this.clearSelectOption()
        );

        // name of the event that is triggered when user clicks on "Create" new media container button in dropdown.
        const createMediaContainerEventName = `CREATE_MEDIA_CONTAINER_BUTTON_PRESSED_EVENT_${this.eventNameAffix}`;
        this.actionableSearchItem = {
            eventId: createMediaContainerEventName
        };
        this.unRegCreateMediaContainer = this.systemEventService.subscribe(
            createMediaContainerEventName,
            (_eventId, name: string) => this.onCreateMediaContainer(name)
        );
    }

    ngOnDestroy(): void {
        this.unRegSelectMediaContainer();
        this.unRegCreateMediaContainer();
        this.unRegClearSelectMediaContainer();
    }

    public onNameChange(name: string): void {
        this.nameChange.emit(name);
    }

    public isNameReadOnly(): boolean {
        return !this.isAdvancedCloning && !this.creationInProgress && this.isSelected();
    }

    public isSelected(): boolean {
        return !!this.mediaContainerNameModel[FieldQualifier];
    }

    private onSelectItem({ optionObject: selectedMediaContainer }: DropdownItem): void {
        this.setCreationInProgressAndEmit(false);

        if (!this.mediaContainerNameModel.mediaContainer) {
            // clear selected mediaContainer option.
            this.onSelect.emit({
                catalogVersion: undefined,
                medias: {},
                qualifier: undefined,
                mediaContainerUuid: undefined
            });
            this.onRemove.emit();
        } else if (!!selectedMediaContainer) {
            this.onSelect.emit(selectedMediaContainer);
        }

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private onCreateMediaContainer(name: string): void {
        // clear selected mediaContainer option.
        this.clearSelectOption();
        this.setCreationInProgressAndEmit(true);

        this.onCreate.emit(name);

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    private setCreationInProgressAndEmit(isInProgress: boolean): void {
        this.creationInProgress = isInProgress;

        this.onCreationInProgressChange.emit(isInProgress);
    }

    private clearSelectOption(): void {
        this.initialName = undefined;
        this.resetSelector();
    }

    private resetSelector(): void {
        if (typeof this.reset === 'function') {
            this.reset(true);
        }
    }
}
