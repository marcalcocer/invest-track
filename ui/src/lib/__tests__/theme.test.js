import { test, describe } from 'node:test';
import assert from 'node:assert';
import {
    THEME_STORAGE_KEY,
    normalizeTheme,
    resolveTheme,
    getStoredTheme,
    setStoredTheme,
    applyTheme,
} from '../theme.js';

describe('normalizeTheme', () => {
    test('returns valid values unchanged', () => {
        assert.strictEqual(normalizeTheme('light'), 'light');
        assert.strictEqual(normalizeTheme('dark'), 'dark');
        assert.strictEqual(normalizeTheme('system'), 'system');
    });

    test('falls back to system for null and unknown values', () => {
        assert.strictEqual(normalizeTheme(null), 'system');
        assert.strictEqual(normalizeTheme('purple'), 'system');
    });
});

describe('resolveTheme', () => {
    test('keeps explicit preferences regardless of system setting', () => {
        assert.strictEqual(resolveTheme('light', true), 'light');
        assert.strictEqual(resolveTheme('dark', false), 'dark');
    });

    test('follows the system setting when preference is system', () => {
        assert.strictEqual(resolveTheme('system', true), 'dark');
        assert.strictEqual(resolveTheme('system', false), 'light');
    });
});

describe('stored theme', () => {
    test('reads the stored preference', () => {
        var storage = createStorage({ [THEME_STORAGE_KEY]: 'dark' });
        assert.strictEqual(getStoredTheme(storage), 'dark');
    });

    test('defaults to system when nothing is stored', () => {
        assert.strictEqual(getStoredTheme(createStorage({})), 'system');
    });

    test('defaults to system when storage throws', () => {
        assert.strictEqual(getStoredTheme(createThrowingStorage()), 'system');
    });

    test('persists a normalized value', () => {
        var storage = createStorage({});
        assert.strictEqual(setStoredTheme(storage, 'light'), 'light');
        assert.strictEqual(storage.getItem(THEME_STORAGE_KEY), 'light');
        assert.strictEqual(setStoredTheme(storage, 'bogus'), 'system');
    });

    test('does not throw when storage fails on write', () => {
        assert.strictEqual(setStoredTheme(createThrowingStorage(), 'dark'), 'dark');
    });
});

describe('applyTheme', () => {
    test('adds the dark class for dark preference', () => {
        var doc = createDocument();
        assert.strictEqual(applyTheme('dark', doc, createMatchMedia(false)), 'dark');
        assert.ok(doc.documentElement.classes.has('dark'));
        assert.strictEqual(doc.documentElement.style.colorScheme, 'dark');
    });

    test('removes the dark class for light preference even when system is dark', () => {
        var doc = createDocument();
        doc.documentElement.classes.add('dark');
        assert.strictEqual(applyTheme('light', doc, createMatchMedia(true)), 'light');
        assert.ok(!doc.documentElement.classes.has('dark'));
        assert.strictEqual(doc.documentElement.style.colorScheme, 'light');
    });

    test('follows matchMedia for system preference', () => {
        var doc = createDocument();
        applyTheme('system', doc, createMatchMedia(true));
        assert.ok(doc.documentElement.classes.has('dark'));
        applyTheme('system', doc, createMatchMedia(false));
        assert.ok(!doc.documentElement.classes.has('dark'));
    });
});

function createStorage(initial) {
    var data = { ...initial };
    return {
        getItem: (key) => (key in data ? data[key] : null),
        setItem: (key, value) => {
            data[key] = String(value);
        },
    };
}

function createThrowingStorage() {
    return {
        getItem: () => {
            throw new Error('denied');
        },
        setItem: () => {
            throw new Error('denied');
        },
    };
}

function createDocument() {
    var classes = new Set();
    return {
        documentElement: {
            classes,
            style: {},
            classList: {
                add: (name) => classes.add(name),
                remove: (name) => classes.delete(name),
            },
        },
    };
}

function createMatchMedia(matches) {
    return () => ({ matches });
}
