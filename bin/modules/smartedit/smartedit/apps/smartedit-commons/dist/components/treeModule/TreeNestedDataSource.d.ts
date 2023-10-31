import { Observable } from 'rxjs';
export declare class TreeNestedDataSource<T> {
    private _data;
    get(): Observable<T[]>;
    set(data: T[]): void;
}
