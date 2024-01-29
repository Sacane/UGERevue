import {Component, inject, OnInit} from '@angular/core';
import {UserService} from "../../shared/HttpServices";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit{
    private userService = inject(UserService)
    ngOnInit(): void {
        console.log("is connected => " + this.userService.isLogin())
    }
}
