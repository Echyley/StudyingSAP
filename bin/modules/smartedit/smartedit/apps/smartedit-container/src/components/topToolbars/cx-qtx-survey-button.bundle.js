/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// upgrade qualtrix from 1.0 to 2.0.1
var Kt = Object.defineProperty,
    Xt = Object.defineProperties;
var te = Object.getOwnPropertyDescriptors;
var xt = Object.getOwnPropertySymbols;
var ee = Object.prototype.hasOwnProperty,
    se = Object.prototype.propertyIsEnumerable;
var X = (v, u, d) =>
        u in v
            ? Kt(v, u, { enumerable: !0, configurable: !0, writable: !0, value: d })
            : (v[u] = d),
    F = (v, u) => {
        for (var d in u || (u = {})) ee.call(u, d) && X(v, d, u[d]);
        if (xt) for (var d of xt(u)) se.call(u, d) && X(v, d, u[d]);
        return v;
    },
    z = (v, u) => Xt(v, te(u));
var At = (v, u, d) => (X(v, typeof u != 'symbol' ? u + '' : u, d), d);
var St = (v, u, d) =>
    new Promise((L, C) => {
        var V = (g) => {
                try {
                    I(d.next(g));
                } catch (y) {
                    C(y);
                }
            },
            D = (g) => {
                try {
                    I(d.throw(g));
                } catch (y) {
                    C(y);
                }
            },
            I = (g) => (g.done ? L(g.value) : Promise.resolve(g.value).then(V, D));
        I((d = d.apply(v, u)).next());
    });
