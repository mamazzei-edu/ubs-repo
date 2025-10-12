import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core'; // Adicionado ViewChild e AfterViewInit
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Observable, map, startWith } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatTableDataSource, MatTableModule } from '@angular/material/table'; // Adicionado MatTableDataSource
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator'; // Adicionado MatPaginator
import { MAT_DATE_LOCALE, MatOptionModule } from '@angular/material/core';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_FORMATS } from '@angular/material/core';
import { AgendamentoService } from '../service/agendamento.service';
import { PacienteService } from '../service/paciente.service';
import { MedicoService } from '../service/medico.service';
import { Agendamento, AgendamentoRequest, StatusAgendamento, TIPOS_CONSULTA } from '../model/agendamento.model';
import { Paciente } from '../model/paciente.model';
import { Medico } from '../model/medico.model';

export const MY_FORMATS = {
  parse: { dateInput: 'DD/MM/YYYY' },
  display: {
    dateInput: 'DD/MM/YYYY',
    monthYearLabel: 'MMMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@Component({
  selector: 'app-agendamento',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatOptionModule, MatAutocompleteModule,
    MatButtonModule, MatTableModule, MatIconModule, MatChipsModule,
    MatDatepickerModule, MatSnackBarModule, MatPaginatorModule // Adicionado MatPaginatorModule
  ],
  templateUrl: './agendamento.component.html',
  styleUrls: ['./agendamento.component.scss'],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
    { provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS] },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
  ]
})
export class AgendamentoComponent implements OnInit, AfterViewInit {
  agendamentoForm: FormGroup;
  filtroForm: FormGroup;
  pacientes: Paciente[] = [];
  medicos: Medico[] = [];
  pacientesFiltrados!: Observable<Paciente[]>;
  medicosFiltrados: Medico[] = [];
  tiposConsulta = TIPOS_CONSULTA;
  statusOptions = Object.values(StatusAgendamento);
  
