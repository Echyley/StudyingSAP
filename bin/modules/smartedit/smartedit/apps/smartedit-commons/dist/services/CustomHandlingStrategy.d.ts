import { UrlHandlingStrategy, UrlTree } from '@angular/router/router';
export declare class CustomHandlingStrategy implements UrlHandlingStrategy {
    shouldProcessUrl(url: UrlTree): boolean;
    extract(url: UrlTree): UrlTree;
    merge(newUrlPart: UrlTree, rawUrl: UrlTree): UrlTree;
}
