import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Message } from '../../core/interfaces/message';
import { MessageComponent } from './message.component';

describe('MessageComponent', () => {
  let fixture: ComponentFixture<MessageComponent>;

  /** Renders a message of the conversation opened with Alice. */
  function render(overrides: Partial<Message> = {}): void {
    fixture.componentRef.setInput('message', {
      id: 1,
      content: 'hello',
      date: new Date(),
      sender: 'Alice',
      ...overrides,
    });
    fixture.componentRef.setInput('userName', 'Alice');
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(MessageComponent);
  });

  it('lines up a message of the other profile on the left', () => {
    render({ sender: 'Alice' });

    expect(fixture.nativeElement.querySelector('.left')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('.right')).toBeNull();
  });

  it('lines up a message of the connected user on the right', () => {
    render({ sender: 'Bob' });

    expect(fixture.nativeElement.querySelector('.right')).not.toBeNull();
  });

  it('displays the content of the message', () => {
    render({ content: 'see you tomorrow' });

    expect(
      fixture.nativeElement.querySelector('.content').textContent.trim(),
    ).toBe('see you tomorrow');
  });

  it('keeps a deleted message in the conversation as a placeholder', () => {
    render({ content: undefined });

    expect(
      fixture.nativeElement.querySelector('.content').textContent.trim(),
    ).toBe('Message deleted');
    expect(fixture.nativeElement.querySelector('.deleted')).not.toBeNull();
  });

  it('only offers to edit a message written by the connected user', () => {
    render({ sender: 'Bob', content: 'hello' });
    expect(fixture.nativeElement.querySelector('.container').title).toBe(
      'Update Message',
    );

    render({ sender: 'Alice', content: 'hello' });
    expect(fixture.nativeElement.querySelector('.container').title).toBe('');
  });

  it('never offers to edit a message already deleted', () => {
    render({ sender: 'Bob', content: undefined });

    expect(fixture.nativeElement.querySelector('.container').title).toBe('');
  });
});
