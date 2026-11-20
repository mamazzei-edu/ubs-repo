import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImcComponent } from './imc.component';

describe('ImcComponent', () => {
  let component: ImcComponent;
  let fixture: ComponentFixture<ImcComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImcComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ImcComponent);
    component = fixture.componentInstance;
  });

  it('deve calcular e classificar um IMC adequado', () => {
    expect(component.calcularImc(70, 1.75)).toEqual({
      valor: 22.9,
      classificacao: 'Peso adequado',
      classe: 'adequado'
    });
  });

  it('deve classificar os limites das faixas', () => {
    expect(component.calcularImc(18.5, 1).classificacao).toBe('Peso adequado');
    expect(component.calcularImc(25, 1).classificacao).toBe('Sobrepeso');
    expect(component.calcularImc(30, 1).classificacao).toBe('Obesidade');
  });

  it('não deve calcular quando o formulário for inválido', () => {
    component.imcForm.setValue({ peso: 0, altura: 0 });

    component.calcular();

    expect(component.resultado).toBeNull();
  });
});
