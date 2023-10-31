/**
 * Provides an abstract extensible user tracking service. Used to manage and perform actions to either the SmartEdit
 * application or the SmartEdit container.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
export declare abstract class IUserTrackingService {
    initConfiguration(): Promise<void>;
    trackingUserAction(functionality: string, key: string): void;
}
