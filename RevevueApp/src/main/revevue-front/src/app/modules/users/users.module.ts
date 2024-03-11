import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {UsersComponent} from "./users.component";
import {UsersRoutingModule} from "./users-routing.module";
import {NgForOf, NgIf} from "@angular/common";

@NgModule({
    declarations: [
        UsersComponent
    ],
    imports: [
        UsersRoutingModule,
        SharedModule,
        NgForOf,
        NgIf,
    ],
    exports: [UsersComponent],
    bootstrap: [UsersComponent]
})
export class UsersModule { }
