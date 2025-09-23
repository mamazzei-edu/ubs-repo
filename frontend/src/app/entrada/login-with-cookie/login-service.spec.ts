import { TestBed } from '@angular/core/testing';

import { LoginServiceCookie } from './login-service-cookie';

describe('LoginService', () => {
  let service: LoginServiceCookie;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginServiceCookie);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
