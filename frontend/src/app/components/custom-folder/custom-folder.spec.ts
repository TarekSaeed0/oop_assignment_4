import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomFolderComponent } from './custom-folder';

describe('CustomFolderComponent', () => {
  let component: CustomFolderComponent;
  let fixture: ComponentFixture<CustomFolderComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomFolderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomFolderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
