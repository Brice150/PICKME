import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { Picture } from '../../core/interfaces/picture';
import { User } from '../../core/interfaces/user';
import { ProfileService } from '../../core/services/profile.service';
import { userFixture } from '../../core/testing/user.fixture';
import { PicturesComponent } from './pictures.component';

describe('PicturesComponent', () => {
  let fixture: ComponentFixture<PicturesComponent>;
  let component: PicturesComponent;
  let profileService: jasmine.SpyObj<ProfileService>;
  let user: User;
  let refreshes: string[];

  function picture(id: number, isMainPicture: boolean): Picture {
    return { id, content: 'base64-' + id, isMainPicture };
  }

  /** Renders the album of the connected user. */
  function render(pictures: Picture[]): void {
    user = userFixture({ pictures });
    fixture.componentRef.setInput('user', user);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    profileService = jasmine.createSpyObj<ProfileService>('ProfileService', [
      'addPicture',
      'deletePicture',
      'selectMainPicture',
    ]);
    await TestBed.configureTestingModule({
      imports: [PicturesComponent],
      providers: [
        provideNoopAnimations(),
        { provide: ProfileService, useValue: profileService },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(PicturesComponent);
    component = fixture.componentInstance;
    refreshes = [];
    component.refreshEvent.subscribe((message) => refreshes.push(message));
  });

  /**
   * Builds a real 1x1 PNG, so that the resizing runs on an image the browser can actually decode
   * rather than on a stub that would never fire its load event.
   */
  function pngFile(): File {
    const base64 =
      'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==';
    const binary = atob(base64);
    const bytes = Uint8Array.from(binary, (character) =>
      character.charCodeAt(0),
    );
    return new File([bytes], 'avatar.png', { type: 'image/png' });
  }

  it('adds the sent picture and clears the file input', (done) => {
    render([picture(1, true)]);
    profileService.addPicture.and.returnValue(of(picture(2, false)));
    const input: HTMLInputElement =
      fixture.nativeElement.querySelector('#image');

    component.refreshEvent.subscribe(() => {
      expect(user.pictures?.[0].id).toBe(2);
      expect(profileService.addPicture).toHaveBeenCalled();
      // Reading the query is what used to be untested: an input the template no longer holds
      // would fail here instead of silently later.
      expect(input.value).toBe('');
      expect(component.activeIndex).toBe(0);
      done();
    });

    component.addPicture([pngFile()]);
  });

  it('moves the main flag to the promoted picture', () => {
    render([picture(1, true), picture(2, false)]);
    profileService.selectMainPicture.and.returnValue(of(undefined));

    component.selectMainPicture(2);

    expect(user.pictures?.map((p) => p.isMainPicture)).toEqual([false, true]);
    expect(refreshes).toEqual(['Main Picture Selected']);
    expect(component.isLoading).toBeFalse();
  });

  it('removes a deleted picture from the album', () => {
    render([picture(1, true), picture(2, false)]);
    profileService.deletePicture.and.returnValue(of(undefined));

    component.deletePicture(2);

    expect(user.pictures?.map((p) => p.id)).toEqual([1]);
    expect(refreshes).toEqual(['Picture Deleted']);
  });

  it('promotes the next picture when the main one is deleted', () => {
    render([picture(1, true), picture(2, false)]);
    profileService.deletePicture.and.returnValue(of(undefined));

    component.deletePicture(1);

    expect(user.pictures?.map((p) => p.id)).toEqual([2]);
    expect(user.pictures?.[0].isMainPicture).toBeTrue();
  });

  it('leaves the album untouched when the deleted picture is already gone', () => {
    render([picture(1, true)]);
    profileService.deletePicture.and.returnValue(of(undefined));

    component.deletePicture(99);

    expect(user.pictures?.length).toBe(1);
    expect(refreshes).toEqual([]);
  });

  it('keeps track of the picture the carousel stops on', () => {
    render([picture(1, true)]);
    component.activeIndex = 3;

    component.onSlideChange();

    expect(component.activeIndex).toBe(3);
  });
});
