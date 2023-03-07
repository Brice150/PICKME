import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from 'src/app/dialog/dialog.component';
import { Message } from 'src/app/core/interfaces/message';
import { AdminService } from 'src/app/core/services/admin.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-admin-messages',
  templateUrl: './admin.messages.component.html',
  styleUrls: ['./admin.messages.component.css']
})
export class AdminMessagesComponent implements OnInit{
  messages: Message[]=[];

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService,
    public dialog: MatDialog) {}

  ngOnInit() {
    this.getMessages();
  }

  getMessages() {
    this.adminService.getAllMessages().subscribe(
      (response: Message[]) => {
        this.messages = response;
        const loaderWrapper = document.getElementById('loaderWrapper');
        loaderWrapper!.style.display = 'none';
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message);
      }
    )
  }

  deleteMessage(id: number) {
    this.adminService.deleteMessage(id).subscribe(
      (response: void) => {
        this.getMessages();
        this.toastr.success("Message deleted");
      },
      (error: HttpErrorResponse) => {
        this.toastr.error(error.message);
      }
    )
  }
  
  openDialog(id: number) {
    const dialogRef = this.dialog.open(DialogComponent);
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteMessage(id);
      }
    });
  }

  search(key: string){
    const results: Message[] = [];
    for (const message of this.messages) {
      if (message.content?.toLowerCase().indexOf(key.toLowerCase())!== -1) {
        results.push(message);
      }
    }
    this.messages = results;
    if (results.length === 0 ||!key) {
      this.getMessages();
    }
  }
}
