import { Gender } from '../enums/gender';
import { UserRole } from '../enums/user-role';
import { User } from '../interfaces/user';

/**
 * Builds the account the specs work on, so that a test only declares what it asserts on.
 *
 * @param overrides fields overriding the defaults
 */
export function userFixture(overrides: Partial<User> = {}): User {
  return {
    id: 1,
    userRole: UserRole.ROLE_USER,
    birthDate: new Date('1995-06-15'),
    nickname: 'nickname',
    job: 'job',
    email: 'user@pickme.com',
    genderAge: {
      gender: Gender.MAN,
      genderSearch: Gender.WOMAN,
      minAge: 18,
      maxAge: 99,
    },
    geolocation: {
      latitude: '48.8566',
      longitude: '2.3522',
      distanceSearch: 100,
    },
    ...overrides,
  };
}
