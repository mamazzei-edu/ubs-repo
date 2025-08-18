import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListaUsuariosComponent } from './listaUsuario.component'; 
import { LoginService } from '../service/login.service'; 
import { of } from 'rxjs';
import { User } from '../model/user.model'; 

describe('ListaUsuarioComponent', () => {
  let component: ListaUsuariosComponent;
  let fixture: ComponentFixture<ListaUsuariosComponent>;
  let userService: jasmine.SpyObj<LoginService>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['listar', 'remover', 'buscarPorMatricula']);

    await TestBed.configureTestingModule({
      declarations: [ListaUsuariosComponent],
      providers: [
        { provide: LoginService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListaUsuariosComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(LoginService) as jasmine.SpyObj<LoginService>;

    userService.listar.and.returnValue(of([
      {
        matricula: '12345',
        email: 'joao.silva@example.com',
        senha: 'senha123',
        funcao: 'ADMIN'
      },
      {
        matricula: '67890',
        email: 'maria.oliveira@example.com',
        senha: 'senha456',
        funcao: 'USER'
      }
    ] as User[]));

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should list users correctly', () => {
    component.listar();
    expect(component.usuarios.length).toBe(2);
    expect(component.usuarios[0].matricula).toBe('12345');
    expect(component.usuarios[1].email).toBe('maria.oliveira@example.com');
  });

 
});
