import { cva, type VariantProps } from 'class-variance-authority';

export type zInputIcon = 'email' | 'password' | 'text';

export const inputVariants = cva('w-full', {
  variants: {
    zType: {
      default:
        'flex rounded-md border px-4 font-normal border-input bg-transparent file:border-0 file:text-foreground file:bg-transparent file:font-medium placeholder:text-muted-foreground outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50',
      textarea:
        'flex pb-2 min-h-20 h-auto rounded-md border border-input bg-background px-3 py-2 text-base placeholder:text-muted-foreground outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-50',
    },
    zSize: {
      default: 'text-(length:--text-sm)',
      sm: 'text-(length:--text-xs)',
      lg: 'text-(length:--text-sm)',
    },
    zStatus: {
      error: 'border-destructive focus-visible:ring-destructive',
      warning: 'border-yellow-500 focus-visible:ring-yellow-500',
      success: 'border-green-500 focus-visible:ring-green-500',
    },
    zBorderless: {
      true: 'flex-1 bg-transparent border-0 outline-none focus-visible:ring-0 focus-visible:ring-offset-0 p-0',
    },
  },
  defaultVariants: {
    zType: 'default',
    zSize: 'default',
  },
  compoundVariants: [
    { zType: 'default', zSize: 'default', class: 'h-10 py-2 file:max-md:py-0' }, //change this
    { zType: 'default', zSize: 'sm', class: 'h-9 file:md:py-2 file:max-md:py-1.5' }, //change this
    { zType: 'default', zSize: 'lg', class: 'h-11 py-2 file:md:py-3 file:max-md:py-2.5' }, //change this
  ],
});

export type ZardInputTypeVariants = NonNullable<VariantProps<typeof inputVariants>['zType']>;
export type ZardInputSizeVariants = NonNullable<VariantProps<typeof inputVariants>['zSize']>;
export type ZardInputStatusVariants = NonNullable<VariantProps<typeof inputVariants>['zStatus']>;
