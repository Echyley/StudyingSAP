import { TranslateService } from '@ngx-translate/core';
import { Payload, SchemaCompilerService } from '@smart/utils';
import { GenericEditorState } from '../models';
import { GenericEditorSchema } from '../types';
import { EditorFieldMappingService } from './EditorFieldMappingService';
import { GenericEditorTabService } from './GenericEditorTabService';
/**
 * @internal
 * GenericEditorStateBuilderService generates a GenericEditorState.
 */
export declare class GenericEditorStateBuilderService {
    /** @internal */
    private editorFieldMappingService;
    /** @internal */
    private genericEditorTabService;
    /** @internal */
    private translateService;
    /** @internal */
    private schemaCompiler;
    constructor(
    /** @internal */
    editorFieldMappingService: EditorFieldMappingService, 
    /** @internal */
    genericEditorTabService: GenericEditorTabService, 
    /** @internal */
    translateService: TranslateService, 
    /** @internal */
    schemaCompiler: SchemaCompilerService);
    /**
     * Compiles a GenericEditorState from schema and data. Whenever a new state
     * is provided the entire form is recompiled.
     */
    buildState(data: Payload, schema: GenericEditorSchema): GenericEditorState;
    /** @internal */
    private _createMapper;
    /** @internal */
    private _fieldAdaptor;
}
