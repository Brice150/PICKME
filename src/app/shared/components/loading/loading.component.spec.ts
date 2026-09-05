import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingComponent } from './loading.component';

describe('LoadingComponent', () => {
  let fixture: ComponentFixture<LoadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(LoadingComponent);
    fixture.detectChanges();
  });

  it('renders the spinner the other screens display while they load', () => {
    expect(fixture.nativeElement.querySelector('.loader')).not.toBeNull();
  });
});
