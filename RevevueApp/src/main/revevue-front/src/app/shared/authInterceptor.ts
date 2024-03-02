import {HttpEvent, HttpInterceptorFn, HttpResponse} from "@angular/common/http";
import {tap} from "rxjs";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    return next(req.clone({withCredentials: true}))
}
