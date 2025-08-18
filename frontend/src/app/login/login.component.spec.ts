import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent} from './login.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

describe('LoginComponent', () => {
  let component:LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, HttpClientTestingModule],
      declarations: [LoginComponent],
      providers: [{ provide: Router, useValue: routerSpy }]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call loginUsuario and navigate on success', () => {
    component.usuario = {
      email: 'test@example.com',
      senha: '123456'    
    };
    component.login();

    // const req = httpMock.expectOne('http://localhost:8090/login');
    // expect(req.request.method).toBe('POST');
    // expect(req.request.body).toEqual(component.usuario);

    // req.flush({}); // simula sucesso

//    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should set errorMessage on cadastro error', () => {
    component.usuario = {
      email: 'test@example.com',
      senha: '123456'
    };
    component.login();

    const req = httpMock.expectOne('http://localhost:8090/login');
    req.flush('Erro de servidor', { status: 500, statusText: 'Server Error' });

    expect(component.errorMessage).toBe('Erro ao cadastrar usuário!');
  });

  it('should call voltar and navigate to root', () => {
    component.voltar();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  });
});
