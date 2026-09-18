import { ZardDropdownMenuItemComponent } from '@/shared/components/base/dropdown/dropdown-item.component';
import { ZardDropdownMenuContentComponent } from '@/shared/components/base/dropdown/dropdown-menu-content.component';
import { ZardDropdownDirective } from '@/shared/components/base/dropdown/dropdown-trigger.directive';
import { ZardDropdownMenuComponent } from '@/shared/components/base/dropdown/dropdown.component';
import { ZardMenuLabelComponent } from '@/shared/components/base/menu/menu-label.component';

export const ZardDropdownImports = [
  ZardDropdownMenuComponent,
  ZardDropdownMenuItemComponent,
  ZardMenuLabelComponent,
  ZardDropdownMenuContentComponent,
  ZardDropdownDirective,
] as const;
