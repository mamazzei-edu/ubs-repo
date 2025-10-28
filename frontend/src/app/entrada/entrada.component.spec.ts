import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntradaComponent } from './entrada.component';

describe('EntradaComponent', () => {
  let component: EntradaComponent;
  let fixture: ComponentFixture<EntradaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntradaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EntradaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
