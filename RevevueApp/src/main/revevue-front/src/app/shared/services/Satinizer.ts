import {Pipe, PipeTransform, inject} from "@angular/core";
import {DomSanitizer} from "@angular/platform-browser";

//@Pipe({standalone: true, name: 'sanitizer'})
export class Sanitizer implements PipeTransform{
    private sanitizer = inject(DomSanitizer)
    transform(value: any, ...args: any[]): any  {
        console.log('test sanitizer')
        return this.sanitizer.bypassSecurityTrustHtml(value)
    }

}
