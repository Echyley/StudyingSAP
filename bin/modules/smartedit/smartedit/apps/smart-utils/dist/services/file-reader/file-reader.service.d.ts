export declare class FileReaderService {
    read(file: File | Blob, config?: {
        onError: (ev: ProgressEvent<FileReader>) => any;
        onLoadEnd: (ev: ProgressEvent<FileReader>) => any;
    }): FileReader;
}
