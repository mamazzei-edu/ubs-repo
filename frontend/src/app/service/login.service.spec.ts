import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AuthService } from '../service/auth.service';
import { User } from '../model/user.model';
import { LoginService } from '../service/login.service';


describe('LoginService', () => {
  let service: LoginService;
  let authservice: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    authservice = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  

 
});
