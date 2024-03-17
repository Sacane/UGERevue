import {HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable, tap} from "rxjs";
import {inject} from "@angular/core";
import {Router} from "@angular/router";

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const router = inject(Router);
    return next(req.clone({withCredentials: true}))
        .pipe(
            tap({
                next: () => {
                },
                error: err => {
                    if (err instanceof HttpErrorResponse) {
                        if (err.status !== 401 && err.status !== 403) {
                            return;
                        }
                        localStorage.setItem('isLoggin', 'false');
                        router.navigate(['login']);
                    }
                }
            })
        );
}
