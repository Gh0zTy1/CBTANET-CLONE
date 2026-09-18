import { cva, type VariantProps } from 'class-variance-authority';

export const comboboxVariants = cva('', {
  variants: {
    zWidth: {
      default: 'w-50',
      sm: 'w-37.5',
      md: 'w-62.5',
      lg: 'w-87.5',
      full: 'w-full'
    },
    zSize: {
      default: 'h-10 text-(length:--text-sm)',
      sm: 'h-9 text-(length:--text-xs)',
      lg: 'h-11 text-(length:--text-sm)',
    }
  },
  defaultVariants: {
    zWidth: 'default',
    zSize: 'default'
  },
});

export type ZardComboboxWidthVariants = NonNullable<VariantProps<typeof comboboxVariants>['zWidth']>;
export type ZardComboboxSizeVariants = NonNullable<VariantProps<typeof comboboxVariants>['zSize']>;
