export declare enum MessageType {
    danger = "danger",
    info = "info",
    success = "success",
    warning = "warning"
}
/**
 *  This component provides contextual feedback messages for the user actions. To provide title and description for the se-essage
 *  use transcluded elements: se-message-title and se-message-description.
 *
 *  ### Example
 *
 *      <se-message>
 *          <div se-message-title>Title</div>
 *          <div se-message-description>Description</div>
 *      </se-message>
 */
export declare class MessageComponent {
    /**
     * Id for the component
     */
    messageId: string;
    /**
     * The type of the component (danger, info, success, warning). Default: info
     */
    type: MessageType;
    classes: string;
    ngOnInit(): void;
}
