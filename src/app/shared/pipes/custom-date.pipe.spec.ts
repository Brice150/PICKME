import { TestBed } from '@angular/core/testing';
import { CustomDatePipe } from './custom-date.pipe';

describe('CustomDatePipe', () => {
  let pipe: CustomDatePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [CustomDatePipe] });
    pipe = TestBed.inject(CustomDatePipe);
  });

  it('returns nothing when there is no date', () => {
    expect(pipe.transform(undefined as unknown as Date)).toBeNull();
  });

  it('only shows the hour of a message of the day', () => {
    const today = new Date();
    today.setHours(14, 5, 0, 0);

    expect(pipe.transform(today)).toBe('14:05');
  });

  it('shows the full date of an older message', () => {
    expect(pipe.transform(new Date(2024, 2, 9, 14, 5))).toBe('09/03/2024');
  });
});
