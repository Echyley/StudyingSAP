/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * This service be used in order to display (or hide) a 'loading' overlay. The overlay should display on top of everything, preventing
 * the user from doing any action until the overlay gets hidden.
 */
export abstract class IWaitDialogService {
    /**
     * @param customLoadingMessageLocalizedKey The i18n key that corresponds to the message to be displayed. Default value `"se.wait.dialog.message"`.
     */
    showWaitModal(customLoadingMessageLocalizedKey?: string): void {
        'proxyFunction';
        return null;
    }

    hideWaitModal(): void {
        'proxyFunction';
        return null;
    }
}
