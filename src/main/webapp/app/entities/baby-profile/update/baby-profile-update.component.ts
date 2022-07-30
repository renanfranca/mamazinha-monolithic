import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IBabyProfile, BabyProfile } from '../baby-profile.model';
import { BabyProfileService } from '../service/baby-profile.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-baby-profile-update',
  templateUrl: './baby-profile-update.component.html',
})
export class BabyProfileUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    picture: [],
    pictureContentType: [],
    birthday: [null, [Validators.required]],
    sign: [],
    main: [],
    userId: [null, [Validators.required]],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected babyProfileService: BabyProfileService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ babyProfile }) => {
      if (babyProfile.id === undefined) {
        const today = dayjs().startOf('day');
        babyProfile.birthday = today;
      }

      this.updateForm(babyProfile);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('babyApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const babyProfile = this.createFromForm();
    if (babyProfile.id !== undefined) {
      this.subscribeToSaveResponse(this.babyProfileService.update(babyProfile));
    } else {
      this.subscribeToSaveResponse(this.babyProfileService.create(babyProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBabyProfile>>): void {
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

  protected updateForm(babyProfile: IBabyProfile): void {
    this.editForm.patchValue({
      id: babyProfile.id,
      name: babyProfile.name,
      picture: babyProfile.picture,
      pictureContentType: babyProfile.pictureContentType,
      birthday: babyProfile.birthday ? babyProfile.birthday.format(DATE_TIME_FORMAT) : null,
      sign: babyProfile.sign,
      main: babyProfile.main,
      userId: babyProfile.userId,
    });
  }

  protected createFromForm(): IBabyProfile {
    return {
      ...new BabyProfile(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      pictureContentType: this.editForm.get(['pictureContentType'])!.value,
      picture: this.editForm.get(['picture'])!.value,
      birthday: this.editForm.get(['birthday'])!.value ? dayjs(this.editForm.get(['birthday'])!.value, DATE_TIME_FORMAT) : undefined,
      sign: this.editForm.get(['sign'])!.value,
      main: this.editForm.get(['main'])!.value,
      userId: this.editForm.get(['userId'])!.value,
    };
  }
}
