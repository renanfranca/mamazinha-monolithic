import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby/baby-profile/service/baby-profile.service';
import { IHumor } from 'app/entities/baby/humor/humor.model';
import { HumorService } from 'app/entities/baby/humor/service/humor.service';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { INap, Nap } from '../nap.model';
import { NapService } from '../service/nap.service';

@Component({
  selector: 'jhi-nap-update',
  templateUrl: './nap-update.component.html',
})
export class NapUpdateComponent implements OnInit {
  isSaving = false;
  dateError = false;

  babyProfilesSharedCollection: IBabyProfile[] = [];
  humorsSharedCollection: IHumor[] = [];

  editForm = this.fb.group({
    id: [],
    start: [null, [Validators.required]],
    end: [],
    place: [],
    babyProfile: [null, [Validators.required]],
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
      const today = dayjs(Date.now());
      if (nap.id === undefined) {
        nap.start = today;
      } else if (nap.end === undefined) {
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
    const nap = this.createFromForm();
    if (!this.validate(nap)) {
      return;
    }
    this.isSaving = true;
    if (nap.id !== undefined) {
      this.subscribeToSaveResponse(this.napService.update(nap));
    } else {
      this.subscribeToSaveResponse(this.napService.create(nap));
    }
  }

  validate(nap: INap): boolean {
    this.dateError = false;
    if (nap.start && nap.end && nap.start.isAfter(nap.end)) {
      this.dateError = true;
      return false;
    }
    return true;
  }

  trackBabyProfileById(index: number, item: IBabyProfile): number {
    return item.id!;
  }

  trackHumorById(index: number, item: IHumor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INap>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
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
      .subscribe((babyProfiles: IBabyProfile[]) => {
        this.babyProfilesSharedCollection = babyProfiles;
        if (babyProfiles.length === 1) {
          this.editForm.patchValue({
            babyProfile: babyProfiles[0],
          });
        }
      });

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
