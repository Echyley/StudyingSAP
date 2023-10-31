import { ISettingsService } from '../../interfaces';
import { FileReaderService } from '../file-reader';
export declare class FileMimeTypeService {
    private fileReaderService;
    private settingsService;
    constructor(fileReaderService: FileReaderService, settingsService: ISettingsService);
    isFileMimeTypeValid(file: File | Blob): Promise<boolean>;
    /**
     *  Mimetype is valid when starting bytes of the file are matching the Mimetype byte pattern.
     *  For example. 89 50 4E 47 is a png file, so if the first 4 bytes are 89504E47 it is recognized as a png file.
     *
     *  Read more on File Signatures and Image Type Pattern Matching
     *  - https://en.wikipedia.org/wiki/List_of_file_signatures
     *  - https://mimesniff.spec.whatwg.org/#image-type-pattern-matching-algorithm
     */
    private validateMimeTypeFromFile;
}
