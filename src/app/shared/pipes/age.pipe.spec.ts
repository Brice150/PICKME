import { AgePipe } from './age.pipe';

describe('AgePipe', () => {
  let pipe: AgePipe;

  beforeEach(() => {
    pipe = new AgePipe();
    vi.useFakeTimers();
    vi.setSystemTime(new Date(2026, 5, 15));
  });

  afterEach(() => vi.useRealTimers());

  it('returns zero when no birth date is known', () => {
    expect(pipe.transform(undefined)).toBe(0);
  });

  it('counts the complete years elapsed since the birth date', () => {
    expect(pipe.transform(new Date(1996, 5, 15))).toBe(30);
  });

  it('does not count the birthday of the current year before it happens', () => {
    expect(pipe.transform(new Date(1996, 5, 16))).toBe(29);
    expect(pipe.transform(new Date(1996, 6, 1))).toBe(29);
  });

  it('counts the birthday of the current year once it has happened', () => {
    expect(pipe.transform(new Date(1996, 4, 30))).toBe(30);
  });
});
