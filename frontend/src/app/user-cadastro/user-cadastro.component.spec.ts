import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserCadastroComponent } from './user-cadastro.component';

describe('UserCadastroComponent', () => {
  let component: UserCadastroComponent;
  let fixture: ComponentFixture<UserCadastroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserCadastroComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserCadastroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
