import { NgModule } from "@angular/core";
import { LoginComponent } from "./login.component";
import { SharedModule } from "../../shared/shared.module";
import { ReactiveFormsModule } from "@angular/forms";

@NgModule({
    declarations: [
        LoginComponent
    ],
    imports: [
        ReactiveFormsModule,
        SharedModule
    ],
    exports: [LoginComponent],
    bootstrap: [LoginComponent]
})
export class LoginModule { }
