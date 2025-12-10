import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoveToMenu } from './move-to-menu';

describe('MoveToMenu', () => {
  let component: MoveToMenu;
  let fixture: ComponentFixture<MoveToMenu>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoveToMenu]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoveToMenu);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
