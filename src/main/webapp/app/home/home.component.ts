import { HttpResponse } from '@angular/common/http';
import { AfterViewInit, Component, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby/baby-profile/service/baby-profile.service';
import { IBreastFeed } from 'app/entities/baby/breast-feed/breast-feed.model';
import { BreastFeedDeleteDialogComponent } from 'app/entities/baby/breast-feed/delete/breast-feed-delete-dialog.component';
import { BreastFeedService } from 'app/entities/baby/breast-feed/service/breast-feed.service';
import { NapDeleteDialogComponent } from 'app/entities/baby/nap/delete/nap-delete-dialog.component';
import { INap } from 'app/entities/baby/nap/nap.model';
import { NapService } from 'app/entities/baby/nap/service/nap.service';
import * as dayjs from 'dayjs';
import { NvD3Component } from 'ng2-nvd3';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { D3ChartService } from '../shared/d3-chart.service';
@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy, AfterViewInit {
  account: Account | null = null;
  currentTodayDateFormat = dayjs(Date.now()).format('D MMM YYYY');
  currentTodayDateShortFormat = dayjs(Date.now()).format('D MMM');
  babyProfiles?: IBabyProfile[];
  napToday: any = {};
  napLastCurrentWeek: any = {};
  napsIncompletes: INap[] = [];
  napOptions: any;
  napData: any;
  breastFeedLastCurrentWeek: any = {};
  breastFeedsToday: IBreastFeed[] = [];
  breastFeedTodayOptions: any;
  breastFeedTodayData: any;
  breastFeedsIncompletes: INap[] = [];
  breastFeedOptions: any;
  breastFeedData: any;
  babyProfile: IBabyProfile = {};
  d3ChartTranslate: any = {};
  @ViewChild(NvD3Component) nvD3Component: NvD3Component | undefined;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private babyProfileService: BabyProfileService,
    private napService: NapService,
    private breastFeedService: BreastFeedService,
    private translateService: TranslateService,
    protected modalService: NgbModal
  ) {}

  @HostListener('touchmove') touchmove(): void {
    d3.select(window).on('scroll', () => {
      d3.selectAll('.nvtooltip').style('opacity', '0');
    });
  }

  @HostListener('touchstart') touchstart(): void {
    d3.selectAll('.nvtooltip').style('opacity', '0');
  }

  ngOnInit(): void {
    this.translateD3Chart(false);

    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.getUserData();
      }
    });
  }

  ngAfterViewInit(): void {
    this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.changeChartLanguage();
      this.currentTodayDateFormat = dayjs(Date.now()).format('D MMM YYYY');
      this.currentTodayDateShortFormat = dayjs(Date.now()).format('D MMM');
    });
    this.translateService.onTranslationChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.changeChartLanguage();
    });
  }

  translateD3Chart(short: boolean): void {
    this.d3ChartTranslate.monday = 'Monday';
    this.d3ChartTranslate.tuesday = 'Tuesday';
    this.d3ChartTranslate.wednesday = 'Wednesday';
    this.d3ChartTranslate.thusday = 'Thusday';
    this.d3ChartTranslate.friday = 'Friday';
    this.d3ChartTranslate.saturday = 'Saturday';
    this.d3ChartTranslate.sunday = 'Sunday';
    this.d3ChartTranslate.lastWeek = 'Last Week';
    this.d3ChartTranslate.currentWeek = 'Current Week';
    this.d3ChartTranslate.goal = 'Goal';
    this.d3ChartTranslate.dayOfWeek = 'Day of Week';
    this.d3ChartTranslate.sleepHours = 'Sleep Hours';
    this.d3ChartTranslate.averageFeedHours = 'Sleep Average Feeding Time (Hours)';
    this.d3ChartTranslate.feedingDurationHours = 'Feeding duration (Hours)';
    this.d3ChartTranslate.amountOfTimes = 'Amount of times';
    this.d3ChartTranslate.time = 'time';
    this.d3ChartTranslate.duration = 'Duration';
    this.d3ChartTranslate.start = 'Start';
    this.d3ChartTranslate.end = 'End';
    this.d3ChartTranslate.breastFeedTodayChartTitle = `Today (${dayjs(Date.now()).format('D MMM')})`;
    this.translateService.get('gatewayApp.lastWeek').subscribe((res: string) => {
      this.d3ChartTranslate.lastWeek = res;
    });
    this.translateService.get('gatewayApp.currentWeek').subscribe((res: string) => {
      this.d3ChartTranslate.currentWeek = res;
    });
    this.translateService.get('gatewayApp.goal').subscribe((res: string) => {
      this.d3ChartTranslate.goal = res;
    });
    this.translateService.get('gatewayApp.dayOfWeek').subscribe((res: string) => {
      this.d3ChartTranslate.dayOfWeek = res;
    });
    this.translateService.get('gatewayApp.sleepHours').subscribe((res: string) => {
      this.d3ChartTranslate.sleepHours = res;
    });
    this.translateService.get('gatewayApp.averageFeedHours').subscribe((res: string) => {
      this.d3ChartTranslate.averageFeedHours = res;
    });
    this.translateService.get('gatewayApp.feedingDurationHours').subscribe((res: string) => {
      this.d3ChartTranslate.feedingDurationHours = res;
    });
    this.translateService.get('gatewayApp.amountOfTimes').subscribe((res: string) => {
      this.d3ChartTranslate.amountOfTimes = res;
    });
    this.translateService.get('gatewayApp.time').subscribe((res: string) => {
      this.d3ChartTranslate.time = res;
    });
    this.translateService.get('gatewayApp.duration').subscribe((res: string) => {
      this.d3ChartTranslate.duration = res;
    });
    this.translateService.get('gatewayApp.start').subscribe((res: string) => {
      this.d3ChartTranslate.start = res;
    });
    this.translateService.get('gatewayApp.end').subscribe((res: string) => {
      this.d3ChartTranslate.end = res;
    });
    this.translateService.get('gatewayApp.breastFeedTodayChartTitle').subscribe((res: string) => {
      this.d3ChartTranslate.breastFeedTodayChartTitle = `${res} - ${dayjs(Date.now()).format('D MMM')}`;
    });
    let translateParameter = 'gatewayApp.daysOfWeek.long';
    if (short) {
      translateParameter = 'gatewayApp.daysOfWeek.short';
    }
    this.translateService.get(translateParameter + '.tuesday').subscribe((res: string) => {
      this.d3ChartTranslate.tuesday = res;
    });
    this.translateService.get(translateParameter + '.monday').subscribe((res: string) => {
      this.d3ChartTranslate.monday = res;
    });
    this.translateService.get(translateParameter + '.wednesday').subscribe((res: string) => {
      this.d3ChartTranslate.wednesday = res;
    });
    this.translateService.get(translateParameter + '.thusday').subscribe((res: string) => {
      this.d3ChartTranslate.thusday = res;
    });
    this.translateService.get(translateParameter + '.friday').subscribe((res: string) => {
      this.d3ChartTranslate.friday = res;
    });
    this.translateService.get(translateParameter + '.saturday').subscribe((res: string) => {
      this.d3ChartTranslate.saturday = res;
    });
    this.translateService.get(translateParameter + '.sunday').subscribe((res: string) => {
      this.d3ChartTranslate.sunday = res;
    });
  }

  isShowWeekNapGraphic(napLastCurrentWeek: any): boolean {
    if (Object.keys(napLastCurrentWeek).length === 0) {
      return false;
    }
    let isShow: boolean = napLastCurrentWeek.lastWeekNaps.some((item: any) => item.sleepHours > 0);
    if (!isShow) {
      isShow = napLastCurrentWeek.currentWeekNaps.some((item: any) => item.sleepHours > 0);
    }
    return isShow;
  }

  trackIncompleteNapsId(index: number, item: INap): number {
    return item.id!;
  }

  deleteIncompleteNap(nap: INap): void {
    const modalRef = this.modalService.open(NapDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.nap = nap;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.getNapData(this.babyProfile.id!);
      }
    });
  }

  isShowWeekBreastFeedGraphic(breastFeedLastCurrentWeek: any): boolean {
    if (Object.keys(breastFeedLastCurrentWeek).length === 0) {
      return false;
    }
    let isShow: boolean = breastFeedLastCurrentWeek.lastWeekBreastFeeds.some((item: any) => item.averageFeedHours > 0);
    if (!isShow) {
      isShow = breastFeedLastCurrentWeek.currentWeekBreastFeeds.some((item: any) => item.averageFeedHours > 0);
    }
    return isShow;
  }

  trackIncompleteBreastFeedsId(index: number, item: IBreastFeed): number {
    return item.id!;
  }

  deleteIncompleteBreastFeed(breastFeed: IBreastFeed): void {
    const modalRef = this.modalService.open(BreastFeedDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.breastFeed = breastFeed;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.getBreastFeedData(this.babyProfile.id!);
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private getUserData(): void {
    this.babyProfileService.query().subscribe((res: HttpResponse<IBabyProfile[]>) => {
      this.babyProfiles = res.body ?? [];
      if (this.babyProfiles.length === 1) {
        this.babyProfile = this.babyProfiles[0];
        this.getNapData(this.babyProfile.id!);
        this.getBreastFeedData(this.babyProfile.id!);
      }
    });
  }

  private getNapData(id: number): void {
    this.napService.todayNapsInHourByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.napToday = res.body;

      // calculate success, warning, or danger
      if (this.napToday.sleepHours >= this.napToday.sleepHoursGoal) {
        this.napToday.progress = 'success';
      } else if (this.napToday.sleepHours < 10) {
        this.napToday.progress = 'danger';
      } else if (this.napToday.sleepHours > 10 && this.napToday.sleepHours < this.napToday.sleepHoursGoal) {
        this.napToday.progress = 'warning';
      }
    });

    this.napService.incompleteNapsByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.napsIncompletes = res.body ?? [];
    });

    this.napService.lastWeekCurrentWeekNapsInHoursEachDayByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.napLastCurrentWeek = res.body;
      this.createNapLastWeekCurrentWeekChart();
    });
  }

  private createNapLastWeekCurrentWeekChart(): void {
    if (Object.keys(this.napLastCurrentWeek).length === 0) {
      return;
    }
    // https://stackoverflow.com/a/34694155/65681
    this.napOptions = { ...D3ChartService.getChartConfig(this.d3ChartTranslate) };
    if (this.napLastCurrentWeek.lastWeekNaps.length || this.napLastCurrentWeek.currentWeekNaps.length) {
      this.napOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dayOfWeek;
      this.napOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.sleepHours;

      const lastWeek: { x: any; y: any }[] = [],
        currentWeek: { x: any; y: any }[] = [],
        sleepHoursGoal: { x: any; y: any }[] = [];

      this.napLastCurrentWeek.lastWeekNaps.forEach((item: any) => {
        lastWeek.push({
          x: item.dayOfWeek,
          y: item.sleepHours,
        });
        sleepHoursGoal.push({
          x: item.dayOfWeek,
          y: this.napLastCurrentWeek.sleepHoursGoal,
        });
      });
      this.napLastCurrentWeek.currentWeekNaps.forEach((item: any) => {
        currentWeek.push({
          x: item.dayOfWeek,
          y: item.sleepHours,
        });
      });
      this.napData = [
        {
          values: sleepHoursGoal,
          key: this.d3ChartTranslate.goal,
          color: '#91f1a2',
        },
        {
          values: lastWeek,
          key: this.d3ChartTranslate.lastWeek,
          color: '#eb00ff',
        },
        {
          values: currentWeek,
          key: this.d3ChartTranslate.currentWeek,
          color: '#0077ff',
        },
      ];
      // set y scale to be 10 more than max and min
      this.napOptions.chart.yDomain = [0, 24];
    } else {
      this.napLastCurrentWeek.lastWeekNaps = [];
      this.napLastCurrentWeek.currentWeekNaps = [];
    }
  }

  private getBreastFeedData(id: number): void {
    this.breastFeedService.incompleteBreastFeedsByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.breastFeedsIncompletes = res.body ?? [];
    });

    this.breastFeedService.todayBreastFeedsByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.breastFeedsToday = res.body ?? [];
      this.createBreastFeedsTodayChart();
    });

    this.breastFeedService.lastWeekCurrentWeekAverageBreastFeedsInHoursEachDayByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.breastFeedLastCurrentWeek = res.body;
      this.createBreastFeedLastWeekCurrentWeekChart();
    });
  }

  private createBreastFeedLastWeekCurrentWeekChart(): void {
    if (Object.keys(this.breastFeedLastCurrentWeek).length === 0) {
      return;
    }
    // https://stackoverflow.com/a/34694155/65681
    this.breastFeedOptions = { ...D3ChartService.getChartConfig(this.d3ChartTranslate) };
    if (this.breastFeedLastCurrentWeek.lastWeekBreastFeeds.length || this.breastFeedLastCurrentWeek.currentWeekBreastFeeds.length) {
      this.breastFeedOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dayOfWeek;
      this.breastFeedOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.averageFeedHours;
      this.breastFeedOptions.chart.yAxis.axisLabelDistance = -20;

      const lastWeek: { x: any; y: any }[] = [],
        currentWeek: { x: any; y: any }[] = [],
        upperValues: any[] = [];
      upperValues.push(1);

      this.breastFeedLastCurrentWeek.lastWeekBreastFeeds.forEach((item: any) => {
        lastWeek.push({
          x: item.dayOfWeek,
          y: item.averageFeedHours,
        });

        upperValues.push(item.averageFeedHours);
      });
      this.breastFeedLastCurrentWeek.currentWeekBreastFeeds.forEach((item: any) => {
        currentWeek.push({
          x: item.dayOfWeek,
          y: item.averageFeedHours,
        });
        upperValues.push(item.averageFeedHours);
      });
      this.breastFeedData = [
        {
          values: lastWeek,
          key: this.d3ChartTranslate.lastWeek,
          color: '#eb00ff',
        },
        {
          values: currentWeek,
          key: this.d3ChartTranslate.currentWeek,
          color: '#0077ff',
        },
      ];
      // set y scale to be 10 more than max and min
      this.breastFeedOptions.chart.yDomain = [0, Math.ceil(Math.max(...upperValues))];
    } else {
      this.breastFeedLastCurrentWeek.lastWeekBreastFeeds = [];
      this.breastFeedLastCurrentWeek.currentWeekBreastFeeds = [];
    }
  }

  private createBreastFeedsTodayChart(): void {
    if (this.breastFeedsToday.length === 0) {
      return;
    }
    // https://stackoverflow.com/a/34694155/65681
    this.breastFeedTodayOptions = { ...D3ChartService.getDiscreteBarChartConfig(this.d3ChartTranslate) };
    if (this.breastFeedsToday.length > 0) {
      this.breastFeedTodayOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.amountOfTimes;
      this.breastFeedTodayOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.feedingDurationHours;

      const breastFeedsTimesHours: { breastFeed: any; label: any; value: any }[] = [],
        upperValuesY: any[] = [];
      upperValuesY.push(1);

      let countBreastFeeds = 1;
      const suffix = 'º';
      this.breastFeedsToday.forEach((item: any) => {
        if (item.end !== undefined) {
          const diffInHours = this.diffHourMaxOneDecimalPlaces(item.start, item.end);
          breastFeedsTimesHours.push({
            breastFeed: item,
            label: `${countBreastFeeds}${suffix}`,
            value: diffInHours,
          });
          upperValuesY.push(diffInHours);
          countBreastFeeds++;
        }
      });
      this.breastFeedTodayData = [
        {
          values: breastFeedsTimesHours,
          key: '',
        },
      ];
      // set y scale to be 10 more than max and min
      this.breastFeedTodayOptions.chart.yDomain = [0, Math.ceil(Math.max(...upperValuesY)).toFixed(0)];
      this.breastFeedTodayOptions.chart.xDomain = null;
    } else {
      this.breastFeedsToday = [];
    }
  }

  private diffHourMaxOneDecimalPlaces(start: dayjs.Dayjs, end: dayjs.Dayjs): string {
    const diff = end.diff(start, 'hour', true);
    if (diff % 1 === 0) {
      return diff.toString();
    }
    return diff.toFixed(1);
  }

  private diffHoursAndMinutes(start: dayjs.Dayjs, end: dayjs.Dayjs): string {
    const diffTotalMinutes = end.diff(start, 'minute', true);
    const diffHours = (diffTotalMinutes / 60).toFixed(0);
    const diffMinutes = diffTotalMinutes % 60;
    return `${diffHours}:${diffMinutes}`;
  }

  private changeChartLanguage(): void {
    this.translateD3Chart(false);
    this.createNapLastWeekCurrentWeekChart();
    this.createBreastFeedLastWeekCurrentWeekChart();
    this.createBreastFeedsTodayChart();
    if (this.nvD3Component !== undefined) {
      this.nvD3Component.chart.update();
    }
  }
}
