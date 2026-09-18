import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, TemplateRef, ViewEncapsulation } from '@angular/core';
import { ZardStringTemplateOutletDirective } from '@/shared/core/directives/string-template-outlet/string-template-outlet.directive';
import { ZardIconComponent } from '@/shared/components/base/icon/icon.component';
export type ComposedAlertStatus = 'danger' | 'warning' | 'success';

@Component({
    selector: 'alert-composed',
    templateUrl: './alert.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    host: {
        role: 'alert',
    },
    imports: [
        ZardStringTemplateOutletDirective,
        ZardIconComponent,
        NgClass
    ]
})
export class AlertComposedComponent {
    readonly status = input<ComposedAlertStatus>('danger');
    readonly title = input<string>('');
    readonly description = input<string | TemplateRef<void>>('');

    protected readonly iconName = computed(() => {
        switch (this.status()) {
            case 'danger':
                return 'error';
            case 'warning':
                return 'warning';
            case 'success':
                return 'check_circle';
        }
    });

    protected readonly borderClass = computed(() => {
        switch (this.status()) {
            case 'danger':
                return '';
            case 'warning':
                return 'border-yellow-500';
            case 'success':
                return 'border-emerald-500';
        }
    });

    protected readonly backgroundClass = computed(() => {
        switch (this.status()) {
            case 'danger':
                return 'bg-red-50';
            case 'warning':
                return 'bg-yellow-50';
            case 'success':
                return 'bg-emerald-50';
        }
    });

    protected readonly iconClass = computed(() => {
        switch (this.status()) {
            case 'danger':
                return 'text-destructive';
            case 'warning':
                return 'text-yellow-600';
            case 'success':
                return 'text-emerald-600';
        }
    });

    protected readonly descriptionClass = computed(() => {
        switch (this.status()) {
            case 'danger':
                return 'text-destructive/90';
            case 'warning':
                return 'text-yellow-700';
            case 'success':
                return 'text-emerald-700';
        }
    });
}
