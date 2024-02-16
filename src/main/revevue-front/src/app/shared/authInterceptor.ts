import {HttpEvent, HttpInterceptorFn, HttpResponse} from "@angular/common/http";
import {tap} from "rxjs";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    if(localStorage.getItem("isLoggin") === "true"){
        return next(req.clone({withCredentials: true}))
    }
    return next(req).pipe(tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
            console.log('Headers of HTTP response:', event.headers);
        }
    }))
}
