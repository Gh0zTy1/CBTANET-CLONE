import { ZardContextMenuDirective } from '@/shared/components/base/menu/context-menu.directive';
import { ZardMenuContentDirective } from '@/shared/components/base/menu/menu-content.directive';
import { ZardMenuItemDirective } from '@/shared/components/base/menu/menu-item.directive';
import { ZardMenuLabelComponent } from '@/shared/components/base/menu/menu-label.component';
import { ZardMenuShortcutComponent } from '@/shared/components/base/menu/menu-shortcut.component';
import { ZardMenuDirective } from '@/shared/components/base/menu/menu.directive';

export const ZardMenuImports = [
  ZardContextMenuDirective,
  ZardMenuContentDirective,
  ZardMenuItemDirective,
  ZardMenuDirective,
  ZardMenuLabelComponent,
  ZardMenuShortcutComponent,
] as const;
