/**
 * An object representing the backend response for any erroy of type "ValidationError".
 */
export interface ValidationError {
    language?: string;
    message: string;
    reason: string;
    subject: string;
    subjectType: string;
    errorCode: string;
    type: 'ValidationError';
}
