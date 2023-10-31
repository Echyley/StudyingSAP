import { Observable } from 'rxjs';
export declare enum ModalButtonStyle {
    Default = "transparent",
    Primary = "emphasized"
}
export declare enum ModalButtonAction {
    Close = "close",
    Dismiss = "dismiss",
    None = "none"
}
export interface ModalButtonOptions {
    /**
     * The key used to identify button.
     */
    id: string;
    /**
     * Translation key.
     */
    label: string;
    /**
     * Method triggered when button is pressed.
     */
    callback?: () => Observable<any>;
    /**
     * Used to define what action button should perform after click.
     */
    action?: ModalButtonAction;
    /**
     * Property used to style the button.
     */
    style?: ModalButtonStyle;
    compact?: boolean;
    /**
     * Decides whether button is disabled or not.
     */
    disabled?: boolean;
    disabledFn?: () => boolean;
}
