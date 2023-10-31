/**
 * A map that contains the necessary site and catalog information for CMS services and directives.
 * It contains the following keys:
 *
 * `CONTEXT_SITE_ID | PAGE_CONTEXT_SITE_ID` For the site uid
 *
 * `CONTEXT_CATALOG | PAGE_CONTEXT_CATALOG` For the catalog uid
 *
 * `CONTEXT_CATALOG_VERSION | PAGE_CONTEXT_CATALOG_VERSION` For the catalog version.
 *
 * See `web/app/common/utils/smarteditconstants.ts`.
 */
export interface IUriContext {
    [index: string]: string;
}
