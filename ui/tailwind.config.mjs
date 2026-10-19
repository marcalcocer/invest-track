const token = (name) => `hsl(var(--${name}) / <alpha-value>)`;

/** @type {import('tailwindcss').Config} */
export default {
	darkMode: 'class',
	content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
	theme: {
		extend: {
			colors: {
				background: token('background'),
				foreground: token('foreground'),
				card: token('card'),
				'card-foreground': token('card-foreground'),
				muted: token('muted'),
				'muted-foreground': token('muted-foreground'),
				border: token('border'),
				primary: token('primary'),
				'primary-foreground': token('primary-foreground'),
				success: token('success'),
				danger: token('danger'),
				ring: token('ring'),
			},
			borderRadius: {
				token: 'var(--radius)',
			},
			fontFamily: {
				sans: [
					'Inter',
					'ui-sans-serif',
					'system-ui',
					'-apple-system',
					'Segoe UI',
					'Roboto',
					'sans-serif',
				],
			},
			keyframes: {
				fadeIn: {
					from: { opacity: '0', transform: 'translateY(-4px)' },
					to: { opacity: '1', transform: 'translateY(0)' },
				},
			},
			animation: {
				fadeIn: 'fadeIn 0.25s ease-out',
			},
		},
	},
	plugins: [],
}
