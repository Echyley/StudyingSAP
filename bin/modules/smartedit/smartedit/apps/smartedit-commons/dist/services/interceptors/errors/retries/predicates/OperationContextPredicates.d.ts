import { HttpErrorResponse } from '@angular/common/http';
export declare function operationContextInteractivePredicate(response: HttpErrorResponse, operationContext: string): boolean;
export declare function operationContextNonInteractivePredicate(response: HttpErrorResponse, operationContext: string): boolean;
export declare function operationContextCMSPredicate(response: HttpErrorResponse, operationContext: string): boolean;
export declare function operationContextToolingPredicate(response: HttpErrorResponse, operationContext: string): boolean;
