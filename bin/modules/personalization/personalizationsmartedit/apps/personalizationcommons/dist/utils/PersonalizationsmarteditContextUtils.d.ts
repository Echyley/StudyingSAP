import { IPersonalizationsmarteditContextObject } from '../dtos';
import { IPersonalizationsmarteditContextService, IPersonalizationsmarteditPreviewService } from '../services';
export declare class PersonalizationsmarteditContextUtils {
    getContextObject(): IPersonalizationsmarteditContextObject;
    clearCustomizeContext(contextService: IPersonalizationsmarteditContextService): void;
    clearCustomizeContextAndReloadPreview(previewService: IPersonalizationsmarteditPreviewService, contextService: IPersonalizationsmarteditContextService): void;
    clearCombinedViewCustomizeContext(contextService: IPersonalizationsmarteditContextService): void;
    clearCombinedViewContext(contextService: IPersonalizationsmarteditContextService): void;
    clearCombinedViewContextAndReloadPreview(previewService: IPersonalizationsmarteditPreviewService, contextService: IPersonalizationsmarteditContextService): void;
}
