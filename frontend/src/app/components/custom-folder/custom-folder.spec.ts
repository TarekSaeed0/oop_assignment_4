import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomFolder } from './custom-folder';

describe('CustomFolder', () => {
  let component: CustomFolder;
  let fixture: ComponentFixture<CustomFolder>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomFolder]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomFolder);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
