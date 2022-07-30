import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IHumorHistory, HumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';
import { IHumor } from 'app/entities/humor/humor.model';
import { HumorService } from 'app/entities/humor/service/humor.service';

@Component({
  selector: 'jhi-humor-history-update',
  templateUrl: './humor-history-update.component.html',
})
export class HumorHistoryUpdateComponent implements OnInit {
  isSaving = false;

  babyProfilesSharedCollection: IBabyProfile[] = [];
  humorsSharedCollection: IHumor[] = [];

  editForm = this.fb.group({
    id: [],
    date: [],
    babyProfile: [],
    humor: [],
  });

  constructor(
    protected humorHistoryService: HumorHistoryService,
    protected babyProfileService: BabyProfileService,
    protected humorService: HumorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ humorHistory }) => {
      if (humorHistory.id === undefined) {
        const today = dayjs().startOf('day');
        humorHistory.date = today;
      }

      this.updateForm(humorHistory);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const humorHistory = this.createFromForm();
    if (humorHistory.id !== undefined) {
      this.subscribeToSaveResponse(this.humorHistoryService.update(humorHistory));
    } else {
      this.subscribeToSaveResponse(this.humorHistoryService.create(humorHistory));
    }
  }

  trackBabyProfileById(_index: number, item: IBabyProfile): number {
    return item.id!;
  }

  trackHumorById(_index: number, item: IHumor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHumorHistory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(humorHistory: IHumorHistory): void {
    this.editForm.patchValue({
      id: humorHistory.id,
      date: humorHistory.date ? humorHistory.date.format(DATE_TIME_FORMAT) : null,
      babyProfile: humorHistory.babyProfile,
      humor: humorHistory.humor,
    });

    this.babyProfilesSharedCollection = this.babyProfileService.addBabyProfileToCollectionIfMissing(
      this.babyProfilesSharedCollection,
      humorHistory.babyProfile
    );
    this.humorsSharedCollection = this.humorService.addHumorToCollectionIfMissing(this.humorsSharedCollection, humorHistory.humor);
  }

  protected loadRelationshipsOptions(): void {
    this.babyProfileService
      .query()
      .pipe(map((res: HttpResponse<IBabyProfile[]>) => res.body ?? []))
      .pipe(
        map((babyProfiles: IBabyProfile[]) =>
          this.babyProfileService.addBabyProfileToCollectionIfMissing(babyProfiles, this.editForm.get('babyProfile')!.value)
        )
      )
      .subscribe((babyProfiles: IBabyProfile[]) => (this.babyProfilesSharedCollection = babyProfiles));

    this.humorService
      .query()
      .pipe(map((res: HttpResponse<IHumor[]>) => res.body ?? []))
      .pipe(map((humors: IHumor[]) => this.humorService.addHumorToCollectionIfMissing(humors, this.editForm.get('humor')!.value)))
      .subscribe((humors: IHumor[]) => (this.humorsSharedCollection = humors));
  }

  protected createFromForm(): IHumorHistory {
    return {
      ...new HumorHistory(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      babyProfile: this.editForm.get(['babyProfile'])!.value,
      humor: this.editForm.get(['humor'])!.value,
    };
  }
}
