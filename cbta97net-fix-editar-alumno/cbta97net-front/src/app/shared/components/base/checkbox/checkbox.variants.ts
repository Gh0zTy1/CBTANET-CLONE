import { cva, type VariantProps } from 'class-variance-authority';

export const checkboxVariants = cva(
  'pointer-events-none peer appearance-none border border-input transition shadow-sm hover:shadow-md focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-input/30',
  {
    variants: {
      zType: {
        default: 'checked:border-border checked:bg-gray-700 dark:checked:bg-primary',
        destructive: 'checked:border-destructive checked:bg-destructive',
      },
      zSize: {
        default: 'size-4',
        md: 'size-5',
        lg: 'size-6',
      },
      zShape: {
        default: 'rounded-sm',
        circle: 'rounded-full',
        square: 'rounded-none',
      },
    },
    defaultVariants: {
      zType: 'default',
      zSize: 'default',
      zShape: 'default',
    },
  },
);

export const checkboxLabelVariants = cva('cursor-[unset] text-current empty:hidden text-slate-600 select-none pointer-events-none', {
  variants: {
    zSize: {
      default: 'text-(length:--text-sm)',
      md: 'text-(length:--text-md)',
      lg: 'text-(length:--text-lg)',
    },
  },
  defaultVariants: {
    zSize: 'default',
  },
});

export type ZardCheckboxShapeVariants = NonNullable<VariantProps<typeof checkboxVariants>['zShape']>;
export type ZardCheckboxSizeVariants = NonNullable<VariantProps<typeof checkboxVariants>['zSize']>;
export type ZardCheckboxTypeVariants = NonNullable<VariantProps<typeof checkboxVariants>['zType']>;
