import { Injectable } from '@angular/core';

@Injectable({providedIn: 'root'})
export class CurrentUserService {
    
    private id: number = 0;
    private roles: string[] = ['ADMINISTRADOR'];
    
    /**
     * Sets the current logged-in user id.
     * @param id Current user id.
     */
    setId(id: number): void {
        this.id = id;
        if (id) {
            sessionStorage.setItem('currentUserId', id.toString());
        }
    }

    /**
     * Fetches the current user id.
     * @returns Current user id.
     */
    getUserId(): number {
        if (!this.id) {
            const stored = sessionStorage.getItem('currentUserId');
            if (stored) {
                this.id = Number(stored);
            } else {
                const token = sessionStorage.getItem('accessToken');
                if (token) {
                    try {
                        const payload = JSON.parse(atob(token.split('.')[1]));
                        const sub = payload.preferred_username || payload.sub;
                        if (sub && !isNaN(Number(sub))) {
                            this.id = Number(sub);
                            sessionStorage.setItem('currentUserId', this.id.toString());
                        }
                    } catch (e) { }
                }
            }
        }
        return this.id;
    }

    /**
     * Sets the array of roles for the user.
     * @param roles Array of roles.
     */
    setRoles(roles: [string]): void {
        this.roles = roles;
    }

    /**
     * Fetches a copy of the current user roles.
     * @returns Copy of the array of the current user roles
     */
    getRoles(): string[] {
        return [...this.roles];
    }
    
    constructor() { }
    
}