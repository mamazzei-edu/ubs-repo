import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListaComponent } from './lista.component';
import { PacienteService } from '../service/paciente.service';
import { of } from 'rxjs'; 
import { Paciente } from '../model/paciente.model';

describe('ListaComponent', () => {
  let component: ListaComponent;
  let fixture: ComponentFixture<ListaComponent>;
  let pacienteService: jasmine.SpyObj<PacienteService>;

  beforeEach(async () => {
    // Criando o mock do serviço
    const pacienteServiceSpy = jasmine.createSpyObj('PacienteService', ['listar', 'remover', 'buscarPorCodigo']);

    await TestBed.configureTestingModule({
      declarations: [ListaComponent],
      providers: [
        { provide: PacienteService, useValue: pacienteServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListaComponent);
    component = fixture.componentInstance;
    pacienteService = TestBed.inject(PacienteService) as jasmine.SpyObj<PacienteService>;

    // Mockando a resposta do método listar com a asserção de tipo
    pacienteService.listar.and.returnValue(of([
      {
        codigo: 1,  // Aqui 'codigo' está sendo usado, mas a asserção força o tipo
        nomeCompleto: 'João da Silva',
        nomeSocial: 'João',
        nomeMae: 'Maria da Silva',
        nomePai: 'José da Silva',
        dataNascimento: new Date('2000-01-01'),
        sexo: 'Masculino',
        nacionalidade: 'Brasileiro',
        municipioNascimento: 'São Paulo',
        racaCor: 'Branca',
        frequentaEscola: 'Sim',
        deficiente: 'Não',
        visual: 'Não',
        auditiva: 'Não',
        motora: 'Não',
        intelectual: 'Não',
        contatoCelular: '11987654321',
        contatoResidencial: '1122334455',
        contatoComercial: '1133445566',
        contatoEmail: 'joao@example.com'
      },
      {
        codigo: 2,  
        nomeCompleto: 'Maria Oliveira',
        nomeSocial: 'Maria',
        nomeMae: 'Ana Oliveira',
        nomePai: 'Carlos Oliveira',
        dataNascimento: new Date('1995-03-15'),
        sexo: 'Feminino',
        nacionalidade: 'Brasileira',
        municipioNascimento: 'Rio de Janeiro',
        racaCor: 'Parda',
        frequentaEscola: 'Não',
        deficiente: 'Sim',
        visual: 'Sim',
        auditiva: 'Não',
        motora: 'Não',
        intelectual: 'Sim',
        telefoneCelular: '11999887766',
        telefoneResidencial: '2133445566',
        email: 'maria@example.com'
      }
    ] as Paciente[]));  // Asserção de tipo

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list pacientes correctly', () => {
    // Testando a lista de pacientes mockada
    component.listar();
    expect(component.pacientes.length).toBe(2);  // Verifica se a lista tem dois pacientes
    expect(component.pacientes[0].nomeCompleto).toBe('João da Silva');  // Verifica o nome do primeiro paciente
    expect(component.pacientes[1].nomeCompleto).toBe('Maria Oliveira');  // Verifica o nome do segundo paciente
  });
});
