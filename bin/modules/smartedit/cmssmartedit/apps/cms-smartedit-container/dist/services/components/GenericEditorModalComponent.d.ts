import { CrossFrameEventService, ModalManagerService, GenericEditorAPI, GenericEditorOnSubmitResponse, GenericEditorStructure, IConfirmationModalService, Payload, SystemEventService, IGenericEditorModalServiceComponent } from 'smarteditcommons';
export interface GenericEditorModalComponentControls {
    isDirty: () => boolean;
    reset: () => Promise<void>;
    submit: () => Promise<GenericEditorOnSubmitResponse>;
    genericEditorAPI: GenericEditorAPI;
}
export interface GenericEditorUnrelatedErrorMessage {
    message: string;
    subject: string;
}
export interface GenericEditorUnrelatedErrorEvent {
    sourceGenericEditorId: string;
    messages: GenericEditorUnrelatedErrorMessage[];
}
export interface StructureChangeEvent {
    content: Payload;
    structureApiMode: string;
    structure: GenericEditorStructure;
    editorId: string;
}
export declare class GenericEditorModalComponent {
    private readonly modalManager;
    private readonly confirmationModalService;
    private readonly crossFrameEventService;
    private readonly systemEventService;
    data: IGenericEditorModalServiceComponent;
    controls: GenericEditorModalComponentControls;
    editorStackId: string;
    genericEditorId: string;
    structure: GenericEditorStructure;
    structureApi: string;
    contentApi: string;
    showDisclaimer: boolean;
    private readonly STRUCTURE_API_BASE_URL;
    private saveCallback;
    private errorCallback;
    private changeStructureEventListener;
    private unrelatedValidationErrorsEvent;
    private isSetupCompleted;
    private isReset;
    private isEscapeInProgress;
    constructor(modalManager: ModalManagerService, confirmationModalService: IConfirmationModalService, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService);
    onEscapeDown(): void;
    getApi(genericEditorAPI: GenericEditorAPI): void;
    onSave(): Promise<GenericEditorOnSubmitResponse>;
    onCancel(): Promise<void>;
    setup(): void;
    /**
     * Method makes each attribute of the structure non editable.
     */
    makeStructureReadOnly(structure: GenericEditorStructure): GenericEditorStructure;
    /**
     * Method verifies whether the generic editor is in read only mode or not.
     * Returns TRUE if the generic editor is in read only mode, FALSE otherwise.
     */
    isGenericEditorInReadOnlyMode(): boolean;
    /**
     * Method returns generic editor content object.
     */
    getGenericEditorContent(): Payload;
    /**
     * Method verifies whether the generic editor is in dirty state or not.
     * Returns TRUE if the generic editor is in dirty state, FALSE otherwise.
     */
    genericEditorIsDirty(): boolean;
    onChangeStructureEvent(eventId: string, payload: StructureChangeEvent): void;
    onUnrelatedErrors(eventId: string, eventData: GenericEditorUnrelatedErrorEvent): void;
    /**
     * Converts the structure api url to read only mode. All fields return in read only mode will not be editable.
     */
    applyReadOnlyModeToStructureApiUrl(structureApiUrl: string): string;
    getStructureApiByMode(structureApiMode: string): string;
    removeEventListeners(): void;
    ngOnInit(): void;
    private markAsReset;
    private onEscapeClicked;
}
