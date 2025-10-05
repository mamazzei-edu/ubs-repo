import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Logado } from './logado';

describe('Logado', () => {
  let component: Logado;
  let fixture: ComponentFixture<Logado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Logado]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Logado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
