---
name: Fiscal Precision
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45474c'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#75777d'
  outline-variant: '#c5c6cd'
  surface-tint: '#545f73'
  primary: '#091426'
  on-primary: '#ffffff'
  primary-container: '#1e293b'
  on-primary-container: '#8590a6'
  inverse-primary: '#bcc7de'
  secondary: '#5b5e67'
  on-secondary: '#ffffff'
  secondary-container: '#dfe2ed'
  on-secondary-container: '#61646d'
  tertiary: '#1e1200'
  on-tertiary: '#ffffff'
  tertiary-container: '#35260c'
  on-tertiary-container: '#a38c6a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8e3fb'
  primary-fixed-dim: '#bcc7de'
  on-primary-fixed: '#111c2d'
  on-primary-fixed-variant: '#3c475a'
  secondary-fixed: '#dfe2ed'
  secondary-fixed-dim: '#c3c6d1'
  on-secondary-fixed: '#181c23'
  on-secondary-fixed-variant: '#43474f'
  tertiary-fixed: '#fadfb8'
  tertiary-fixed-dim: '#ddc39d'
  on-tertiary-fixed: '#271902'
  on-tertiary-fixed-variant: '#564427'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.01em
  numeric-data:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 24px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 48px
  container-max: 1200px
  gutter: 24px
---

## Brand & Style
The design system is centered on the concepts of stability, clarity, and empowerment. It targets individuals seeking a sophisticated yet accessible way to manage their financial life. The UI avoids the "gamified" look of consumer fintech, opting instead for a **Minimalist Corporate** aesthetic that feels like a premium banking tool.

The emotional response should be one of "controlled oversight." This is achieved through heavy use of whitespace, a restricted color palette, and a focus on high-quality typography. Visual complexity is minimized to reduce the cognitive load associated with financial data, ensuring that the most important information—the user's balance and cash flow—is always the focal point.

## Colors
The palette uses high-contrast functional colors against a neutral foundation.
- **Primary (Deep Navy):** Used for navigation, primary actions, and headers to establish authority and stability.
- **Success (Emerald):** Reserved exclusively for positive financial movement, such as income, savings goals met, and surplus indicators.
- **Danger (Rose):** Used for expenses, debt alerts, and over-budget warnings.
- **Neutral (Slate):** A range of grays used to create hierarchy in typography and subtle boundaries between UI sections.

The background is a very light slate tint to reduce screen glare, while cards and containers use pure white to pop from the canvas.

## Typography
This design system utilizes **Inter** for its exceptional legibility and modern, neutral character. 

**Numerical Data Guidelines:**
For all financial figures, use `tabular-nums` (tnum) CSS properties to ensure numbers align vertically in lists and tables. Large balance displays use `display-lg` with tight letter spacing for a punchy, authoritative look. Labels for metadata (e.g., "Transaction Date") should use `label-sm` in a medium-gray slate to maintain hierarchy without distracting from the primary data points.

## Layout & Spacing
The layout follows a **Fixed Grid** model on desktop (1200px max width) to maintain a professional, organized structure. 

- **Grid:** A 12-column system for desktop, 8-column for tablet, and 4-column for mobile.
- **Rhythm:** An 8px linear scale (4, 8, 16, 24, 48) drives all padding and margins. 
- **Mobile Adaptation:** On mobile, side margins shrink to 16px. Cards should span the full width of the viewport minus margins, stacking vertically to ensure readability of financial rows.

## Elevation & Depth
Depth is created through **Tonal Layers** and **Ambient Shadows**. 

- **Level 0 (Background):** Slate-50 (#F8FAFC).
- **Level 1 (Cards/Content):** Pure white surface with a "Soft Natural" shadow: `0px 1px 3px rgba(0,0,0,0.05), 0px 10px 15px -3px rgba(0,0,0,0.02)`.
- **Level 2 (Modals/Popovers):** Pure white with a more pronounced shadow to indicate focus and separation from the base content.

Borders are used sparingly; only use 1px solid Slate-200 for internal dividers within cards or to separate navigation elements.

## Shapes
The design system uses **Soft** roundedness to balance professionalism with modern approachability. 
- **Standard Elements:** 0.25rem (4px) for inputs, small buttons, and tags.
- **Containers/Cards:** 0.5rem (8px) for primary content containers.
- **Interactive Large:** 0.75rem (12px) for large action panels.
- **Progress Bars:** Use a "Pill" radius (999px) for the inner and outer tracks to create a distinct visual language for tracking completion.

## Components

### Buttons
- **Primary:** Deep Navy background, white text. Bold, 16px padding (horizontal).
- **Secondary:** Slate-100 background, Slate-900 text.
- **Transaction Toggle:** A segmented control (pill shape) that toggles between "Expense" (Rose tint when active) and "Income" (Emerald tint when active).

### Input Fields
- Use a 1px border (Slate-200). On focus, the border shifts to Deep Navy with a 2px outer glow of Navy at 10% opacity. 

### Cards
- The central component of the design. Every card must have a white background, 24px padding, and the defined Level 1 shadow. Headers within cards should be `headline-md`.

### Progress Bars
- **Credit/Budget Trackers:** A background rail of Slate-100. The fill color is dynamic: Emerald for <80% of budget, Amber for 80-99%, and Rose for >100%.

### Lists
- Transaction lists should feature a left-aligned icon in a circle (e.g., a "Food" icon). The amount is right-aligned using `numeric-data` typography, colored Emerald if positive and Slate-900 if negative.