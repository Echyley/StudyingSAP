/**
 * @ngdoc service
 * @name @smartutils.services:CryptographicUtils
 *
 * @description
 * utility service around Cryptographic operations.
 */
export declare class CryptographicUtils {
    /**
     * @ngdoc method
     * @name @smartutils.services:CryptographicUtils#sha1Hash
     * @methodOf @smartutils.services:CryptographicUtils
     *
     * @description
     * A utility function that takes an input string and provides a cryptographic SHA1 hash value.
     *
     * @param {String} data The input string to be encrypted.
     * @returns {String} the encrypted hashed result.
     */
    sha1Hash(data: string): string;
    aesBase64Encrypt(base64EncodedMessage: string, secretPassphrase: string): string;
    aesDecrypt(encryptedMessage: string, secretPassphrase: string): string;
}
