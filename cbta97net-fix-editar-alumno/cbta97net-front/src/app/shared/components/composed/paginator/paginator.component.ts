import { Component, EventEmitter, Output, signal, computed } from '@angular/core';
import { ZardPaginationImports } from '@/shared/components/base/pagination';

@Component({
    selector: 'paginator',
    templateUrl: 'paginator.component.html',
    imports: [
        ZardPaginationImports
    ]
})

export class PaginatorComponent {

    @Output() onPageChanged = new EventEmitter<null>();

    protected page = signal<number>(1);
    protected totalPages = signal<number>(0);
    protected itemsPerPage = signal<number>(0);

    protected _isDisabled = computed(() => this.totalPages() === 0 || this._disabled());
    protected _disabled = signal<boolean>(false);
    protected delta = signal(5); // cantidad de páginas visibles (modificable)

    protected paginationDescription =  computed(()=> {
        const firstNumCurrentBlock = (this.page() * this.itemsPerPage()) - this.itemsPerPage();
        const lastNumCurrentBlock = (this.page() * this.itemsPerPage());
        const totalPages = this.totalPages();   

        return `${firstNumCurrentBlock} - ${lastNumCurrentBlock} de ${totalPages} paginas`
    })

    protected visiblePages = computed(() => {
        const total = this.totalPages();
        const current = this.page();
        const size = this.delta();
      
        if (total <= size) {
          return Array.from({ length: total }, (_, i) => i + 1);
        }
      
        const half = Math.floor(size / 2);
      
        let start: number;
      
        // Inicio (no centrar aún)
        if (current <= half + 1) {
          start = 1;
      
        // Final
        } else if (current >= total - half) {
          start = total - size + 1;
      
        // Centro
        } else {
          start = current - half;
        }
      
        return Array.from({ length: size }, (_, i) => start + i);
      });

    /**
     * Sets the value of `pageIndex`.
     * @param value The new value for `first`.
     */
    setPage(value: number): void {
        if (value < 1 || value > this.totalPages()) return;
        this.page.set(value);
    }

    /**
     * Returns the current value of `pageIndex`.
     * @returns The current page index* value.
     */
    getPage(): number {
        return this.page();
    }

    /**
     * Returns the current value of `total`.
     * @returns The current `total` value.
     */
    setTotalPages(number: number) {
        this.totalPages.set(number);
    }

    /**
     * Returns the current value of `total`.
     * @returns The current `total` value.
     */
    getTotalPages(): number {
        return this.totalPages();
    }

    /**
     * Returns the current value of `itemsPerPage`.
     * @returns The current `itemsPerPage` value.
     */
    setItemsPerPage(number: number) {
        this.itemsPerPage.set(number);
    }

    /**
     * Manualy manaje of the disable state of the component
     * @param state boolean state
     */
    setDisabled(state: boolean): void {
        this._disabled.set(state);
    }

    /**
     * Returns the current value of `itemsPerPage`.
     * @returns The current `itemsPerPage` value.
     */
    getItemsPerPage(): number {
        return this.itemsPerPage();
    }

    next() {
        this.setPage(this.page() + 1);
        this.onPageChanged.emit();
    }
    
    prev() {
        this.setPage(this.page() - 1);
        this.onPageChanged.emit();
    }
}