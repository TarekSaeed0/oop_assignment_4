import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateFolderDialogComponent } from './create-folder-dialog'; // Ensure path is correct
import { UserFolderService } from '../../services/UserFolderService';
import { of, throwError } from 'rxjs';

describe('CreateFolderDialogComponent', () => {
  let component: CreateFolderDialogComponent;
  let fixture: ComponentFixture<CreateFolderDialogComponent>;
  let mockUserFolderService: jasmine.SpyObj<UserFolderService>;

  beforeEach(async () => {
    // 1. Create a "fake" service that spies on the methods we use
    mockUserFolderService = jasmine.createSpyObj('UserFolderService', [
      'isValidNameForUserFolder', 
      'createFolder'
    ]);

    await TestBed.configureTestingModule({
      // Import the standalone component
      imports: [CreateFolderDialogComponent], 
      // 2. Provide the fake service instead of the real one
      providers: [
        { provide: UserFolderService, useValue: mockUserFolderService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateFolderDialogComponent);
    component = fixture.componentInstance;
    
    // Set required @Input
    component.userId = 1; 
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT call service if folder name is empty', () => {
    component.folderName = '   '; // Empty/Whitespace
    component.tryCreateFolder();
    
    expect(mockUserFolderService.isValidNameForUserFolder).not.toHaveBeenCalled();
  });

  it('should show error if folder name already exists', () => {
    // Arrange: User enters name, Service says "Name is NOT valid (exists)"
    component.folderName = 'ExistingFolder';
    // Mock the validation check to return false (false = valid check passed? No. 
    // Logic: isValidNameForUserFolder returns boolean. 
    // If your backend returns true for "Name is available", use true. 
    // If it returns true for "Name exists", use false. 
    // Based on your TS: if(isValid) -> create. So true means "Unique/Good".
    
    mockUserFolderService.isValidNameForUserFolder.and.returnValue(of(false)); 

    // Act
    component.tryCreateFolder();

    // Assert
    expect(mockUserFolderService.isValidNameForUserFolder).toHaveBeenCalledWith('ExistingFolder', 1);
    expect(mockUserFolderService.createFolder).not.toHaveBeenCalled(); // Should stop here
    expect(component.errorMessage).toBe('Folder name already exists.');
  });

  it('should create folder and emit events if name is valid', () => {
    // Arrange
    component.folderName = 'NewFolder';
    // Mock 1: Name is valid
    mockUserFolderService.isValidNameForUserFolder.and.returnValue(of(true));
    // Mock 2: Create folder success
    mockUserFolderService.createFolder.and.returnValue(of('Success'));
    
    // Spy on the outputs
    spyOn(component.folderCreated, 'emit');
    spyOn(component.close, 'emit');

    // Act
    component.tryCreateFolder();

    // Assert
    expect(mockUserFolderService.createFolder).toHaveBeenCalledWith('NewFolder', 1);
    expect(component.folderCreated.emit).toHaveBeenCalled();
    expect(component.close.emit).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  });

  it('should handle network error during creation', () => {
    // Arrange
    component.folderName = 'NewFolder';
    mockUserFolderService.isValidNameForUserFolder.and.returnValue(of(true));
    // Mock: Create fails with error
    mockUserFolderService.createFolder.and.returnValue(throwError(() => new Error('Server Error')));

    // Act
    component.tryCreateFolder();

    // Assert
    expect(component.errorMessage).toBe('Could not create folder.');
    expect(component.isLoading).toBeFalse();
  });
});