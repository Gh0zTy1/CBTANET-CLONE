import { ZardCommandDividerComponent } from '@/shared/components/base/command/command-divider.component';
import { ZardCommandEmptyComponent } from '@/shared/components/base/command/command-empty.component';
import { ZardCommandInputComponent } from '@/shared/components/base/command/command-input.component';
import { ZardCommandListComponent } from '@/shared/components/base/command/command-list.component';
import { ZardCommandOptionGroupComponent } from '@/shared/components/base/command/command-option-group.component';
import { ZardCommandOptionComponent } from '@/shared/components/base/command/command-option.component';
import { ZardCommandComponent } from '@/shared/components/base/command/command.component';

export const ZardCommandImports = [
  ZardCommandComponent,
  ZardCommandInputComponent,
  ZardCommandListComponent,
  ZardCommandEmptyComponent,
  ZardCommandOptionComponent,
  ZardCommandOptionGroupComponent,
  ZardCommandDividerComponent,
] as const;
