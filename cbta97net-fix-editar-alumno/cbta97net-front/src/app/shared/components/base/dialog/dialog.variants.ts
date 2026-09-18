import { cva, type VariantProps } from 'class-variance-authority';

export const dialogVariants = cva(
  'fixed left-[50%] top-[50%] z-50 grid w-fit translate-x-[-50%] translate-y-[-50%] gap-4 border bg-background p-6 m-6 shadow-lg rounded-lg',
);
export type ZardDialogVariants = VariantProps<typeof dialogVariants>;
