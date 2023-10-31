import { PipeTransform } from '@angular/core';
/**
 * Returns an array containing the items from the specified collection in reverse order.
 */
export declare class ReversePipe implements PipeTransform {
    transform<T>(value: T[]): T[] | undefined;
}