(function () {
    'use strict';
    /**
     * @license
     * Copyright 2019 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ const v = window,
        u =
            v.ShadowRoot &&
            (v.ShadyCSS === void 0 || v.ShadyCSS.nativeShadow) &&
            'adoptedStyleSheets' in Document.prototype &&
            'replace' in CSSStyleSheet.prototype,
        d = Symbol(),
        L = new WeakMap();
    class C {
        constructor(t, e, s) {
            if (((this._$cssResult$ = !0), s !== d))
                throw Error('CSSResult is not constructable. Use `unsafeCSS` or `css` instead.');
            (this.cssText = t), (this.t = e);
        }
        get styleSheet() {
            let t = this.o;
            const e = this.t;
            if (u && t === void 0) {
                const s = e !== void 0 && e.length === 1;
                s && (t = L.get(e)),
                    t === void 0 &&
                        ((this.o = t = new CSSStyleSheet()).replaceSync(this.cssText),
                        s && L.set(e, t));
            }
            return t;
        }
        toString() {
            return this.cssText;
        }
    }
    const V = (r) => new C(typeof r == 'string' ? r : r + '', void 0, d),
        D = (r, ...t) => {
            const e =
                r.length === 1
                    ? r[0]
                    : t.reduce(
                          (s, o, i) =>
                              s +
                              ((n) => {
                                  if (n._$cssResult$ === !0) return n.cssText;
                                  if (typeof n == 'number') return n;
                                  throw Error(
                                      "Value passed to 'css' function must be a 'css' function result: " +
                                          n +
                                          ". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security."
                                  );
                              })(o) +
                              r[i + 1],
                          r[0]
                      );
            return new C(e, r, d);
        },
        I = (r, t) => {
            u
                ? (r.adoptedStyleSheets = t.map((e) =>
                      e instanceof CSSStyleSheet ? e : e.styleSheet
                  ))
                : t.forEach((e) => {
                      const s = document.createElement('style'),
                          o = v.litNonce;
                      o !== void 0 && s.setAttribute('nonce', o),
                          (s.textContent = e.cssText),
                          r.appendChild(s);
                  });
        },
        g = u
            ? (r) => r
            : (r) =>
                  r instanceof CSSStyleSheet
                      ? ((t) => {
                            let e = '';
                            for (const s of t.cssRules) e += s.cssText;
                            return V(e);
                        })(r)
                      : r;
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var y;
    const N = window,
        tt = N.trustedTypes,
        wt = tt ? tt.emptyScript : '',
        et = N.reactiveElementPolyfillSupport,
        G = {
            toAttribute(r, t) {
                switch (t) {
                    case Boolean:
                        r = r ? wt : null;
                        break;
                    case Object:
                    case Array:
                        r = r == null ? r : JSON.stringify(r);
                }
                return r;
            },
            fromAttribute(r, t) {
                let e = r;
                switch (t) {
                    case Boolean:
                        e = r !== null;
                        break;
                    case Number:
                        e = r === null ? null : Number(r);
                        break;
                    case Object:
                    case Array:
                        try {
                            e = JSON.parse(r);
                        } catch (s) {
                            e = null;
                        }
                }
                return e;
            }
        },
        st = (r, t) => t !== r && (t == t || r == r),
        Z = { attribute: !0, type: String, converter: G, reflect: !1, hasChanged: st };
    class x extends HTMLElement {
        constructor() {
            super(),
                (this._$Ei = new Map()),
                (this.isUpdatePending = !1),
                (this.hasUpdated = !1),
                (this._$El = null),
                this.u();
        }
        static addInitializer(t) {
            var e;
            ((e = this.h) !== null && e !== void 0) || (this.h = []), this.h.push(t);
        }
        static get observedAttributes() {
            this.finalize();
            const t = [];
            return (
                this.elementProperties.forEach((e, s) => {
                    const o = this._$Ep(s, e);
                    o !== void 0 && (this._$Ev.set(o, s), t.push(o));
                }),
                t
            );
        }
        static createProperty(t, e = Z) {
            if (
                (e.state && (e.attribute = !1),
                this.finalize(),
                this.elementProperties.set(t, e),
                !e.noAccessor && !this.prototype.hasOwnProperty(t))
            ) {
                const s = typeof t == 'symbol' ? Symbol() : '__' + t,
                    o = this.getPropertyDescriptor(t, s, e);
                o !== void 0 && Object.defineProperty(this.prototype, t, o);
            }
        }
        static getPropertyDescriptor(t, e, s) {
            return {
                get() {
                    return this[e];
                },
                set(o) {
                    const i = this[t];
                    (this[e] = o), this.requestUpdate(t, i, s);
                },
                configurable: !0,
                enumerable: !0
            };
        }
        static getPropertyOptions(t) {
            return this.elementProperties.get(t) || Z;
        }
        static finalize() {
            if (this.hasOwnProperty('finalized')) return !1;
            this.finalized = !0;
            const t = Object.getPrototypeOf(this);
            if (
                (t.finalize(),
                (this.elementProperties = new Map(t.elementProperties)),
                (this._$Ev = new Map()),
                this.hasOwnProperty('properties'))
            ) {
                const e = this.properties,
                    s = [...Object.getOwnPropertyNames(e), ...Object.getOwnPropertySymbols(e)];
                for (const o of s) this.createProperty(o, e[o]);
            }
            return (this.elementStyles = this.finalizeStyles(this.styles)), !0;
        }
        static finalizeStyles(t) {
            const e = [];
            if (Array.isArray(t)) {
                const s = new Set(t.flat(1 / 0).reverse());
                for (const o of s) e.unshift(g(o));
            } else t !== void 0 && e.push(g(t));
            return e;
        }
        static _$Ep(t, e) {
            const s = e.attribute;
            return s === !1
                ? void 0
                : typeof s == 'string'
                ? s
                : typeof t == 'string'
                ? t.toLowerCase()
                : void 0;
        }
        u() {
            var t;
            (this._$E_ = new Promise((e) => (this.enableUpdating = e))),
                (this._$AL = new Map()),
                this._$Eg(),
                this.requestUpdate(),
                (t = this.constructor.h) === null || t === void 0 || t.forEach((e) => e(this));
        }
        addController(t) {
            var e, s;
            ((e = this._$ES) !== null && e !== void 0 ? e : (this._$ES = [])).push(t),
                this.renderRoot !== void 0 &&
                    this.isConnected &&
                    ((s = t.hostConnected) === null || s === void 0 || s.call(t));
        }
        removeController(t) {
            var e;
            (e = this._$ES) === null || e === void 0 || e.splice(this._$ES.indexOf(t) >>> 0, 1);
        }
        _$Eg() {
            this.constructor.elementProperties.forEach((t, e) => {
                this.hasOwnProperty(e) && (this._$Ei.set(e, this[e]), delete this[e]);
            });
        }
        createRenderRoot() {
            var t;
            const e =
                (t = this.shadowRoot) !== null && t !== void 0
                    ? t
                    : this.attachShadow(this.constructor.shadowRootOptions);
            return I(e, this.constructor.elementStyles), e;
        }
        connectedCallback() {
            var t;
            this.renderRoot === void 0 && (this.renderRoot = this.createRenderRoot()),
                this.enableUpdating(!0),
                (t = this._$ES) === null ||
                    t === void 0 ||
                    t.forEach((e) => {
                        var s;
                        return (s = e.hostConnected) === null || s === void 0 ? void 0 : s.call(e);
                    });
        }
        enableUpdating(t) {}
        disconnectedCallback() {
            var t;
            (t = this._$ES) === null ||
                t === void 0 ||
                t.forEach((e) => {
                    var s;
                    return (s = e.hostDisconnected) === null || s === void 0 ? void 0 : s.call(e);
                });
        }
        attributeChangedCallback(t, e, s) {
            this._$AK(t, s);
        }
        _$EO(t, e, s = Z) {
            var o;
            const i = this.constructor._$Ep(t, s);
            if (i !== void 0 && s.reflect === !0) {
                const n = (((o = s.converter) === null || o === void 0 ? void 0 : o.toAttribute) !==
                void 0
                    ? s.converter
                    : G
                ).toAttribute(e, s.type);
                (this._$El = t),
                    n == null ? this.removeAttribute(i) : this.setAttribute(i, n),
                    (this._$El = null);
            }
        }
        _$AK(t, e) {
            var s;
            const o = this.constructor,
                i = o._$Ev.get(t);
            if (i !== void 0 && this._$El !== i) {
                const n = o.getPropertyOptions(i),
                    c =
                        typeof n.converter == 'function'
                            ? { fromAttribute: n.converter }
                            : ((s = n.converter) === null || s === void 0
                                  ? void 0
                                  : s.fromAttribute) !== void 0
                            ? n.converter
                            : G;
                (this._$El = i), (this[i] = c.fromAttribute(e, n.type)), (this._$El = null);
            }
        }
        requestUpdate(t, e, s) {
            let o = !0;
            t !== void 0 &&
                (((s = s || this.constructor.getPropertyOptions(t)).hasChanged || st)(this[t], e)
                    ? (this._$AL.has(t) || this._$AL.set(t, e),
                      s.reflect === !0 &&
                          this._$El !== t &&
                          (this._$EC === void 0 && (this._$EC = new Map()), this._$EC.set(t, s)))
                    : (o = !1)),
                !this.isUpdatePending && o && (this._$E_ = this._$Ej());
        }
        _$Ej() {
            return St(this, null, function* () {
                this.isUpdatePending = !0;
                try {
                    yield this._$E_;
                } catch (e) {
                    Promise.reject(e);
                }
                const t = this.scheduleUpdate();
                return t != null && (yield t), !this.isUpdatePending;
            });
        }
        scheduleUpdate() {
            return this.performUpdate();
        }
        performUpdate() {
            var t;
            if (!this.isUpdatePending) return;
            this.hasUpdated,
                this._$Ei && (this._$Ei.forEach((o, i) => (this[i] = o)), (this._$Ei = void 0));
            let e = !1;
            const s = this._$AL;
            try {
                (e = this.shouldUpdate(s)),
                    e
                        ? (this.willUpdate(s),
                          (t = this._$ES) === null ||
                              t === void 0 ||
                              t.forEach((o) => {
                                  var i;
                                  return (i = o.hostUpdate) === null || i === void 0
                                      ? void 0
                                      : i.call(o);
                              }),
                          this.update(s))
                        : this._$Ek();
            } catch (o) {
                throw ((e = !1), this._$Ek(), o);
            }
            e && this._$AE(s);
        }
        willUpdate(t) {}
        _$AE(t) {
            var e;
            (e = this._$ES) === null ||
                e === void 0 ||
                e.forEach((s) => {
                    var o;
                    return (o = s.hostUpdated) === null || o === void 0 ? void 0 : o.call(s);
                }),
                this.hasUpdated || ((this.hasUpdated = !0), this.firstUpdated(t)),
                this.updated(t);
        }
        _$Ek() {
            (this._$AL = new Map()), (this.isUpdatePending = !1);
        }
        get updateComplete() {
            return this.getUpdateComplete();
        }
        getUpdateComplete() {
            return this._$E_;
        }
        shouldUpdate(t) {
            return !0;
        }
        update(t) {
            this._$EC !== void 0 &&
                (this._$EC.forEach((e, s) => this._$EO(s, this[s], e)), (this._$EC = void 0)),
                this._$Ek();
        }
        updated(t) {}
        firstUpdated(t) {}
    }
    (x.finalized = !0),
        (x.elementProperties = new Map()),
        (x.elementStyles = []),
        (x.shadowRootOptions = { mode: 'open' }),
        et == null || et({ ReactiveElement: x }),
        ((y = N.reactiveElementVersions) !== null && y !== void 0
            ? y
            : (N.reactiveElementVersions = [])
        ).push('1.4.1');
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var W;
    const R = window,
        A = R.trustedTypes,
        rt = A ? A.createPolicy('lit-html', { createHTML: (r) => r }) : void 0,
        _ = `lit$${(Math.random() + '').slice(9)}$`,
        ot = '?' + _,
        Et = `<${ot}>`,
        S = document,
        O = (r = '') => S.createComment(r),
        T = (r) => r === null || (typeof r != 'object' && typeof r != 'function'),
        nt = Array.isArray,
        Pt = (r) => nt(r) || typeof (r == null ? void 0 : r[Symbol.iterator]) == 'function',
        B = /<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g,
        it = /-->/g,
        at = />/g,
        $ = RegExp(
            `>|[ 	
\f\r](?:([^\\s"'>=/]+)([ 	
\f\r]*=[ 	
\f\r]*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`,
            'g'
        ),
        lt = /'/g,
        ht = /"/g,
        ct = /^(?:script|style|textarea|title)$/i,
        kt = (r) => (t, ...e) => ({ _$litType$: r, strings: t, values: e }),
        J = kt(1),
        w = Symbol.for('lit-noChange'),
        b = Symbol.for('lit-nothing'),
        ut = new WeakMap(),
        Ct = (r, t, e) => {
            var s, o;
            const i = (s = e == null ? void 0 : e.renderBefore) !== null && s !== void 0 ? s : t;
            let n = i._$litPart$;
            if (n === void 0) {
                const c =
                    (o = e == null ? void 0 : e.renderBefore) !== null && o !== void 0 ? o : null;
                i._$litPart$ = n = new q(t.insertBefore(O(), c), c, void 0, e != null ? e : {});
            }
            return n._$AI(r), n;
        },
        E = S.createTreeWalker(S, 129, null, !1),
        It = (r, t) => {
            const e = r.length - 1,
                s = [];
            let o,
                i = t === 2 ? '<svg>' : '',
                n = B;
            for (let a = 0; a < e; a++) {
                const l = r[a];
                let f,
                    h,
                    p = -1,
                    m = 0;
                for (; m < l.length && ((n.lastIndex = m), (h = n.exec(l)), h !== null); )
                    (m = n.lastIndex),
                        n === B
                            ? h[1] === '!--'
                                ? (n = it)
                                : h[1] !== void 0
                                ? (n = at)
                                : h[2] !== void 0
                                ? (ct.test(h[2]) && (o = RegExp('</' + h[2], 'g')), (n = $))
                                : h[3] !== void 0 && (n = $)
                            : n === $
                            ? h[0] === '>'
                                ? ((n = o != null ? o : B), (p = -1))
                                : h[1] === void 0
                                ? (p = -2)
                                : ((p = n.lastIndex - h[2].length),
                                  (f = h[1]),
                                  (n = h[3] === void 0 ? $ : h[3] === '"' ? ht : lt))
                            : n === ht || n === lt
                            ? (n = $)
                            : n === it || n === at
                            ? (n = B)
                            : ((n = $), (o = void 0));
                const j = n === $ && r[a + 1].startsWith('/>') ? ' ' : '';
                i +=
                    n === B
                        ? l + Et
                        : p >= 0
                        ? (s.push(f), l.slice(0, p) + '$lit$' + l.slice(p) + _ + j)
                        : l + _ + (p === -2 ? (s.push(void 0), a) : j);
            }
            const c = i + (r[e] || '<?>') + (t === 2 ? '</svg>' : '');
            if (!Array.isArray(r) || !r.hasOwnProperty('raw'))
                throw Error('invalid template strings array');
            return [rt !== void 0 ? rt.createHTML(c) : c, s];
        };
    class U {
        constructor({ strings: t, _$litType$: e }, s) {
            let o;
            this.parts = [];
            let i = 0,
                n = 0;
            const c = t.length - 1,
                a = this.parts,
                [l, f] = It(t, e);
            if (((this.el = U.createElement(l, s)), (E.currentNode = this.el.content), e === 2)) {
                const h = this.el.content,
                    p = h.firstChild;
                p.remove(), h.append(...p.childNodes);
            }
            for (; (o = E.nextNode()) !== null && a.length < c; ) {
                if (o.nodeType === 1) {
                    if (o.hasAttributes()) {
                        const h = [];
                        for (const p of o.getAttributeNames())
                            if (p.endsWith('$lit$') || p.startsWith(_)) {
                                const m = f[n++];
                                if ((h.push(p), m !== void 0)) {
                                    const j = o.getAttribute(m.toLowerCase() + '$lit$').split(_),
                                        Q = /([.?@])?(.*)/.exec(m);
                                    a.push({
                                        type: 1,
                                        index: i,
                                        name: Q[2],
                                        strings: j,
                                        ctor:
                                            Q[1] === '.'
                                                ? Tt
                                                : Q[1] === '?'
                                                ? Ut
                                                : Q[1] === '@'
                                                ? qt
                                                : M
                                    });
                                } else a.push({ type: 6, index: i });
                            }
                        for (const p of h) o.removeAttribute(p);
                    }
                    if (ct.test(o.tagName)) {
                        const h = o.textContent.split(_),
                            p = h.length - 1;
                        if (p > 0) {
                            o.textContent = A ? A.emptyScript : '';
                            for (let m = 0; m < p; m++)
                                o.append(h[m], O()), E.nextNode(), a.push({ type: 2, index: ++i });
                            o.append(h[p], O());
                        }
                    }
                } else if (o.nodeType === 8)
                    if (o.data === ot) a.push({ type: 2, index: i });
                    else {
                        let h = -1;
                        for (; (h = o.data.indexOf(_, h + 1)) !== -1; )
                            a.push({ type: 7, index: i }), (h += _.length - 1);
                    }
                i++;
            }
        }
        static createElement(t, e) {
            const s = S.createElement('template');
            return (s.innerHTML = t), s;
        }
    }
    function P(r, t, e = r, s) {
        var o, i, n, c;
        if (t === w) return t;
        let a = s !== void 0 ? ((o = e._$Cl) === null || o === void 0 ? void 0 : o[s]) : e._$Cu;
        const l = T(t) ? void 0 : t._$litDirective$;
        return (
            (a == null ? void 0 : a.constructor) !== l &&
                ((i = a == null ? void 0 : a._$AO) === null || i === void 0 || i.call(a, !1),
                l === void 0 ? (a = void 0) : ((a = new l(r)), a._$AT(r, e, s)),
                s !== void 0
                    ? (((n = (c = e)._$Cl) !== null && n !== void 0 ? n : (c._$Cl = []))[s] = a)
                    : (e._$Cu = a)),
            a !== void 0 && (t = P(r, a._$AS(r, t.values), a, s)),
            t
        );
    }
    class Ot {
        constructor(t, e) {
            (this.v = []), (this._$AN = void 0), (this._$AD = t), (this._$AM = e);
        }
        get parentNode() {
            return this._$AM.parentNode;
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        p(t) {
            var e;
            const {
                    el: { content: s },
                    parts: o
                } = this._$AD,
                i = ((e = t == null ? void 0 : t.creationScope) !== null && e !== void 0
                    ? e
                    : S
                ).importNode(s, !0);
            E.currentNode = i;
            let n = E.nextNode(),
                c = 0,
                a = 0,
                l = o[0];
            for (; l !== void 0; ) {
                if (c === l.index) {
                    let f;
                    l.type === 2
                        ? (f = new q(n, n.nextSibling, this, t))
                        : l.type === 1
                        ? (f = new l.ctor(n, l.name, l.strings, this, t))
                        : l.type === 6 && (f = new Lt(n, this, t)),
                        this.v.push(f),
                        (l = o[++a]);
                }
                c !== (l == null ? void 0 : l.index) && ((n = E.nextNode()), c++);
            }
            return i;
        }
        m(t) {
            let e = 0;
            for (const s of this.v)
                s !== void 0 &&
                    (s.strings !== void 0
                        ? (s._$AI(t, s, e), (e += s.strings.length - 2))
                        : s._$AI(t[e])),
                    e++;
        }
    }
    class q {
        constructor(t, e, s, o) {
            var i;
            (this.type = 2),
                (this._$AH = b),
                (this._$AN = void 0),
                (this._$AA = t),
                (this._$AB = e),
                (this._$AM = s),
                (this.options = o),
                (this._$C_ =
                    (i = o == null ? void 0 : o.isConnected) === null || i === void 0 || i);
        }
        get _$AU() {
            var t, e;
            return (e = (t = this._$AM) === null || t === void 0 ? void 0 : t._$AU) !== null &&
                e !== void 0
                ? e
                : this._$C_;
        }
        get parentNode() {
            let t = this._$AA.parentNode;
            const e = this._$AM;
            return e !== void 0 && t.nodeType === 11 && (t = e.parentNode), t;
        }
        get startNode() {
            return this._$AA;
        }
        get endNode() {
            return this._$AB;
        }
        _$AI(t, e = this) {
            (t = P(this, t, e)),
                T(t)
                    ? t === b || t == null || t === ''
                        ? (this._$AH !== b && this._$AR(), (this._$AH = b))
                        : t !== this._$AH && t !== w && this.$(t)
                    : t._$litType$ !== void 0
                    ? this.T(t)
                    : t.nodeType !== void 0
                    ? this.k(t)
                    : Pt(t)
                    ? this.O(t)
                    : this.$(t);
        }
        S(t, e = this._$AB) {
            return this._$AA.parentNode.insertBefore(t, e);
        }
        k(t) {
            this._$AH !== t && (this._$AR(), (this._$AH = this.S(t)));
        }
        $(t) {
            this._$AH !== b && T(this._$AH)
                ? (this._$AA.nextSibling.data = t)
                : this.k(S.createTextNode(t)),
                (this._$AH = t);
        }
        T(t) {
            var e;
            const { values: s, _$litType$: o } = t,
                i =
                    typeof o == 'number'
                        ? this._$AC(t)
                        : (o.el === void 0 && (o.el = U.createElement(o.h, this.options)), o);
            if (((e = this._$AH) === null || e === void 0 ? void 0 : e._$AD) === i) this._$AH.m(s);
            else {
                const n = new Ot(i, this),
                    c = n.p(this.options);
                n.m(s), this.k(c), (this._$AH = n);
            }
        }
        _$AC(t) {
            let e = ut.get(t.strings);
            return e === void 0 && ut.set(t.strings, (e = new U(t))), e;
        }
        O(t) {
            nt(this._$AH) || ((this._$AH = []), this._$AR());
            const e = this._$AH;
            let s,
                o = 0;
            for (const i of t)
                o === e.length
                    ? e.push((s = new q(this.S(O()), this.S(O()), this, this.options)))
                    : (s = e[o]),
                    s._$AI(i),
                    o++;
            o < e.length && (this._$AR(s && s._$AB.nextSibling, o), (e.length = o));
        }
        _$AR(t = this._$AA.nextSibling, e) {
            var s;
            for (
                (s = this._$AP) === null || s === void 0 || s.call(this, !1, !0, e);
                t && t !== this._$AB;

            ) {
                const o = t.nextSibling;
                t.remove(), (t = o);
            }
        }
        setConnected(t) {
            var e;
            this._$AM === void 0 &&
                ((this._$C_ = t), (e = this._$AP) === null || e === void 0 || e.call(this, t));
        }
    }
    class M {
        constructor(t, e, s, o, i) {
            (this.type = 1),
                (this._$AH = b),
                (this._$AN = void 0),
                (this.element = t),
                (this.name = e),
                (this._$AM = o),
                (this.options = i),
                s.length > 2 || s[0] !== '' || s[1] !== ''
                    ? ((this._$AH = Array(s.length - 1).fill(new String())), (this.strings = s))
                    : (this._$AH = b);
        }
        get tagName() {
            return this.element.tagName;
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        _$AI(t, e = this, s, o) {
            const i = this.strings;
            let n = !1;
            if (i === void 0)
                (t = P(this, t, e, 0)),
                    (n = !T(t) || (t !== this._$AH && t !== w)),
                    n && (this._$AH = t);
            else {
                const c = t;
                let a, l;
                for (t = i[0], a = 0; a < i.length - 1; a++)
                    (l = P(this, c[s + a], e, a)),
                        l === w && (l = this._$AH[a]),
                        n || (n = !T(l) || l !== this._$AH[a]),
                        l === b ? (t = b) : t !== b && (t += (l != null ? l : '') + i[a + 1]),
                        (this._$AH[a] = l);
            }
            n && !o && this.P(t);
        }
        P(t) {
            t === b
                ? this.element.removeAttribute(this.name)
                : this.element.setAttribute(this.name, t != null ? t : '');
        }
    }
    class Tt extends M {
        constructor() {
            super(...arguments), (this.type = 3);
        }
        P(t) {
            this.element[this.name] = t === b ? void 0 : t;
        }
    }
    const Bt = A ? A.emptyScript : '';
    class Ut extends M {
        constructor() {
            super(...arguments), (this.type = 4);
        }
        P(t) {
            t && t !== b
                ? this.element.setAttribute(this.name, Bt)
                : this.element.removeAttribute(this.name);
        }
    }
    class qt extends M {
        constructor(t, e, s, o, i) {
            super(t, e, s, o, i), (this.type = 5);
        }
        _$AI(t, e = this) {
            var s;
            if ((t = (s = P(this, t, e, 0)) !== null && s !== void 0 ? s : b) === w) return;
            const o = this._$AH,
                i =
                    (t === b && o !== b) ||
                    t.capture !== o.capture ||
                    t.once !== o.once ||
                    t.passive !== o.passive,
                n = t !== b && (o === b || i);
            i && this.element.removeEventListener(this.name, this, o),
                n && this.element.addEventListener(this.name, this, t),
                (this._$AH = t);
        }
        handleEvent(t) {
            var e, s;
            typeof this._$AH == 'function'
                ? this._$AH.call(
                      (s = (e = this.options) === null || e === void 0 ? void 0 : e.host) !==
                          null && s !== void 0
                          ? s
                          : this.element,
                      t
                  )
                : this._$AH.handleEvent(t);
        }
    }
    class Lt {
        constructor(t, e, s) {
            (this.element = t),
                (this.type = 6),
                (this._$AN = void 0),
                (this._$AM = e),
                (this.options = s);
        }
        get _$AU() {
            return this._$AM._$AU;
        }
        _$AI(t) {
            P(this, t);
        }
    }
    const dt = R.litHtmlPolyfillSupport;
    dt == null || dt(U, q),
        ((W = R.litHtmlVersions) !== null && W !== void 0 ? W : (R.litHtmlVersions = [])).push(
            '2.3.1'
        );
    /**
     * @license
     * Copyright 2017 Google LLC
     * SPDX-License-Identifier: BSD-3-Clause
     */ var Y, K;
    class k extends x {
        constructor() {
            super(...arguments), (this.renderOptions = { host: this }), (this._$Do = void 0);
        }
        createRenderRoot() {
            var t, e;
            const s = super.createRenderRoot();
            return (
                ((t = (e = this.renderOptions).renderBefore) !== null && t !== void 0) ||
                    (e.renderBefore = s.firstChild),
                s
            );
        }
        update(t) {
            const e = this.render();
            this.hasUpdated || (this.renderOptions.isConnected = this.isConnected),
                super.update(t),
                (this._$Do = Ct(e, this.renderRoot, this.renderOptions));
        }
        connectedCallback() {
            var t;
            super.connectedCallback(),
                (t = this._$Do) === null || t === void 0 || t.setConnected(!0);
        }
        disconnectedCallback() {
            var t;
            super.disconnectedCallback(),
                (t = this._$Do) === null || t === void 0 || t.setConnected(!1);
        }
        render() {
            return w;
        }
    }
    (k.finalized = !0),
        (k._$litElement$ = !0),
        (Y = globalThis.litElementHydrateSupport) === null ||
            Y === void 0 ||
            Y.call(globalThis, { LitElement: k });
    const pt = globalThis.litElementPolyfillSupport;
    pt == null || pt({ LitElement: k }),
        ((K = globalThis.litElementVersions) !== null && K !== void 0
            ? K
            : (globalThis.litElementVersions = [])
        ).push('3.2.2');
    function Nt(r, t, e) {
        arguments.length === 2 && (e = !0);
        let s = r;
        const o = (t || '').split('/');
        for (const i of o) {
            if (!Object.prototype.hasOwnProperty.call(s, i)) {
                if (!e) return;
                s[i] = {};
            }
            s = s[i];
        }
        return s;
    }
    function Rt(r, t, e) {
        let s = r;
        if (t === '') throw new Error('Path cannot be empty');
        const o = (t || '').split('/'),
            i = o.length;
        return (
            o.forEach((n, c) => {
                Object.prototype.hasOwnProperty.call(s, n) || (s[n] = {}),
                    c === i - 1 && (s[n] = e),
                    (s = s[n]);
            }),
            s
        );
    }
    function vt(r, t) {
        let e = r;
        const s = (t || '').split('/'),
            o = s.length;
        s.forEach((i, n) => {
            !Object.prototype.hasOwnProperty.call(e, i) || (n === o - 1 ? delete e[i] : (e = e[i]));
        });
    }
    function Mt(r, t) {
        if (typeof t != 'string' || t.length === 0) return null;
        const e = t.split('/');
        let s = '',
            o = r;
        for (let i = 0; i < e.length; i++) {
            const n = e[i];
            if (!Object.prototype.hasOwnProperty.call(o, n))
                return s + (s.length === 0 ? '' : '/') + n;
            (o = o[n]), (s += (i === 0 ? '' : '/') + n);
        }
        return t === s ? null : s;
    }
    const bt = 'surveyTriggerButton';
    function Ht(r) {
        const { interceptUrl: t, globalObj: e, surveyLaunchMethod: s } = r;
        return new Promise((o, i) => {
            if (!t) {
                i(new Error('No intercept URL was provided. This is a mandatory parameter.'));
                return;
            }
            try {
                if (!e.document || !e.document.body)
                    throw new Error(
                        'Cannot inject elements in the document: document.body is not available.'
                    );
                let n = null;
                switch (s) {
                    case 'invisibleButton':
                        if (document.getElementById(bt))
                            throw new Error(
                                `An element with id ${bt} already exists in this page. The survey won't be launched unless the existing button is removed or its id is renamed.`
                            );
                        n = Qt();
                        break;
                    case 'qsiApi':
                        break;
                    default:
                        throw new Error(`Unsupported launch method: ${s}.`);
                }
                if (mt(e))
                    e.QSI.API.load().then(() => {
                        const c = ft(r, n, null);
                        o(c);
                    });
                else {
                    Ft(t, e);
                    const c = () => {
                        const a = ft(r, n, c);
                        o(a);
                    };
                    e.addEventListener('qsi_js_loaded', c, !1);
                }
            } catch (n) {
                i(n);
            }
        });
    }
    function ft(r, t, e) {
        const s = r.globalObj;
        if (!s.QSI.API)
            throw new Error(
                'Cannot create api context: QSI.API is not available in the global scope'
            );
        return {
            QSI: s.QSI,
            setParameters: new Set([]),
            globalObj: s,
            destroyed: !1,
            firstNonExistingRoot: Mt(s, r.contextRootPath),
            contextRootPath: r.contextRootPath,
            contextParamPaths: jt(r.contextParamPaths, r.customContextParamPaths),
            surveyLaunchMethod: r.surveyLaunchMethod,
            apiLoadedListener: e,
            invisibleButtonElement: t
        };
    }
    function mt(r) {
        return !!(r.QSI && r.QSI.API && typeof r.QSI.API.unload == 'function');
    }
    function jt(r, t) {
        return Object.keys(r || {}).reduce(
            (e, s) => ((e[s] = r[s]), e),
            JSON.parse(JSON.stringify(t || {}))
        );
    }
    function Qt() {
        const r = document.createElement('button');
        return (
            r.setAttribute('id', 'surveyTriggerButton'),
            (r.style.display = 'none'),
            document.body.appendChild(r),
            r
        );
    }
    function Ft(r, t) {
        const e = t.document.createElement('script');
        (e.type = 'text/javascript'), (e.src = r), t.document.body.appendChild(e);
    }
    function zt(r, t) {
        const e = Nt(r.globalObj, r.contextRootPath);
        Object.keys(t).forEach((s) => {
            const o = r.contextParamPaths[s] || s;
            Rt(e, o, t[s]), r.setParameters.add(s);
        });
    }
    function Vt(r) {
        if (r.setParameters.size === 0)
            throw new Error(
                'Cannot start a survey without context parameters. Call setContextParameters first.'
            );
        if (r.surveyLaunchMethod === 'invisibleButton') {
            r.invisibleButtonElement && r.invisibleButtonElement.dispatchEvent(new Event('click'));
            return;
        }
        r.QSI.API.unload(), r.QSI.API.load().then(r.QSI.API.run.bind(r.QSI.API));
    }
    function Dt(r) {
        mt(r) && r.QSI.API.unload(),
            r.apiLoadedListener &&
                r.globalObj.removeEventListener('qsi_js_loaded', r.apiLoadedListener);
        let t = r.contextRootPath;
        r.firstNonExistingRoot
            ? vt(r.globalObj, r.firstNonExistingRoot)
            : r.setParameters.forEach((e) => {
                  const s = r.contextParamPaths[e];
                  s && (t = `${t}/${s}`), vt(r.globalObj, t);
              }),
            r.invisibleButtonElement && r.invisibleButtonElement.remove(),
            (r.destroyed = !0);
    }
    const H = { init: Ht, destroy: Dt, setContextParameters: zt, startSurvey: Vt },
        gt = 'cx-qtx-sbtn-timestamp',
        _t = 'cx-qtx-sbtn-localstorage-test';
    function Gt(r) {
        if (!!r.enabled)
            try {
                r.globalObj.localStorage.setItem(gt, +new Date());
            } catch (t) {
                return;
            }
    }
    function Zt(r) {
        try {
            return r.globalObj.localStorage.getItem(gt);
        } catch (t) {
            return null;
        }
    }
    function Wt(r) {
        var s;
        if (typeof r._IS_LOCAL_STORAGE_AVAILABLE_CACHE == 'boolean')
            return r._IS_LOCAL_STORAGE_AVAILABLE_CACHE;
        const t = (s = r == null ? void 0 : r.globalObj) == null ? void 0 : s.localStorage;
        let e;
        try {
            t.setItem(_t, 'true'), t.removeItem(_t), (e = !0);
        } catch (o) {
            e = !1;
        }
        return (r._IS_LOCAL_STORAGE_AVAILABLE_CACHE = e), e;
    }
    function Jt(r) {
        return new Promise((t, e) => {
            var h;
            if (!r.enabled) {
                t(!1);
                return;
            }
            if (!Wt(r)) {
                t(!1);
                return;
            }
            const s = (h = r == null ? void 0 : r.range) == null ? void 0 : h.type;
            if (s !== 'months') {
                e(new Error(`Invalid range type configured: ${s}`));
                return;
            }
            const o = new Date().getMonth() + 1,
                n = Yt(r).indexOf(o) >= 0,
                c = Zt(r);
            if (!c) {
                t(n);
                return;
            }
            const a = parseInt(c, 10),
                f = new Date(a).getMonth() + 1;
            t(n && o !== f);
        });
    }
    function Yt(r) {
        var t, e;
        return ((t = r == null ? void 0 : r.range) == null ? void 0 : t.type) === 'months'
            ? (e = r == null ? void 0 : r.range) == null
                ? void 0
                : e.value.map((s) => window.parseInt(s, 10))
            : [];
    }
    const yt = { saveUserInteractionTime: Gt, determineNotificationStatus: Jt };
    class $t extends k {
        constructor() {
            super(),
                (this._version = '2.0.1'),
                (this.apiContext = null),
                (this.notifyUser = null),
                (this.surveyLaunchMethod = 'invisibleButton'),
                (this.contextParams = {}),
                (this.fiori3Compatible = !1),
                (this.interceptUrl = ''),
                (this.contextRootPath = 'sap/qtx'),
                (this.notificationConfig = {
                    enabled: !0,
                    range: { type: 'months', value: [2, 5, 8, 11] }
                }),
                (this.title = 'Give Feedback'),
                (this.ariaLabel = 'Give Feedback'),
                (this.contextParamPaths = {
                    Q_Language: 'appcontext/languageTag',
                    language: 'appcontext/languageTag',
                    appFrameworkVersion: 'appcontext/appFrameworkVersion',
                    theme: 'appcontext/theme',
                    appId: 'appcontext/appId',
                    appVersion: 'appcontext/appVersion',
                    technicalAppComponentId: 'appcontext/technicalAppComponentId',
                    appTitle: 'appcontext/appTitle',
                    appSupportInfo: 'appcontext/appSupportInfo',
                    tenantId: 'session/tenantId',
                    tenantRole: 'session/tenantRole',
                    appFrameworkId: 'appcontext/appFrameworkId',
                    productName: 'session/productName',
                    platformType: 'session/platformType',
                    pushSrcType: 'push/pushSrcType',
                    pushSrcAppId: 'push/pushSrcAppId',
                    pushSrcTrigger: 'push/pushSrcTrigger',
                    clientAction: 'appcontext/clientAction',
                    previousTheme: 'appcontext/previousTheme',
                    followUpCount: 'appcontext/followUpCount',
                    deviceType: 'device/type',
                    orientation: 'device/orientation',
                    osName: 'device/osName',
                    browserName: 'device/browserName'
                }),
                (this.customContextParamPaths = {});
        }
        static get properties() {
            return {
                title: { type: String, attribute: 'title', reflect: !1 },
                ariaLabel: { type: String, attribute: 'aria-label', reflect: !1 },
                surveyLaunchMethod: {
                    type: String,
                    attribute: 'survey-launch-method',
                    reflect: !1
                },
                notifyUser: { type: Boolean, attribute: 'notify-user', reflect: !0 },
                notificationConfig: { type: Object, attribute: 'notification-config', reflect: !0 },
                interceptUrl: { type: String, attribute: 'intercept-url', reflect: !0 },
                customContextParamPaths: {
                    type: Object,
                    attribute: 'custom-context-param-paths',
                    reflect: !0
                },
                contextParamPaths: { type: Object, attribute: 'context-param-paths', reflect: !0 },
                contextRootPath: { type: String, attribute: 'context-root-path', reflect: !0 },
                contextParams: { type: Object, attribute: 'context-params', reflect: !0 },
                fiori3Compatible: { type: Boolean, attribute: 'fiori3-compatible', reflect: !0 }
            };
        }
        _onClick() {
            this._turnOffNotification(),
                yt.saveUserInteractionTime(
                    z(F({}, this.notificationConfig), { globalObj: window })
                ),
                this._runSurvey();
        }
        _turnOffNotification() {
            (this.notifyUser = !1), this.requestUpdate();
        }
        _runSurvey() {
            this._getAPIContext().then(
                (t) => {
                    const e = this.contextParams;
                    (this.apiContext = t),
                        H.setContextParameters(this.apiContext, e),
                        H.startSurvey(this.apiContext);
                },
                (t) => {
                    console.log(t);
                }
            );
        }
        _getAPIContext() {
            return this.apiContext
                ? Promise.resolve(this.apiContext)
                : H.init({
                      interceptUrl: this.interceptUrl,
                      globalObj: window,
                      contextRootPath: this.contextRootPath,
                      contextParamPaths: this.contextParamPaths,
                      customContextParamPaths: this.customContextParamPaths,
                      surveyLaunchMethod: this.surveyLaunchMethod
                  });
        }
        setContextParams(t) {
            this.contextParams = JSON.parse(JSON.stringify(t));
        }
        updateContextParam(t, e) {
            this.contextParams[t] = e;
        }
        connectedCallback() {
            super.connectedCallback(),
                this.addEventListener('click', this._onClick),
                this.setAttribute('aria-hidden', 'true'),
                yt
                    .determineNotificationStatus(
                        z(F({}, this.notificationConfig), { globalObj: window })
                    )
                    .then(
                        (t) => {
                            this.notifyUser === null &&
                                ((this.notifyUser = t), this.requestUpdate());
                        },
                        (t) => {
                            console.log(t);
                        }
                    );
        }
        disconnectedCallback() {
            super.disconnectedCallback(),
                this.removeEventListener('click', this._onClick),
                this.apiContext && (H.destroy(this.apiContext), (this.apiContext = null));
        }
        render() {
            return J`
            <slot>
                <button
                    class="qtxSurveyFallbackButton"
                    title="${this.title}"
                    aria-label="${this.ariaLabel}"
                >
                </button>
            </slot>
        `;
        }
        static get styles() {
            return D`/*
 * Prevent focus effect on the host, the button only should be focussed.
 */
:host(:focus) {
    outline: none;
    box-shadow: none;
}
:host(:focus) .qtxSurveyFallbackButton {
    background-color: initial;
}
.qtxSurveyFallbackButton {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    width: var(--qtxSurveyButton_Size, 2rem);
    height: var(--qtxSurveyButton_Size, 2rem);
    outline: none;
    shape-rendering: geometricprecision;
    /* Fiori Next buttons have transitions */
    -webkit-transition: 0.3s ease-in-out;
    transition: 0.3s ease-in-out;
    font-weight: 400;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin: 0;
    border: 0;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
    position: relative;
    padding: 6px;
    -webkit-box-shadow: none;
    box-shadow: none;
    border-radius: var(--sapButton_BorderCornerRadius, 0.5rem);
    background: 0 0;
}
.qtxSurveyFallbackButton.is-focus,
.qtxSurveyFallbackButton:focus {
    z-index: 5;
    background: var(--sapShell_Hover_Background, transparent);
    outline: 0;
    border-color: #fff;
    -webkit-box-shadow: 0 0 2px rgba(27, 144, 255, 0.16),
        0 8px 16px rgba(27, 144, 255, 0.16), 0 0 0 0.125rem #1b90ff,
        inset 0 0 0 0.125rem #fff;
    box-shadow: 0 0 2px rgba(27, 144, 255, 0.16),
        0 8px 16px rgba(27, 144, 255, 0.16), 0 0 0 0.125rem #1b90ff,
        inset 0 0 0 0.125rem #fff;
}
:host([fiori3-compatible]) .qtxSurveyFallbackButton {
    /*
     * Fiori 3: use correct sizing
     */
    width: var(--qtxSurveyButton_Size, 2.25rem);
    height: var(--qtxSurveyButton_Size, 2.25rem);
    /*
     * Fiori 3: disable Fiori Next transitions + box shadow
     */
    -webkit-transition: none;
    transition: none;
    box-shadow: none;
    -webkit-box-shadow: none;
}
:host([fiori3-compatible]):host(:hover) .qtxSurveyFallbackButton {
    box-shadow: none;
    -webkit-box-shadow: none;
}
:host([fiori3-compatible]):host(:active) .qtxSurveyFallbackButton {
    box-shadow: none;
    -webkit-box-shadow: none;
}
/*
 * Fiori3: create visible focus area
 */
:host([fiori3-compatible]):host(:focus) .qtxSurveyFallbackButton:after {
    content: "";
    position: absolute;
    width: auto;
    height: auto;
    top: 0.0625rem;
    left: 0.0625rem;
    right: 0.0625rem;
    bottom: 0.0625rem;
    border: var(--sapContent_FocusWidth, 0.0625rem) dotted
        var(--sapContent_ContrastFocusColor, #fff);
    border-radius: 4px;
}
:host(:hover) .qtxSurveyFallbackButton {
    background-color: var(--sapShell_Hover_Background, transparent);
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 8px 16px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 8px 16px rgba(131, 150, 168, 0.16);
}
:host(:hover) path {
    stroke: var(--sapShell_Active_TextColor, #475e75);
}
:host(:hover) .eye,
:host(:hover) .foregroundIcon {
    fill: var(--sapShell_Active_TextColor, #475e75);
    stroke: transparent;
}
:host(:active) .qtxSurveyFallbackButton {
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    background: var(--sapShell_Active_Background, #fff);
}
:host([notify-user]) .smiley,
:host([notify-user]) .backgroundCircle {
    fill: var(
        --qtxSurveyButton_NotificationColor,
        #64edd2
    ); /* Teal 2 */
}
:host([notify-user][fiori3-compatible]) .smiley,
:host([notify-user][fiori3-compatible]) .backgroundCircle {
    fill: var(
        --qtxSurveyButton_NotificationColor,
        var(--sapIndicationColor_6, #0f828f)
    );
}
:host([fiori3-compatible]) .pathGroup {
    transform: scale(var(--qtxSurveyButton_IconScale, 1))
        translate(0, var(--qtxSurveyButton_IconOffsetY, 0));
}
.pathGroup {
    transform: scale(var(--qtxSurveyButton_IconScale, 1))
        translate(0, var(--qtxSurveyButton_IconOffsetY, 0));
    transform-origin: center;
    fill: none;
}
path {
    stroke: var(--sapShell_InteractiveTextColor, #5b738b);
    stroke-width: 2px;
    vector-effect: non-scaling-stroke;
}
.eye,
.foregroundIcon {
    fill: var(--sapShell_InteractiveTextColor, #5b738b);
    stroke: transparent;
}
.qtxSurveyFallbackButton::after,
.qtxSurveyFallbackButton::before {
    -webkit-box-sizing: inherit;
    box-sizing: inherit;
    font-size: inherit;
}
.qtxSurveyFallbackButton.is-pressed,
.qtxSurveyFallbackButton[aria-pressed="true"] {
    -webkit-box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    box-shadow: 0 0 2px rgba(131, 150, 168, 0.16),
        0 2px 4px rgba(131, 150, 168, 0.16);
    background: var(--sapShell_Active_Background, #fff);
}
`;
        }
    }
    At(
        $t,
        'shadowRootOptions',
        z(F({}, k.shadowRootOptions), { mode: 'closed', delegatesFocus: 'true' })
    ),
        window.customElements.define('cx-qtx-survey-button', $t);
})();
//# sourceMappingURL=cx-qtx-survey-button.bundle.js.map
