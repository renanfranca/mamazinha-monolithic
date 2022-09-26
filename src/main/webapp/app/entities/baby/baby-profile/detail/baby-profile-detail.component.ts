import { HttpResponse } from '@angular/common/http';
import { AfterViewInit, Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AccountService } from 'app/core/auth/account.service';
import { DataUtils } from 'app/core/util/data-util.service';
import { HeightService } from 'app/entities/baby/height/service/height.service';
import { HumorHistoryService } from 'app/entities/baby/humor-history/service/humor-history.service';
import { NapService } from 'app/entities/baby/nap/service/nap.service';
import { WeightService } from 'app/entities/baby/weight/service/weight.service';
import { IWeight } from 'app/entities/baby/weight/weight.model';
import { D3ChartService } from 'app/shared/d3-chart.service';
import dayjs from 'dayjs';
import { NvD3Component } from 'ng2-nvd3';
import { IBabyProfile } from '../baby-profile.model';

@Component({
  selector: 'jhi-baby-profile-detail',
  templateUrl: './baby-profile-detail.component.html',
})
export class BabyProfileDetailComponent implements OnInit, AfterViewInit {
  babyProfile: IBabyProfile | null = null;
  currentTodayDateShortFormat = dayjs(Date.now()).format('D MMM');
  humorAverageNap?: any;
  averageNapHumorLastCurrentWeek: any = {};
  averageNapHumorOptions?: any;
  averageNapHumorData?: any;
  humorAverageHumorHistory?: any;
  averageHumorHistoryLastCurrentWeek: any = {};
  averageHumorHistoryOptions?: any;
  averageHumorHistoryData?: any;
  lastWeightsDaysAgo: IWeight[] = [];
  lastWeightsDaysAgoOptions?: any;
  lastWeightsDaysAgoData?: any;
  hideLastWeightsDaysAgoGraphicIcon = false;
  lastHeightsDaysAgo: any = {};
  lastHeightsDaysAgoOptions?: any;
  lastHeightsDaysAgoData?: any;
  hideLastHeightsDaysAgoGraphicIcon = false;
  latestWeight?: any;
  latestHeight?: any;
  favoriteNapPlace?: any;
  d3ChartTranslate: any = {};
  graphicLoading = false;
  @ViewChild(NvD3Component) nvD3Component: NvD3Component | undefined;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected napService: NapService,
    protected humorHistoryService: HumorHistoryService,
    protected weightService: WeightService,
    protected heightService: HeightService,
    private translateService: TranslateService
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

