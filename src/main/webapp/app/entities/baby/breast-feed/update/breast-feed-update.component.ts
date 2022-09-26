import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby/baby-profile/service/baby-profile.service';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { BreastFeed, IBreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';

@Component({
  selector: 'jhi-breast-feed-update',
  templateUrl: './breast-feed-update.component.html',
})
export class BreastFeedUpdateComponent implements OnInit {
  isSaving = false;
  dateError = false;

  babyProfilesSharedCollection: IBabyProfile[] = [];

  editForm = this.fb.group({
    id: [],
    start: [null, [Validators.required]],
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
      const today = dayjs(Date.now());
      if (breastFeed.id === undefined) {
        breastFeed.start = today;
      } else if (breastFeed.end === undefined) {
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
    const breastFeed = this.createFromForm();
    if (!this.validate(breastFeed)) {
      return;
    }
    this.isSaving = true;
    if (breastFeed.id !== undefined) {
      this.subscribeToSaveResponse(this.breastFeedService.update(breastFeed));
    } else {
      this.subscribeToSaveResponse(this.breastFeedService.create(breastFeed));
    }
  }

  validate(breastFeed: IBreastFeed): boolean {
    this.dateError = false;
    if (breastFeed.start && breastFeed.end && breastFeed.start.isAfter(breastFeed.end)) {
      this.dateError = true;
      return false;
    }
    return true;
  }

  trackBabyProfileById(index: number, item: IBabyProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBreastFeed>>): void {
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
      .subscribe((babyProfiles: IBabyProfile[]) => {
        this.babyProfilesSharedCollection = babyProfiles;
        if (babyProfiles.length === 1) {
          this.editForm.patchValue({
            babyProfile: babyProfiles[0],
          });
        }
      });
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
