export interface BaseAction {
    type: string;
    code: string;
}
export interface Action {
    action: BaseAction;
    status: string;
}
