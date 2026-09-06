import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Gender } from '../../core/enums/gender';
import { MatchDemoComponent } from './match-demo.component';

describe('MatchDemoComponent', () => {
  let fixture: ComponentFixture<MatchDemoComponent>;
  let component: MatchDemoComponent;

  function render(genderSearch: Gender): void {
    fixture.componentRef.setInput('userGenderSearch', genderSearch);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchDemoComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(MatchDemoComponent);
    component = fixture.componentInstance;
  });

  it('shows two sample conversations', () => {
    render(Gender.WOMAN);

    expect(component.images.length).toBe(2);
    expect(component.images[0]).toContain('woman-select-demo/Picture1.jpg');
  });

  it('shows the sample men to someone looking for a man', () => {
    render(Gender.MAN);

    expect(component.images[0]).toContain('man-select-demo/Picture1.jpg');
    expect(component.isWomanGenderSearch()).toBe(false);
  });

  it('adapts the names of the samples to the gender being searched', () => {
    render(Gender.WOMAN);

    expect(component.isWomanGenderSearch()).toBe(true);
  });

  it('opens a conversation and closes it again', () => {
    render(Gender.WOMAN);

    component.selectMatch(component.images[0]);
    expect(component.selectedMatch).toBe(component.images[0]);

    component.back();
    expect(component.selectedMatch).toBeUndefined();
  });
});
