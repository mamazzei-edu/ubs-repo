import { Injectable } from '@angular/core';
import { CanActivate, Route, Router } from '@angular/router';
import { AuthService } from './entrada/logado/auth-service';

@Injectable({
  providedIn: 'root'
})

export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }

}

// export class AuthGuard implements CanActivate: CanActivateFn = (route: Route) => {
//   const authService: AuthService = Inject(AuthService);
//   const router: Router = Inject(Router);

//   if (authService.isLoggedIn() === true) {
// //    router.navigate(["/"]);
//     return true;
//   } else {
//     router.navigate(["/login"]);
//     return false;
//   }
  
// };
