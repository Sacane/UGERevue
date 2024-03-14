import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {ReactiveFormsModule} from "@angular/forms";
import {SignupComponent} from "./signup.component";
import {SignupRoutingModule} from "./signup-routing.module";
import {ToastContainerDirective, ToastrModule} from "ngx-toastr";

@NgModule({
    declarations: [
        SignupComponent
    ],
    imports: [
        ReactiveFormsModule,
        SignupRoutingModule,
        SharedModule,
        ToastrModule,
        ToastContainerDirective
    ],
    exports: [SignupComponent],
    bootstrap: [SignupComponent]
})
export class SignupModule { }
