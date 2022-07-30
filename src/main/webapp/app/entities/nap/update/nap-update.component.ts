import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { INap, Nap } from '../nap.model';
import { NapService } from '../service/nap.service';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';
import { IHumor } from 'app/entities/humor/humor.model';
import { HumorService } from 'app/entities/humor/service/humor.service';
import { Place } from 'app/entities/enumerations/place.model';

@Component({
  selector: 'jhi-nap-update',
  templateUrl: './nap-update.component.html',
})
export class NapUpdateComponent implements OnInit {
  isSaving = false;
  placeValues = Object.keys(Place);

  babyProfilesSharedCollection: IBabyProfile[] = [];
  humorsSharedCollection: IHumor[] = [];

  editForm = this.fb.group({
    id: [],
    start: [],
    end: [],
    place: [],
    babyProfile: [],
    humor: [],
  });

  constructor(
    protected napService: NapService,
    protected babyProfileService: BabyProfileService,
    protected humorService: HumorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nap }) => {
      if (nap.id === undefined) {
        const today = dayjs().startOf('day');
        nap.start = today;
        nap.end = today;
      }

      this.updateForm(nap);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const nap = this.createFromForm();
    if (nap.id !== undefined) {
      this.subscribeToSaveResponse(this.napService.update(nap));
    } else {
      this.subscribeToSaveResponse(this.napService.create(nap));
    }
  }

  trackBabyProfileById(_index: number, item: IBabyProfile): number {
    return item.id!;
  }

  trackHumorById(_index: number, item: IHumor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INap>>): void {
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

  protected updateForm(nap: INap): void {
    this.editForm.patchValue({
      id: nap.id,
      start: nap.start ? nap.start.format(DATE_TIME_FORMAT) : null,
      end: nap.end ? nap.end.format(DATE_TIME_FORMAT) : null,
      place: nap.place,
      babyProfile: nap.babyProfile,
      humor: nap.humor,
    });

    this.babyProfilesSharedCollection = this.babyProfileService.addBabyProfileToCollectionIfMissing(
      this.babyProfilesSharedCollection,
      nap.babyProfile
    );
    this.humorsSharedCollection = this.humorService.addHumorToCollectionIfMissing(this.humorsSharedCollection, nap.humor);
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

  protected createFromForm(): INap {
    return {
      ...new Nap(),
      id: this.editForm.get(['id'])!.value,
      start: this.editForm.get(['start'])!.value ? dayjs(this.editForm.get(['start'])!.value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end'])!.value ? dayjs(this.editForm.get(['end'])!.value, DATE_TIME_FORMAT) : undefined,
      place: this.editForm.get(['place'])!.value,
      babyProfile: this.editForm.get(['babyProfile'])!.value,
      humor: this.editForm.get(['humor'])!.value,
    };
  }
}
