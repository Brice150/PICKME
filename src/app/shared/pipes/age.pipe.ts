import { Pipe, PipeTransform } from '@angular/core';

/**
 * Turns a birth date into the number of complete years elapsed since then.
 */
@Pipe({
  name: 'age',
  standalone: true,
})
export class AgePipe implements PipeTransform {
  transform(birth: Date | undefined): number {
    if (!birth) {
      return 0;
    }
    const birthDate = new Date(birth);
    const today = new Date();
    const age = today.getFullYear() - birthDate.getFullYear();
    return this.hasHadBirthdayThisYear(birthDate, today) ? age : age - 1;
  }

  private hasHadBirthdayThisYear(birthDate: Date, today: Date): boolean {
    if (today.getMonth() !== birthDate.getMonth()) {
      return today.getMonth() > birthDate.getMonth();
    }
    return today.getDate() >= birthDate.getDate();
  }
}
