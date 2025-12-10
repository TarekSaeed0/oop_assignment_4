import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MailInSent } from './mail-in-sent';

describe('MailInSent', () => {
  let component: MailInSent;
  let fixture: ComponentFixture<MailInSent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MailInSent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MailInSent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
