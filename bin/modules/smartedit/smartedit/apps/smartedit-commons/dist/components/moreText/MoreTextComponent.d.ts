import { TranslateService } from '@ngx-translate/core';
import { TextTruncateService } from '../../services';
export declare class MoreTextComponent {
    private textTruncateService;
    private translate;
    text: string;
    limit?: number;
    moreLabelI18nKey?: string;
    lessLabelI18nKey?: string;
    ellipsis?: string;
    capitalizeLabel: boolean;
    linkLabel: string;
    isTruncated: boolean;
    private showingMore;
    private moreLabel;
    private lessLabel;
    private truncatedText;
    constructor(textTruncateService: TextTruncateService, translate: TranslateService);
    ngOnInit(): void;
    showHideMoreText(): void;
    private translateLabels;
}
