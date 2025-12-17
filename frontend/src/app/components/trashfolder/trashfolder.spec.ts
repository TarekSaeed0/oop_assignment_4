import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrashfolderComponent } from './trashfolder';


describe('Trashfolder', () => {
  let component: TrashfolderComponent;
  let fixture: ComponentFixture<TrashfolderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrashfolderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrashfolderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
