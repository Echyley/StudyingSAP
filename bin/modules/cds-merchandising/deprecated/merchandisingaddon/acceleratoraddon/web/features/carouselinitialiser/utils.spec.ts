import { sanitizeUrl } from "./utils";

describe('utils.sanitizeUrl', () => {

    it('URL should be sanitized', () => {
        expect(sanitizeUrl("http://localhost/path/to/file")).toBe("http://localhost/path/to/file");
        expect(sanitizeUrl("https://localhost/path/to/file")).toBe("https://localhost/path/to/file");
        expect(sanitizeUrl("ftp://localhost/path/to/file")).toBe("ftp://localhost/path/to/file");
        expect(sanitizeUrl("ftps://localhost/path/to/file")).toBe("ftps://localhost/path/to/file");
        expect(sanitizeUrl("javascript://localhost/path/to/file")).toBe("#");
        expect(sanitizeUrl("jAvasCrIPT://localhost/path/to/file")).toBe("#");
        expect(sanitizeUrl(null)).toBe("#");
        expect(sanitizeUrl("")).toBe("#");
    });

    it("replaces javascript urls with #", () => {
        expect(sanitizeUrl("javascript:alert(document.domain)")).toBe(
            "#"
        );
    });

    it("disregards capitalization for JavaScript urls", () => {
        expect(sanitizeUrl("jAvasCrIPT:alert(document.domain)")).toBe(
            "#"
        );
    });

    it("ignores ctrl characters in javascript urls", () => {
        expect(
            sanitizeUrl(decodeURIComponent("JaVaScRiP%0at:alert(document.domain)"))
        ).toBe("#");
    });

    it("replaces javascript urls with # when javascript url begins with %20", () => {
        expect(sanitizeUrl("%20%20%20%20javascript:alert(document.domain)")).toBe(
            "#"
        );
    });

    it("replaces javascript urls with # when javascript url begins with s", () => {
        expect(sanitizeUrl("    javascript:alert(document.domain)")).toBe(
            "#"
        );
    });

    it("does not replace javascript: if it is not in the scheme of the URL", () => {
        expect(sanitizeUrl("http://example.com#myjavascript:foo")).toBe(
            "http://example.com#myjavascript:foo"
        );
    });

    it("replaces data urls with #", () => {
        expect(
            sanitizeUrl(
                "data:text/html;base64,PH%3Cscript%3Ealert(document.domain)%3C/script%3E"
            )
        ).toBe("#");
    });

    it("replaces data urls with # when data url begins with %20", () => {
        expect(
            sanitizeUrl(
                "%20%20%20%20data:text/html;base64,PH%3Cscript%3Ealert(document.domain)%3C/script%3E"
            )
        ).toBe("#");
    });

    it("replaces data urls with # when data url begins with s", () => {
        expect(
            sanitizeUrl(
                "    data:text/html;base64,PH%3Cscript%3Ealert(document.domain)%3C/script%3E"
            )
        ).toBe("#");
    });

    it("disregards capitalization for data urls", () => {
        expect(
            sanitizeUrl(
                "dAtA:text/html;base64,PH%3Cscript%3Ealert(document.domain)%3C/script%3E"
            )
        ).toBe("#");
    });

    it("ignores ctrl characters in data urls", () => {
        expect(
            sanitizeUrl(
                decodeURIComponent(
                    "dat%0aa:text/html;base64,PH%3Cscript%3Ealert(document.domain)%3C/script%3E"
                )
            )
        ).toBe("#");
    });

    it("replaces VBscript urls with #", () => {
        expect(sanitizeUrl("vbscript:msgbox('XSS')")).toBe("#");
    });

    it("disregards capitalization for VBscript urls", () => {
        expect(sanitizeUrl("vbScrIpT:mSGBOX('XSS')")).toBe("#");
    });

    it("ignores ctrl characters in VBscript urls", () => {
        expect(sanitizeUrl(decodeURIComponent("VbScRiP%0at:msgbox('XSS')"))).toBe(
            "#"
        );
    });

    it("replaces VBscript urls with # when VBscript url begins with %20", () => {
        expect(sanitizeUrl("%20%20%20%20vbscript:msgbox('XSS')")).toBe(
            "#"
        );
    });

    it("replaces VBScript urls with # when VBscript url begins with s", () => {
        expect(sanitizeUrl("    vbscript:msgbox('XSS')")).toBe("#");
    });

    it("does not replace VBscript: if it is not in the scheme of the URL", () => {
        expect(sanitizeUrl("http://example.com#whatisvbscript:foo")).toBe(
            "http://example.com#whatisvbscript:foo"
        );
    });

    it("does not alter http URLs", () => {
        expect(sanitizeUrl("http://example.com/path/to:something")).toBe(
            "http://example.com/path/to:something"
        );
    });

    it("does not alter http URLs with ports", () => {
        expect(sanitizeUrl("http://example.com:4567/path/to:something")).toBe(
            "http://example.com:4567/path/to:something"
        );
    });

    it("does not alter https URLs", () => {
        expect(sanitizeUrl("https://example.com")).toBe("https://example.com");
    });

    it("does not alter https URLs with ports", () => {
        expect(sanitizeUrl("https://example.com:4567/path/to:something")).toBe(
            "https://example.com:4567/path/to:something"
        );
    });

    it("does not alter relative-path reference URLs", () => {
        expect(sanitizeUrl("./path/to/my.json")).toBe("./path/to/my.json");
    });

    it("does not alter absolute-path reference URLs", () => {
        expect(sanitizeUrl("/path/to/my.json")).toBe("/path/to/my.json");
    });

    it("does not alter protocol-less network-path URLs", () => {
        expect(sanitizeUrl("//google.com/robots.txt")).toBe(
            "//google.com/robots.txt"
        );
    });

    it("does not alter protocol-less URLs", () => {
        expect(sanitizeUrl("www.example.com")).toBe("www.example.com");
    });

    it("does not alter deep-link urls", () => {
        expect(sanitizeUrl("com.braintreepayments.demo://example")).toBe(
            "com.braintreepayments.demo://example"
        );
    });

    it("does not alter mailto urls", () => {
        expect(sanitizeUrl("mailto:test@example.com?subject=hello+world")).toBe(
            "mailto:test@example.com?subject=hello+world"
        );
    });

    it("does not alter urls with accented characters", () => {
        expect(sanitizeUrl("www.example.com/with-áccêntš")).toBe(
            "www.example.com/with-áccêntš"
        );
    });

    it("replaces blank urls with #", () => {
        expect(sanitizeUrl("")).toBe("#");
    });

    it("replaces null values with #", () => {
        expect(sanitizeUrl(null)).toBe("#");
    });

    it("replaces undefined values with #", () => {
        expect(sanitizeUrl()).toBe("#");
    });

    it("removes whitespace from urls", () => {
        expect(sanitizeUrl("   http://example.com/path/to:something    ")).toBe(
            "http://example.com/path/to:something"
        );
    });

});

