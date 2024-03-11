import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    selector: 'app-tags',
    templateUrl: './tags.component.html',
    styleUrl: './tags.component.scss'
})
export class TagsComponent {
    constructor(private router: Router) { }

    navigateTo(): void {
        this.router.navigateByUrl('/tags/detail');
    }
}
