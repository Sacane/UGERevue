import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { ReactiveFormsModule } from "@angular/forms";
import { SignupComponent } from "./signup.component";

@NgModule({
    declarations: [
        SignupComponent
    ],
    imports: [
        ReactiveFormsModule,
        SharedModule
    ],
    exports: [SignupComponent],
    bootstrap: [SignupComponent]
})
export class SignupModule { }
