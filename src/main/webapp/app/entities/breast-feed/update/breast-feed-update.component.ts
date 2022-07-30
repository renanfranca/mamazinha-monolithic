import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IBreastFeed, BreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';
import { Pain } from 'app/entities/enumerations/pain.model';

@Component({
  selector: 'jhi-breast-feed-update',
  templateUrl: './breast-feed-update.component.html',
})
export class BreastFeedUpdateComponent implements OnInit {
  isSaving = false;
  painValues = Object.keys(Pain);

  babyProfilesSharedCollection: IBabyProfile[] = [];

  editForm = this.fb.group({
    id: [],
    start: [],
    end: [],
    pain: [],
    babyProfile: [],
  });

  constructor(
    protected breastFeedService: BreastFeedService,
    protected babyProfileService: BabyProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ breastFeed }) => {
      if (breastFeed.id === undefined) {
        const today = dayjs().startOf('day');
        breastFeed.start = today;
        breastFeed.end = today;
      }

      this.updateForm(breastFeed);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const breastFeed = this.createFromForm();
    if (breastFeed.id !== undefined) {
      this.subscribeToSaveResponse(this.breastFeedService.update(breastFeed));
    } else {
      this.subscribeToSaveResponse(this.breastFeedService.create(breastFeed));
    }
  }

  trackBabyProfileById(_index: number, item: IBabyProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBreastFeed>>): void {
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

  protected updateForm(breastFeed: IBreastFeed): void {
    this.editForm.patchValue({
      id: breastFeed.id,
      start: breastFeed.start ? breastFeed.start.format(DATE_TIME_FORMAT) : null,
      end: breastFeed.end ? breastFeed.end.format(DATE_TIME_FORMAT) : null,
      pain: breastFeed.pain,
      babyProfile: breastFeed.babyProfile,
    });

    this.babyProfilesSharedCollection = this.babyProfileService.addBabyProfileToCollectionIfMissing(
      this.babyProfilesSharedCollection,
      breastFeed.babyProfile
    );
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
  }

  protected createFromForm(): IBreastFeed {
    return {
      ...new BreastFeed(),
      id: this.editForm.get(['id'])!.value,
      start: this.editForm.get(['start'])!.value ? dayjs(this.editForm.get(['start'])!.value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end'])!.value ? dayjs(this.editForm.get(['end'])!.value, DATE_TIME_FORMAT) : undefined,
      pain: this.editForm.get(['pain'])!.value,
      babyProfile: this.editForm.get(['babyProfile'])!.value,
    };
  }
}
