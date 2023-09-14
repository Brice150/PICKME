import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'role',
})
export class RolePipe implements PipeTransform {
  transform(role: string | null): string | null {
    if (role && role.includes('_')) {
      role = role.split('_')[1];
    }
    return role;
  }
}
