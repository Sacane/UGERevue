import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SignupComponent} from "./signup.component";
import {SignupRoutingModule} from "./signup-routing.module";

@NgModule({
    declarations: [
        SignupComponent
    ],
    imports: [
        ReactiveFormsModule,
        SignupRoutingModule,
        SharedModule
    ],
    exports: [SignupComponent],
    bootstrap: [SignupComponent]
})
export class SignupModule { }
