import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHandlerFn } from '@angular/common/http';
import { Observable } from 'rxjs';

export function WithCredentialsInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
  console.log(req.url);
  const clonedRequest = req.clone({
      withCredentials: true
  });
  return next(clonedRequest);
}