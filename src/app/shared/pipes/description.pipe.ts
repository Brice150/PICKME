import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'description',
})
export class DescriptionPipe implements PipeTransform {
  transform(description: string | null, nb: number): string | null {
    if (description && description.length > nb) {
      description = description.substring(0, nb - 3) + '...';
    }
    return description;
  }
}
