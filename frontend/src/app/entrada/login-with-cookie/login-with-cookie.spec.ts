import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginWithCookie } from './login-with-cookie';

describe('LoginWithCookie', () => {
  let component: LoginWithCookie;
  let fixture: ComponentFixture<LoginWithCookie>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginWithCookie]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginWithCookie);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
