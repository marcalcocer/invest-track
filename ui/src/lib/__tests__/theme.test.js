import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { THEME_STORAGE_KEY, applyTheme, getStoredTheme, resolveTheme } from "../theme.js";

describe("theme.getStoredTheme", () => {
    it("returns the stored theme when it is valid", () => {
        var storage = fakeStorage({ [THEME_STORAGE_KEY]: "dark" });

        assert.equal(getStoredTheme(storage), "dark");
    });

    it("returns null when nothing is stored", () => {
        assert.equal(getStoredTheme(fakeStorage()), null);
    });

    it("ignores an invalid stored value", () => {
        var storage = fakeStorage({ [THEME_STORAGE_KEY]: "purple" });

        assert.equal(getStoredTheme(storage), null);
    });

    it("returns null when the storage throws", () => {
        var storage = {
            getItem() {
                throw new Error("denied");
            },
        };

        assert.equal(getStoredTheme(storage), null);
    });
});

describe("theme.resolveTheme", () => {
    it("prefers the stored light theme over a dark system preference", () => {
        assert.equal(resolveTheme("light", true), "light");
    });

    it("prefers the stored dark theme over a light system preference", () => {
        assert.equal(resolveTheme("dark", false), "dark");
    });

    it("falls back to dark when the system prefers dark", () => {
        assert.equal(resolveTheme(null, true), "dark");
    });

    it("falls back to light when the system prefers light", () => {
        assert.equal(resolveTheme(null, false), "light");
    });

    it("ignores an invalid stored value", () => {
        assert.equal(resolveTheme("purple", true), "dark");
    });
});

describe("theme.applyTheme", () => {
    it("adds the dark class and persists the choice", () => {
        var root = fakeRoot();
        var storage = fakeStorage();

        applyTheme("dark", root, storage);

        assert.equal(root.classes.has("dark"), true);
        assert.equal(storage.getItem(THEME_STORAGE_KEY), "dark");
    });

    it("removes the dark class when switching to light", () => {
        var root = fakeRoot(["dark"]);
        var storage = fakeStorage();

        applyTheme("light", root, storage);

        assert.equal(root.classes.has("dark"), false);
        assert.equal(storage.getItem(THEME_STORAGE_KEY), "light");
    });

    it("applies the class without storage", () => {
        var root = fakeRoot();

        applyTheme("dark", root);

        assert.equal(root.classes.has("dark"), true);
    });

    it("still applies the class when the storage throws", () => {
        var root = fakeRoot();
        var storage = {
            setItem() {
                throw new Error("quota exceeded");
            },
        };

        applyTheme("dark", root, storage);

        assert.equal(root.classes.has("dark"), true);
    });
});

function fakeStorage(initial = {}) {
    var data = new Map(Object.entries(initial));
    return {
        getItem: (key) => (data.has(key) ? data.get(key) : null),
        setItem: (key, value) => {
            data.set(key, value);
        },
    };
}

function fakeRoot(initialClasses = []) {
    var classes = new Set(initialClasses);
    return {
        classes,
        classList: {
            toggle: (name, force) => {
                if (force) {
                    classes.add(name);
                } else {
                    classes.delete(name);
                }
            },
        },
    };
}
