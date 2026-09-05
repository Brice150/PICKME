import { DatePipe } from '@angular/common';
import { LOCALE_ID, Pipe, PipeTransform, inject } from '@angular/core';

/**
 * Formats the date of a message: the hour alone for a message of the day, the full date otherwise.
 */
@Pipe({
  name: 'customDate',
  standalone: true,
})
export class CustomDatePipe implements PipeTransform {
  private readonly datePipe = new DatePipe(inject(LOCALE_ID));

  transform(date: Date): string | null {
    if (!date) {
      return null;
    }
    const messageDate = new Date(date);
    return this.isToday(messageDate)
      ? this.datePipe.transform(date, 'HH:mm')
      : this.datePipe.transform(date, 'dd/MM/yyyy');
  }

  private isToday(date: Date): boolean {
    const today = new Date();
    return (
      date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth() &&
      date.getDate() === today.getDate()
    );
  }
}
