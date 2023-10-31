import { TypedMap } from 'smarteditcommons';
export declare class BaseContextualMenuComponent {
    protected remainOpenMap: TypedMap<boolean>;
    isHybrisIcon(icon: string): boolean;
    setRemainOpen(key: string, remainOpen: boolean): void;
    showOverlay(active: boolean): boolean;
}
