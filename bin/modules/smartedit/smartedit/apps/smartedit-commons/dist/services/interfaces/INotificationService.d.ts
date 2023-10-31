export interface INotificationConfiguration {
    id: string;
    /**
     * Component class name, decorated with @SeCustomComponent.
     *
     * Component must be also registered in @NgModule entryComponents array.
     *
     *
     * ### Example
     *
     *      \@SeCustomComponent()
     *      \@Component({
     *          selector: 'se-my-custom-component',
     *          templateUrl: './SeMyComponent.html'
     *      })
     *      export class MyCustomComponent {}
     *
     *      componentName = 'MyCustomComponent'
     *      or
     *      componentName = MyCustomComponent.name
     */
    componentName: string;
}
export declare type INotificationPush = INotificationConfiguration;
export interface INotificationRemove {
    id: string;
}
/**
 * INotificationService provides a service to display visual cues to inform
 * the user of the state of the application in the container or the iFramed application.
 * The interface defines the methods required to manage notifications that are to be displayed to the user.
 */
export declare abstract class INotificationService {
    /**
     * This method creates a new notification based on the given configuration and
     * adds it to the top of the list.
     *
     * The configuration must contain either one of componentName, template or templateUrl.
     *
     * ### Throws
     *
     * - Throws An error if no configuration is given.
     * - Throws An error if the configuration does not contain a unique identifier.
     * - Throws An error if the configuration's unique identifier is an empty string.
     * - Throws An error if the configuration does not contain a componenName, template or templateUrl.
     * - Throws An error if the configuration contains more than one template type.
     */
    pushNotification(configuration: INotificationConfiguration): PromiseLike<void>;
    /**
     * Moves the notification with the given ID from the list.
     */
    removeNotification(notificationId: string): PromiseLike<void>;
    /**
     * This method removes all notifications.
     */
    removeAllNotifications(): PromiseLike<void>;
}
