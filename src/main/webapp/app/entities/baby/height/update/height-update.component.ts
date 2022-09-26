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
import { Height, IHeight } from '../height.model';
import { HeightService } from '../service/height.service';

@Component({
  selector: 'jhi-height-update',
  templateUrl: './height-update.component.html',
})
export class HeightUpdateComponent implements OnInit {
  isSaving = false;

  babyProfilesSharedCollection: IBabyProfile[] = [];

  editForm = this.fb.group({
    id: [],
    value: [null, [Validators.required]],
    date: [null, [Validators.required]],
    babyProfile: [null, [Validators.required]],
  });

  constructor(
    protected heightService: HeightService,
    protected babyProfileService: BabyProfileService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ height }) => {
      if (height.id === undefined) {
        const today = dayjs(Date.now());
        height.date = today;
      }

      this.updateForm(height);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const height = this.createFromForm();
    if (height.id !== undefined) {
      this.subscribeToSaveResponse(this.heightService.update(height));
    } else {
      this.subscribeToSaveResponse(this.heightService.create(height));
    }
  }

  trackBabyProfileById(index: number, item: IBabyProfile): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHeight>>): void {
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

  protected updateForm(height: IHeight): void {
    this.editForm.patchValue({
      id: height.id,
      value: height.value,
      date: height.date ? height.date.format(DATE_TIME_FORMAT) : null,
      babyProfile: height.babyProfile,
    });

    this.babyProfilesSharedCollection = this.babyProfileService.addBabyProfileToCollectionIfMissing(
      this.babyProfilesSharedCollection,
      height.babyProfile
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

  protected createFromForm(): IHeight {
    return {
      ...new Height(),
      id: this.editForm.get(['id'])!.value,
      value: this.editForm.get(['value'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      babyProfile: this.editForm.get(['babyProfile'])!.value,
    };
  }
}
