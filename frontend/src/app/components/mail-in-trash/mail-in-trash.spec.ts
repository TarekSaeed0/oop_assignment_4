import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MailInTrash } from './mail-in-trash';

describe('MailInTrash', () => {
  let component: MailInTrash;
  let fixture: ComponentFixture<MailInTrash>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MailInTrash]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MailInTrash);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
