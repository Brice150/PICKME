import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'description',
  standalone: true,
})
export class DescriptionPipe implements PipeTransform {
  transform(description: string | undefined, nb: number): string | undefined {
    if (description && description.length > nb) {
      description = description.substring(0, nb - 3) + '...';
    }
    return description;
  }
}
