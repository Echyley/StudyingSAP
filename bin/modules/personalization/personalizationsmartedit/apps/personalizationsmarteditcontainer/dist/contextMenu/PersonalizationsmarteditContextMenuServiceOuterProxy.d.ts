import { TranslateService } from '@ngx-translate/core';
import { ContextMenuDto, IPersonalizationsmarteditContextMenuServiceProxy, PersonalizationsmarteditMessageHandler } from 'personalizationcommons';
import { PersonalizationsmarteditContextService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditContextServiceOuter';
import { PersonalizationsmarteditRestService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService';
import { IEditorModalService, IModalService, IRenderService, LogService } from 'smarteditcommons';
export declare class PersonalizationsmarteditContextMenuServiceProxy extends IPersonalizationsmarteditContextMenuServiceProxy {
    private modalService;
    private renderService;
    private editorModalService;
    private personalizationsmarteditContextService;
    private personalizationsmarteditRestService;
    private personalizationsmarteditMessageHandler;
    private translateService;
    private logService;
    constructor(modalService: IModalService, renderService: IRenderService, editorModalService: IEditorModalService, personalizationsmarteditContextService: PersonalizationsmarteditContextService, personalizationsmarteditRestService: PersonalizationsmarteditRestService, personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler, translateService: TranslateService, logService: LogService);
    openDeleteAction(config: ContextMenuDto): Promise<void>;
    openAddAction(config: ContextMenuDto): Promise<void>;
    openEditAction(config: ContextMenuDto): Promise<void>;
    openEditComponentAction(config: ContextMenuDto): void;
    private deleteContext;
    private removeItemFromCollection;
    private addActionsToVariation;
    private confirmDelete;
}
