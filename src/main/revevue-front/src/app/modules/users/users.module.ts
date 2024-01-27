import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { UsersComponent } from "./users.component";
import { UsersRoutingModule } from "./users-routing.module";

@NgModule({
    declarations: [
        UsersComponent
    ],
    imports: [
        UsersRoutingModule,
        SharedModule
    ],
    exports: [UsersComponent],
    bootstrap: [UsersComponent]
})
export class UsersModule { }
