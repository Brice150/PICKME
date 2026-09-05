import { DescriptionPipe } from './description.pipe';

describe('DescriptionPipe', () => {
  const pipe = new DescriptionPipe();

  it('returns nothing when there is no description', () => {
    expect(pipe.transform(undefined, 10)).toBeUndefined();
  });

  it('keeps a description short enough untouched', () => {
    expect(pipe.transform('short', 10)).toBe('short');
  });

  it('truncates a description longer than the limit', () => {
    expect(pipe.transform('a very long description', 10)).toBe('a very ...');
  });

  it('never returns more characters than the limit', () => {
    expect(pipe.transform('a very long description', 10)?.length).toBe(10);
  });
});
