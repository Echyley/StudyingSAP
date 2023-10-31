import { CMSItem } from './CMSItem';
/**
 * @description
 * Interface for cms-page information
 */
export interface ICMSPage extends CMSItem {
    label?: string;
    type?: string | {
        [index: string]: string;
    };
    [index: string]: any;
    pageStatus: CMSPageStatus;
    approvalStatus: CmsApprovalStatus;
    displayStatus: string;
    template?: string;
    masterTemplate: string;
    masterTemplateId: string;
    title: {
        [index: string]: string;
    };
    defaultPage: boolean;
    restrictions: string[];
    identifier?: string;
    homepage: boolean;
    isPrimary?: boolean;
    primaryPage?: string | ICMSPage | null;
}
/** The unique identifier for the page type. */
export declare enum CMSPageTypes {
    ContentPage = "ContentPage",
    CategoryPage = "CategoryPage",
    ProductPage = "ProductPage",
    EmailPage = "EmailPage"
}
export declare enum CMSPageStatus {
    ACTIVE = "ACTIVE",
    DELETED = "DELETED"
}
export declare enum CmsApprovalStatus {
    APPROVED = "APPROVED",
    CHECK = "CHECK",
    UNAPPROVED = "UNAPPROVED"
}
