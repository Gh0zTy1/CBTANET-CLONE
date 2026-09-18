import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideCheck } from '@ng-icons/lucide';
import { mergeClasses } from '@/shared/utils/merge-classes';
import type { ClassValue } from 'clsx';
import {
  Component,
  computed,
  input,
  output,
  signal,
} from '@angular/core';

import {
  checkboxLabelVariants,
  checkboxVariants,
  type ZardCheckboxShapeVariants,
  type ZardCheckboxSizeVariants,
  type ZardCheckboxTypeVariants,
} from './checkbox.variants';

@Component({
  selector: 'app-checkbox',
  imports: [NgIcon],
  providers: [provideIcons({ lucideCheck })],
  template: `
    <div 
      class="w-fit flex flex-row items-center align-start gap-(--space-2) cursor-pointer" (click)="toggle()"
    >
      <!-- input with a relative icon placed inside -->
      <div class="relative flex">
        <input
          type="checkbox"
          [class]="classes()"
          [checked]="value()"
          [disabled]="disabled()"
        />
        <!-- relative icon -->
        <ng-icon
          name="lucideCheck"
          color="white"
          class="pointer-events-none absolute top-1/2 left-1/2 -translate-1/2 transition-opacity select-none"
        />
      </div>
      <label [class]="labelClasses()">
        {{ placeHolder() }}
      </label>
    </div>
  `
})
export class CheckboxComponent {

  readonly value = signal(false);
  readonly placeHolder = input<string>('')
  readonly disabled = input(false);
  readonly type = input<ZardCheckboxTypeVariants>('default');
  readonly size = input<ZardCheckboxSizeVariants>('default');
  readonly shape = input<ZardCheckboxShapeVariants>('default');
  readonly class = input<ClassValue>('');

  readonly change = output<boolean>(); // emit event for toggle on checkbox

  /**
   * Gets the current boolean value of the checkbox component.
   * @returns Boolean with the current value.
   */
  getValue(): boolean {
    return this.value();
  }

  /**
   * Establish the current value to display as the checkbox state.
   * @param value Boolean value to set
   */
  setValue(value: boolean): void {
    this.value.set(value);
  }

  /**
   * Toogle event to switch the current checkbox value
   */
  toggle(): void {
    if (this.disabled()) return;

    this.value.update(v => !v);
    this.change.emit(this.value());
  }

  /**
   * Establishes the different tailwind variations for the component
   * including pass-trough classes.
   */
  protected readonly classes = computed(() =>
    mergeClasses(checkboxVariants({ zType: this.type(), zSize: this.size(), zShape: this.shape() }), this.class()),
  );

  /**
   * Establishes the pass-trough classes for the label.
   */
  readonly labelClasses = computed(() =>
    mergeClasses(
      checkboxLabelVariants({ zSize: this.size() })
    )
  );
}