  dataSource = new MatTableDataSource<Agendamento>();
  displayedColumns = [ 'paciente', 'medico', 'dataHora', 'tipoConsulta', 'status', 'acoes' ];

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private fb: FormBuilder,
    private agendamentoService: AgendamentoService,
    private pacienteService: PacienteService,
    private medicoService: MedicoService,
    private snackBar: MatSnackBar
  ) {
    this.agendamentoForm = this.fb.group({
      pacienteInput: [''], pacienteId: ['', Validators.required],
      tipoConsulta: ['', Validators.required], medicoId: ['', Validators.required],
      dataConsulta: ['', Validators.required], horaConsulta: ['', Validators.required],
      observacoes: ['']
    });

    // Inicializa o formulário de filtro
    this.filtroForm = this.fb.group({
      termoBusca: [''],
      dataFiltro: [''],
      statusFiltro: ['']
    });
  }

  ngOnInit(): void {
    this.carregarDados();
    this.pacientesFiltrados = this.agendamentoForm.get('pacienteInput')!.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.nomeCompleto;
        return name ? this._filtrarPacientes(name) : this.pacientes.slice();
      })
    );
    this.agendamentoForm.get('tipoConsulta')!.valueChanges.subscribe(tipo => {
      this._filtrarMedicosPorTipo(tipo);
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  // --- MÉTODOS DE CARREGAMENTO DE DADOS ---
  private carregarDados(): void {
    this.carregarAgendamentos();
    this.carregarPacientes();
    this.carregarMedicos();
  }

  private carregarAgendamentos(): void {
    this.agendamentoService.listarTodos().subscribe({
      next: ags => {
        this.dataSource.data = ags;
        this.configurarFiltro();
      },
      error: () => this.mostrarMensagem('Erro ao carregar agendamentos')
    });
  }

  private carregarPacientes(): void {
    this.pacienteService.listarPacientes().subscribe({
      next: pcs => this.pacientes = pcs,
      error: () => this.mostrarMensagem('Erro ao carregar pacientes')
    });
  }

  private carregarMedicos(): void {
    this.medicoService.listarTodos().subscribe({
      next: mds => {
        this.medicos = mds;
        this.medicosFiltrados = [...mds];
      },
      error: () => this.mostrarMensagem('Erro ao carregar médicos')
    });
  }

  // --- MÉTODOS DE FILTRAGEM ---
  configurarFiltro(): void {
    this.dataSource.filterPredicate = (data: Agendamento, filter: string): boolean => {
      const searchString = JSON.parse(filter);
      const termoBusca = searchString.termoBusca?.toLowerCase() || '';
      const statusFiltro = searchString.statusFiltro;
      const dataFiltro = searchString.dataFiltro ? new Date(searchString.dataFiltro) : null;
      if (dataFiltro) {
        dataFiltro.setMinutes(dataFiltro.getMinutes() + dataFiltro.getTimezoneOffset());
      }

      const matchNome = termoBusca === '' ||
                        (data.paciente.nomeCompleto.toLowerCase().includes(termoBusca) ||
                         data.medico.nomeCompleto.toLowerCase().includes(termoBusca));
      
      const matchStatus = !statusFiltro || data.status === statusFiltro;

      const matchData = !dataFiltro || 
                        new Date(data.dataHoraConsulta).toDateString() === dataFiltro.toDateString();

      return matchNome && matchStatus && matchData;
    };
  }

  aplicarFiltro(): void {
    this.dataSource.filter = JSON.stringify(this.filtroForm.value);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
  
  limparFiltro(): void {
    this.filtroForm.reset({ termoBusca: '', dataFiltro: '', statusFiltro: '' });
    this.aplicarFiltro();
  }

  displayPaciente(paciente: Paciente): string { return paciente && paciente.nomeCompleto ? paciente.nomeCompleto : ''; }
  private _filtrarPacientes(valor: string): Paciente[] {
    const v = valor.toLowerCase();
    return this.pacientes.filter(p =>
      p.nomeCompleto.toLowerCase().includes(v) ||
      p.cpf.replace(/\D/g, '').includes(v.replace(/\D/g, ''))
    );
  }

  onPacienteSelecionado(p: Paciente): void { this.agendamentoForm.patchValue({ pacienteId: p.id, pacienteInput: p }); }
  private _filtrarMedicosPorTipo(tipo: string): void {
    if (!tipo || tipo === 'Consulta Clínica Geral' || tipo === 'Exame de Rotina' || tipo === 'Consulta de Retorno') {
      this.medicosFiltrados = [...this.medicos];
    } else {
      const especialidadeChave = tipo.replace('Consulta ', '').toLowerCase();
      this.medicosFiltrados = this.medicos.filter(medico => {
        if (typeof medico.especialidade === 'string') {
          return medico.especialidade.toLowerCase().includes(especialidadeChave);
        }
        return false;
      });
    }
    const medicoIdAtual = this.agendamentoForm.get('medicoId')!.value;
    if (!this.medicosFiltrados.some(m => m.id === medicoIdAtual)) {
      this.agendamentoForm.patchValue({ medicoId: null });
    }
  }

  formatarEspecialidade(m: Medico): string { const esp = m.especialidade; return Array.isArray(esp) ? esp.join(', ') : esp; }
  onSubmit(): void {
    if (this.agendamentoForm.invalid) return;
    const req = this._montarRequest();
    this.agendamentoService
      .verificarDisponibilidade(req.medicoId, req.dataHoraConsulta)
      .subscribe({
        next: (response: any) => {
          if (response.disponivel) {
            this.agendamentoService.criarAgendamento(req).subscribe({
              next: () => {
                this.mostrarMensagem('Agendamento criado com sucesso!');
                this.agendamentoForm.reset();
                Object.keys(this.agendamentoForm.controls).forEach(key => {
                  this.agendamentoForm.get(key)?.setErrors(null) ;
                });
                this.carregarAgendamentos();
              },
              error: () => this.mostrarMensagem('Erro ao criar agendamento.')
            });
          } else {
            this.mostrarMensagem('Horário indisponível. Escolha outro horário.');
          }
        },
        error: () => this.mostrarMensagem('Erro ao verificar disponibilidade.')
      });
  }

  cancelarAgendamento(id: number): void {
    if (!confirm('Tem certeza que deseja cancelar este agendamento?')) return;
    this.agendamentoService.cancelarAgendamento(id).subscribe({
      next: () => {
        this.mostrarMensagem('Agendamento cancelado');
        this.carregarAgendamentos();
      },
      error: () => this.mostrarMensagem('Erro ao cancelar agendamento')
    });
  }

  confirmarAgendamento(id: number): void {
    this.agendamentoService.confirmarAgendamento(id).subscribe({
      next: () => {
        this.mostrarMensagem('Agendamento confirmado');
        this.carregarAgendamentos();
      },
      error: () => this.mostrarMensagem('Erro ao confirmar agendamento')
    });
  }

  getStatusColor(status: StatusAgendamento): string {
    switch (status) {
      case StatusAgendamento.AGENDADO: return 'primary';
      case StatusAgendamento.CONFIRMADO: return 'accent';
      case StatusAgendamento.CANCELADO: return 'warn';
      case StatusAgendamento.REALIZADO: return 'primary';
      case StatusAgendamento.FALTOU: return 'warn';
      default: return 'primary';
    }
  }

  formatarDataHora(s: string): string { return new Date(s).toLocaleString('pt-BR'); }
  private _montarRequest(): AgendamentoRequest {
    const f = this.agendamentoForm.value;
    const dataSelecionada = new Date(f.dataConsulta);
    const hora = f.horaConsulta;
    dataSelecionada.setMinutes(dataSelecionada.getMinutes() - dataSelecionada.getTimezoneOffset());
    const dataISO = dataSelecionada.toISOString().slice(0, 10);
    const dataHoraParaBackend = `${dataISO}T${hora}`;
    return {
      pacienteId: f.pacienteId,
      medicoId: f.medicoId,
      dataHoraConsulta: dataHoraParaBackend,
      tipoConsulta: f.tipoConsulta,
      observacoes: f.observacoes
    };
  }
  private mostrarMensagem(msg: string): void {
    this.snackBar.open(msg, 'Fechar', {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
    });
  }
}