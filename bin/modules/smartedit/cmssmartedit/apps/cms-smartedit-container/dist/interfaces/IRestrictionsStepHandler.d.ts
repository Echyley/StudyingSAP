export interface IRestrictionsStepHandler {
    hideStep(): void;
    showStep(): void;
    isStepValid(): boolean;
    save?(): Promise<void>;
    getStepId(): string;
    goToStep(): void;
}
