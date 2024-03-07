import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ProfileRoutingModule} from './profile-routing.module';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";


@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        ProfileRoutingModule
    ],
    providers: [
        {
            provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
            useValue: {
                subscriptSizing: 'dynamic'
            }
        }
    ]
})
export class ProfileModule {
}
