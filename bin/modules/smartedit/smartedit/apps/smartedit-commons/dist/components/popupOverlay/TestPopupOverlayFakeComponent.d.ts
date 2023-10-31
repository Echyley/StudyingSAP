import { PopupOverlayComponent } from './PopupOverlayComponent';
import { PopupOverlayConfig } from './PopupOverlayConfig';
export declare const ON_SHOW = "On Show";
export declare class TestPopupOverlayFakeComponent {
    popupOverlayComponentRef: PopupOverlayComponent;
    popupConfig: PopupOverlayConfig;
    isPopupOpened: boolean;
    message: string;
    trigger: [];
    constructor();
    setMessage(message: string): void;
    hidePopup(): void;
    handleOnShow(): void;
}
