import {AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs';

@Component({
    selector: 'app-sidenav-bar',
    templateUrl: './sidenav-bar.component.html',
    styleUrl: './sidenav-bar.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavBarComponent implements AfterViewInit {
    @Input() navs: any[] = [];

    private router = inject(Router)
    private ref = inject(ChangeDetectorRef)
    ngAfterViewInit(): void {
        this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(val => {
            if (val instanceof NavigationEnd) {
                this.ref.markForCheck();
            }
        });
    }

    isSelected(nav: any): boolean {
        return this.router && this.router.url.includes(nav.url);
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
