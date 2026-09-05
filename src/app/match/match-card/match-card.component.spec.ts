import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Match } from '../../core/interfaces/match';
import { Message } from '../../core/interfaces/message';
import { userFixture } from '../../core/testing/user.fixture';
import { MatchCardComponent } from './match-card.component';

describe('MatchCardComponent', () => {
  let fixture: ComponentFixture<MatchCardComponent>;
  let component: MatchCardComponent;
  let clicks: number;

  /** Builds the conversation held with Alice. */
  function match(messages: Partial<Message>[] = []): Match {
    return {
      user: userFixture({ id: 2, nickname: 'Alice' }),
      messages: messages.map((message, index) => ({
        id: index + 1,
        date: new Date(),
        sender: 'Alice',
        ...message,
      })),
    };
  }

  function render(value: Match, messageMode = false): void {
    fixture.componentRef.setInput('match', value);
    fixture.componentRef.setInput('messageMode', messageMode);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchCardComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(MatchCardComponent);
    component = fixture.componentInstance;
    clicks = 0;
    component.clickEvent.subscribe(() => clicks++);
  });

  it('displays the nickname of the match', () => {
    render(match());

    expect(fixture.nativeElement.textContent).toContain('Alice');
  });

  it('displays the preview it is given', () => {
    fixture.componentRef.setInput('match', match());
    fixture.componentRef.setInput('preview', 'see you tomorrow');
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('see you tomorrow');
  });

  it('signals a conversation whose last message comes from the match', () => {
    render(match([{ content: 'hello', sender: 'Alice' }]));

    expect(component.isLastMessageFromMatch()).toBeTrue();
  });

  it('signals nothing when the connected user wrote last', () => {
    render(
      match([
        { content: 'hello', sender: 'Alice' },
        { content: 'hi', sender: 'Bob' },
      ]),
    );

    expect(component.isLastMessageFromMatch()).toBeFalse();
  });

  it('signals nothing on a conversation that has not started', () => {
    render(match());

    expect(component.isLastMessageFromMatch()).toBeFalse();
  });

  it('ignores the deleted messages when looking for the last one', () => {
    render(
      match([
        { content: 'hello', sender: 'Alice' },
        { content: undefined, sender: 'Bob' },
      ]),
    );

    expect(component.isLastMessageFromMatch()).toBeTrue();
  });

  it('signals nothing when every message has been deleted', () => {
    render(match([{ content: undefined, sender: 'Alice' }]));

    expect(component.isLastMessageFromMatch()).toBeFalse();
  });

  it('stops signalling once the conversation is open', () => {
    render(match([{ content: 'hello', sender: 'Alice' }]), true);

    expect(component.isLastMessageFromMatch()).toBeFalse();
  });

  it('reports the click that opens the conversation', () => {
    render(match());

    component.click();

    expect(clicks).toBe(1);
  });
});