    this.activatedRoute.data.subscribe(({ babyProfile }) => {
      this.babyProfile = babyProfile;
      this.getUserData(this.babyProfile!.id!);
    });
  }

  ngAfterViewInit(): void {
    this.translateService.onLangChange.pipe().subscribe(() => {
      this.changeChartLanguage();
      this.currentTodayDateShortFormat = dayjs(Date.now()).format('D MMM');
    });
    this.translateService.onTranslationChange.pipe().subscribe(() => {
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
    this.d3ChartTranslate.dayOfWeek = 'Day of Week';
    this.d3ChartTranslate.averageNapHumor = 'Average humor after nap';
    this.d3ChartTranslate.averageHumorHistory = 'Average humor history';
    this.d3ChartTranslate.weight = 'Weight (kg)';
    this.d3ChartTranslate.height = 'Height (cm)';
    this.d3ChartTranslate.dates = 'Dates';
    this.d3ChartTranslate.last30Days = 'Last 30 Days';
    this.d3ChartTranslate.angry = 'angry';
    this.d3ChartTranslate.sad = 'sad';
    this.d3ChartTranslate.calm = 'calm';
    this.d3ChartTranslate.happy = 'happy';
    this.d3ChartTranslate.excited = 'excited';
    this.translateService.get('gatewayApp.lastWeek').subscribe((res: string) => {
      this.d3ChartTranslate.lastWeek = res;
    });
    this.translateService.get('gatewayApp.currentWeek').subscribe((res: string) => {
      this.d3ChartTranslate.currentWeek = res;
    });
    this.translateService.get('gatewayApp.dayOfWeek').subscribe((res: string) => {
      this.d3ChartTranslate.dayOfWeek = res;
    });
    this.translateService.get('gatewayApp.averageNapHumor').subscribe((res: string) => {
      this.d3ChartTranslate.averageNapHumor = res;
    });
    this.translateService.get('gatewayApp.averageHumorHistory').subscribe((res: string) => {
      this.d3ChartTranslate.averageHumorHistory = res;
    });
    this.translateService.get('gatewayApp.weight').subscribe((res: string) => {
      this.d3ChartTranslate.weight = res;
    });
    this.translateService.get('gatewayApp.height').subscribe((res: string) => {
      this.d3ChartTranslate.height = res;
    });
    this.translateService.get('gatewayApp.dates').subscribe((res: string) => {
      this.d3ChartTranslate.dates = res;
    });
    this.translateService.get('gatewayApp.last30Days').subscribe((res: string) => {
      this.d3ChartTranslate.last30Days = res;
    });
    this.translateService.get('gatewayApp.angry').subscribe((res: string) => {
      this.d3ChartTranslate.angry = res;
    });
    this.translateService.get('gatewayApp.sad').subscribe((res: string) => {
      this.d3ChartTranslate.sad = res;
    });
    this.translateService.get('gatewayApp.calm').subscribe((res: string) => {
      this.d3ChartTranslate.calm = res;
    });
    this.translateService.get('gatewayApp.happy').subscribe((res: string) => {
      this.d3ChartTranslate.happy = res;
    });
    this.translateService.get('gatewayApp.excited').subscribe((res: string) => {
      this.d3ChartTranslate.excited = res;
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

  isShowWeekAverageNapHumorGraphic(averageNapHumorLastCurrentWeek: any): boolean {
    if (Object.keys(averageNapHumorLastCurrentWeek).length === 0) {
      return false;
    }
    let isShow: boolean = averageNapHumorLastCurrentWeek.lastWeekHumorAverage.some((item: any) => item.humorAverage > 0);
    if (!isShow) {
      isShow = averageNapHumorLastCurrentWeek.currentWeekHumorAverage.some((item: any) => item.humorAverage > 0);
    }
    return isShow;
  }

  showHideWeekAverageNapHumorGraphic(): void {
    if (this.isShowWeekAverageNapHumorGraphic(this.averageNapHumorLastCurrentWeek)) {
      this.averageNapHumorLastCurrentWeek = {};
      this.createAverageNapsHumorLastWeekCurrentWeekChart();
    } else {
      this.graphicLoading = true;
      this.napService.lastWeekCurrentWeekAverageNapsHumorEachDayByBabyProfile(this.babyProfile!.id!).subscribe((res: HttpResponse<any>) => {
        this.averageNapHumorLastCurrentWeek = res.body;
        this.createAverageNapsHumorLastWeekCurrentWeekChart();
        this.graphicLoading = false;
      });
    }
  }

  isShowWeekAverageHumorHistoryGraphic(averageHumorHistoryLastCurrentWeek: any): boolean {
    if (Object.keys(averageHumorHistoryLastCurrentWeek).length === 0) {
      return false;
    }
    let isShow: boolean = averageHumorHistoryLastCurrentWeek.lastWeekHumorAverage.some((item: any) => item.humorAverage > 0);
    if (!isShow) {
      isShow = averageHumorHistoryLastCurrentWeek.currentWeekHumorAverage.some((item: any) => item.humorAverage > 0);
    }
    return isShow;
  }

  showHideWeekAverageHumorHistoryGraphic(): void {
    if (this.isShowWeekAverageHumorHistoryGraphic(this.averageHumorHistoryLastCurrentWeek)) {
      this.averageHumorHistoryLastCurrentWeek = {};
      this.createAverageHumorHistoryLastWeekCurrentWeekChart();
    } else {
      this.graphicLoading = true;
      this.humorHistoryService
        .lastWeekCurrentWeekAverageHumorHistoryEachDayByBabyProfile(this.babyProfile!.id!)
        .subscribe((res: HttpResponse<any>) => {
          this.averageHumorHistoryLastCurrentWeek = res.body;
          this.createAverageHumorHistoryLastWeekCurrentWeekChart();
          this.graphicLoading = false;
        });
    }
  }

  isShowLastWeightsDaysAgoGraphic(lastWeightsDaysAgo: IWeight[]): boolean {
    return lastWeightsDaysAgo.length > 1;
  }

  showHideLastWeightsDaysAgoGraphic(): void {
    if (this.isShowLastWeightsDaysAgoGraphic(this.lastWeightsDaysAgo)) {
      this.lastWeightsDaysAgo = [];
      this.createLastWeightsDaysAgo();
    } else {
      this.graphicLoading = true;
      this.weightService.lastWeightsByDaysByBabyProfile(this.babyProfile!.id!, 30).subscribe((res: HttpResponse<any>) => {
        this.lastWeightsDaysAgo = res.body;
        this.createLastWeightsDaysAgo();
        this.graphicLoading = false;
      });
    }
  }

  isShowLastHeightsDaysAgoGraphic(lastHeightsDaysAgo: any): boolean {
    return Object.keys(lastHeightsDaysAgo).length > 1;
  }

  showHideLastHeightsDaysAgoGraphic(): void {
    if (this.isShowLastHeightsDaysAgoGraphic(this.lastHeightsDaysAgo)) {
      this.lastHeightsDaysAgo = {};
      this.createLastHeightsDaysAgo();
    } else {
      this.graphicLoading = true;
      this.heightService.lastHeightsByDaysByBabyProfile(this.babyProfile!.id!, 30).subscribe((res: HttpResponse<any>) => {
        this.lastHeightsDaysAgo = res.body;
        this.createLastHeightsDaysAgo();
        this.graphicLoading = false;
      });
    }
  }

  getUserData(id: number): void {
    this.getNapData(id);
    this.getHumorHistoryData(id);
    this.getWeightData(id);
    this.getHeightData(id);
  }

  getNapData(id: number): void {
    this.napService.todayAverageNapHumorByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.humorAverageNap = res.body;
    });
    this.napService.favoriteNapPlaceFromLastDaysByBabyProfile(id, 30).subscribe((res: HttpResponse<any>) => {
      this.favoriteNapPlace = res.body;
    });
  }

  getHumorHistoryData(id: number): void {
    this.humorHistoryService.todayAverageHumorHistoryByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.humorAverageHumorHistory = res.body;
    });
  }

  getWeightData(id: number): void {
    this.weightService.latestWeightByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.latestWeight = res.body;
    });
    this.weightService.lastWeightsByDaysByBabyProfile(id, 30).subscribe((res: HttpResponse<any>) => {
      const weights = res.body;
      this.hideLastWeightsDaysAgoGraphicIcon = weights.length <= 1;
    });
  }

  getHeightData(id: number): void {
    this.heightService.latestHeightByBabyProfile(id).subscribe((res: HttpResponse<any>) => {
      this.latestHeight = res.body;
    });
    this.heightService.lastHeightsByDaysByBabyProfile(id, 30).subscribe((res: HttpResponse<any>) => {
      const heights = res.body;
      this.hideLastHeightsDaysAgoGraphicIcon = heights.length <= 1;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  private createAverageNapsHumorLastWeekCurrentWeekChart(): void {
    if (Object.keys(this.averageNapHumorLastCurrentWeek).length === 0) {
      return;
    }
    // https://stackoverflow.com/a/34694155/65681
    this.averageNapHumorOptions = { ...D3ChartService.getHumorChartConfig(this.d3ChartTranslate) };
    if (
      this.averageNapHumorLastCurrentWeek.lastWeekHumorAverage.length ||
      this.averageNapHumorLastCurrentWeek.currentWeekHumorAverage.length
    ) {
      this.averageNapHumorOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dayOfWeek;
      this.averageNapHumorOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.averageNapHumor;

      const lastWeek: { x: any; y: any }[] = [],
        currentWeek: { x: any; y: any }[] = [];

      this.averageNapHumorLastCurrentWeek.lastWeekHumorAverage.forEach((item: any) => {
        lastWeek.push({
          x: item.dayOfWeek,
          y: item.humorAverage,
        });
      });
      this.averageNapHumorLastCurrentWeek.currentWeekHumorAverage.forEach((item: any) => {
        currentWeek.push({
          x: item.dayOfWeek,
          y: item.humorAverage,
        });
      });
      this.averageNapHumorData = [
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
      this.averageNapHumorOptions.chart.yDomain = [0, 5];
    } else {
      this.averageNapHumorLastCurrentWeek.lastWeekHumorAverage = [];
      this.averageNapHumorLastCurrentWeek.currentWeekHumorAverage = [];
    }
  }

  private createAverageHumorHistoryLastWeekCurrentWeekChart(): void {
    if (Object.keys(this.averageHumorHistoryLastCurrentWeek).length === 0) {
      return;
    }
    // https://stackoverflow.com/a/34694155/65681
    this.averageHumorHistoryOptions = { ...D3ChartService.getHumorChartConfig(this.d3ChartTranslate) };
    if (
      this.averageHumorHistoryLastCurrentWeek.lastWeekHumorAverage.length ||
      this.averageHumorHistoryLastCurrentWeek.currentWeekHumorAverage.length
    ) {
      this.averageHumorHistoryOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dayOfWeek;
      this.averageHumorHistoryOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.averageHumorHistory;

      const lastWeek: { x: any; y: any }[] = [],
        currentWeek: { x: any; y: any }[] = [];

      this.averageHumorHistoryLastCurrentWeek.lastWeekHumorAverage.forEach((item: any) => {
        lastWeek.push({
          x: item.dayOfWeek,
          y: item.humorAverage,
        });
      });
      this.averageHumorHistoryLastCurrentWeek.currentWeekHumorAverage.forEach((item: any) => {
        currentWeek.push({
          x: item.dayOfWeek,
          y: item.humorAverage,
        });
      });
      this.averageHumorHistoryData = [
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
      this.averageHumorHistoryOptions.chart.yDomain = [0, 5];
    } else {
      this.averageHumorHistoryLastCurrentWeek.lastWeekHumorAverage = [];
      this.averageHumorHistoryLastCurrentWeek.currentWeekHumorAverage = [];
    }
  }

  private createLastWeightsDaysAgo(): void {
    if (Object.keys(this.lastWeightsDaysAgo).length > 1) {
      this.lastWeightsDaysAgoOptions = { ...D3ChartService.getWeightHeightChartConfig() };
      this.lastWeightsDaysAgoOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dates;
      this.lastWeightsDaysAgoOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.weight;
      const weightValues: { x: any; y: any }[] = [];
      const yValues: any[] = [];
      this.lastWeightsDaysAgo.forEach((item: any) => {
        weightValues.push({
          x: item.date.valueOf(),
          y: item.value,
        });
        yValues.push(item.value);
      });
      this.lastWeightsDaysAgoData = [
        {
          values: weightValues,
          key: this.d3ChartTranslate.weight,
          color: 'rgb(252 145 78)',
          area: true,
        },
      ];
      this.lastWeightsDaysAgoOptions.chart.yDomain = [0, Math.ceil(Math.max(...yValues))];
    }
  }

  private createLastHeightsDaysAgo(): void {
    if (Object.keys(this.lastHeightsDaysAgo).length > 1) {
      this.lastHeightsDaysAgoOptions = { ...D3ChartService.getWeightHeightChartConfig() };
      this.lastHeightsDaysAgoOptions.chart.xAxis.axisLabel = this.d3ChartTranslate.dates;
      this.lastHeightsDaysAgoOptions.chart.yAxis.axisLabel = this.d3ChartTranslate.height;
      const heightValues: { x: any; y: any }[] = [];
      const yValues: any[] = [];
      this.lastHeightsDaysAgo.forEach((item: any) => {
        heightValues.push({
          x: item.date.valueOf(),
          y: item.value,
        });
        yValues.push(item.value);
      });
      this.lastHeightsDaysAgoData = [
        {
          values: heightValues,
          key: this.d3ChartTranslate.height,
          color: 'rgb(153 255 81)',
          area: true,
        },
      ];
      this.lastHeightsDaysAgoOptions.chart.yDomain = [0, Math.ceil(Math.max(...yValues))];
    }
  }

  private changeChartLanguage(): void {
    this.translateD3Chart(false);
    this.createAverageNapsHumorLastWeekCurrentWeekChart();
    this.createAverageHumorHistoryLastWeekCurrentWeekChart();
    this.createLastWeightsDaysAgo();
    this.createLastHeightsDaysAgo();
    if (this.nvD3Component !== undefined) {
      this.nvD3Component.chart.update();
    }
  }
}
