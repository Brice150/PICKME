import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: 'age'
})
export class AgePipe implements PipeTransform {
    transform(birth: Date | undefined): number {
        let age: number = 0;
        let today: Date = new Date();
        if (birth) {
            let birthDate: Date = new Date(birth);
            let dateDifference: Date = new Date(
              today.getFullYear() - birthDate.getFullYear(), 
              today.getMonth() - birthDate.getMonth(), 
              today.getDate() - birthDate.getDate()
            )
            age = dateDifference.getFullYear();
            age = Number(String(age).slice(-2));
        }
        return age;
    }
}