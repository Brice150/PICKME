import { Component, Input } from '@angular/core';
import { User } from 'src/app/core/interfaces/user';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent {
  imagePath: string = environment.imagePath;
  @Input() user: User = {} as User;
}
